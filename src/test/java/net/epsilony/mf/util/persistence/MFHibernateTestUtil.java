/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

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
        testConfig.setProperty("hibernate.connection.url", "jdbc:sqlite::memory:");
        testConfig.setProperty("hibernate.hbm2dll2.auto","create");
        return testConfig;
    }
}
