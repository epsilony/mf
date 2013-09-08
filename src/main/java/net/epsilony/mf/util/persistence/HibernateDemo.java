/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.type.DoubleType;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Entity
public class HibernateDemo implements Serializable {

    public static void main(String[] args) {
        Configuration conf = new Configuration();

        conf.configure();
        //!must add prefix hibernate before any property names
        conf.setProperty("hibernate.connection.url", "jdbc:sqlite:hibernate_demo.db");
        conf.setProperty("hibernate.hbm2ddl.auto", "create");


        ServiceRegistry buildServiceRegistry = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory factory = conf.buildSessionFactory(buildServiceRegistry);

        Session session = factory.openSession();
        double[] value = new double[]{0.5, 1.13, 12};
        HibernateDemo[] items = new HibernateDemo[]{
            new HibernateDemo("bob", value),
            new HibernateDemo("uncle", value)};
        session.beginTransaction();
        for (HibernateDemo item : items) {
            session.save(item);
        }

        session.getTransaction().commit();

        session.close();

    }
    static long maxId = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    static final String typeName = UserCoord.class.getName();
    @Basic
    String name;
    @Columns(columns = {
        @Column(name = "x"),
        @Column(name = "y"),
        @Column(name = "z")})
    @Type(type = "net.epsilony.mf.util.persistence.HibernateDemo$UserCoord")
    double[] doubles;

    public HibernateDemo(String name, double[] value) {

        this.name = name;
        this.doubles = value;
    }

    public HibernateDemo() {
    }

    public static class UserCoord extends MutableUserType {

        @Override
        public int[] sqlTypes() {
            return new int[]{Types.DOUBLE, Types.DOUBLE, Types.DOUBLE};
        }

        @Override
        public Class returnedClass() {
            return double[].class;
        }

        @Override
        public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
            int i = 0;
            double[] result = new double[3];
            for (String name : names) {
                Object nullSafeGet = org.hibernate.type.DoubleType.INSTANCE.nullSafeGet(rs, name, session, owner);
                if (i == 0 && null == nullSafeGet) {
                    return null;
                } else if (null != nullSafeGet) {
                    result[i++] = (double) nullSafeGet;
                } else {
                    result[i++] = 0;
                }
            }
            return result;
        }

        @Override
        public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
            double[] xs = (double[]) value;
            for (int i = 0; i < 3; i++) {
                if (xs == null || i >= xs.length) {
                    DoubleType.INSTANCE.nullSafeSet(st, null, index + i, session);
                } else {
                    DoubleType.INSTANCE.nullSafeSet(st, xs[i], index + i, session);
                }
            }
        }

        @Override
        public Object deepCopy(Object value) throws HibernateException {
            double[] xs = (double[]) value;
            return Arrays.copyOf(xs, xs.length);
        }
    }
}
