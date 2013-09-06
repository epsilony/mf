/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class HibernateDemo {

    public static void main(String[] args) {
        Configuration conf = new Configuration();

        conf.configure();
        //!must add prefix hibernate before any property names
        conf.setProperty("hibernate.connection.url", "jdbc:sqlite:hibernate_demo.db");
        conf.setProperty("hibernate.hbm2ddl.auto", "update");


        ServiceRegistry buildServiceRegistry = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory factory = conf.buildSessionFactory(buildServiceRegistry);

        Session session = factory.openSession();
        double[] value = new double[]{0.5, 1.13};
        HibernateDemoItem[] items = new HibernateDemoItem[]{
            new HibernateDemoItem("bob", value),
            new HibernateDemoItem("uncle", value)};
        session.beginTransaction();
        for (HibernateDemoItem item : items) {
            session.save(item);
        }
        session.getTransaction().commit();
        session.close();
    }
}
