/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import net.epsilony.tb.IntIdentity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Ignore;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Ignore
public class MFHibernateTestUtil {

    public static Configuration genTestConfig() {
        Configuration testConfig = new Configuration();
        testConfig.configure();
        testConfig.setProperty("hibernate.connection.url", "jdbc:h2:mem:");
        testConfig.setProperty("hibernate.hbm2dll2.auto", "create");
        testConfig.setProperty("hibernate.show_sql", "false");
        return testConfig;
    }

    public static <T extends IntIdentity> T copyByHibernate(T ori, Configuration testConfig) {
        SessionFactory factory = MFHibernateUtil.newSessionFactory(testConfig);
        Session session = factory.openSession();
        session.beginTransaction();
        session.save(ori);
        session.getTransaction().commit();
        session.close();

        Session newSession = factory.openSession();
        Object copy = newSession.get(ori.getClass(), ori.getId());

        newSession.close();
        factory.close();

        return (T) copy;
    }

    public static <T extends IntIdentity> T copyByHibernate(T ori) {
        return copyByHibernate(ori, genTestConfig());
    }
}
