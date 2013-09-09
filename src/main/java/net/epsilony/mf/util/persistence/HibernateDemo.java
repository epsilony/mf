/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Entity
public class HibernateDemo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    static final String typeName = Coord3DType.class.getName();
    @Basic
    String name;
    @Columns(columns = {
        @Column(name = "x"),
        @Column(name = "y"),
        @Column(name = "z")})
    @Type(type = "net.epsilony.mf.util.persistence.Coord3DType")
    double[] doubles;

    public HibernateDemo(String name, double[] value) {

        this.name = name;
        this.doubles = value;
    }

    public HibernateDemo() {
    }

    public static void main(String[] args) {
        Configuration conf = new Configuration();

        conf.configure();
        //!must add prefix hibernate before any property names
        conf.setProperty("hibernate.connection.url", "jdbc:sqlite:target/hibernate_demo.sqlite");
        conf.setProperty("hibernate.hbm2ddl.auto", "create");


        ServiceRegistry buildServiceRegistry = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory factory = conf.buildSessionFactory(buildServiceRegistry);

        Session session = factory.openSession();
        double[][] values = new double[][]{
            {0.5, 1.13, 12},
            {0.23},
            {0.24, 0.15},
            null
        };
        HibernateDemo[] items = new HibernateDemo[]{
            new HibernateDemo("3d", values[0]),
            new HibernateDemo("1d", values[1]),
            new HibernateDemo("2d", values[2]),
            new HibernateDemo("null_", values[3])
        };
        session.beginTransaction();
        for (HibernateDemo item : items) {
            session.save(item);
        }

        session.getTransaction().commit();

        session.close();

    }
}
