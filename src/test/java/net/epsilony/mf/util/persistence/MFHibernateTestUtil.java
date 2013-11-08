/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    @SuppressWarnings("unchecked")
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
