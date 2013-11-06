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

package net.epsilony.mf.util.persistence.demo;

import java.util.HashSet;
import java.util.Set;
import net.epsilony.mf.util.persistence.MFHibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MixMethodDemo {

    public static Configuration genConfig() {
        Configuration config = new Configuration();
        config.configure(MixMethodDemo.class.getResource("mix_method_demo.cfg.xml"));
        return config;
    }

    public static void main(String[] args) {
        Configuration config = genConfig();
        SessionFactory factory = MFHibernateUtil.newSessionFactory(config);
        Session session = factory.openSession();
        session.beginTransaction();
        Set<Integer> idSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ClassA a = new ClassA(i * 2);
            session.saveOrUpdate(a);
            ClassA2 a2 = new ClassA2(i * 2 + 1);
            session.saveOrUpdate(a2);
            ClassB b = new ClassB(i + 100, i);
            ClassB2 b2 = new ClassB2(i * 2 + 100, i * 2);
            session.saveOrUpdate(b);
            session.saveOrUpdate(b2);
            idSet.add(a.id);
            idSet.add(a2.id);
            idSet.add(b.id);

        }
        session.getTransaction().commit();
        session.close();

        session = factory.openSession();
        for (int id : idSet) {
            ClassA a = (ClassA) session.get(ClassA.class, id);
            ClassA2 a2 = (ClassA2) session.get(ClassA2.class, id);
            ClassB b = (ClassB) session.get(ClassB.class, id);
            ClassB2 b2 = (ClassB2) session.get(ClassB2.class, id);
            System.out.println("a = " + a);
            System.out.println("a2 = " + a2);
            System.out.println("b = " + b);
            System.out.println("b2 = " + b2);
        }
    }
}
