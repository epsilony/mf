/* (c) Copyright by Man YUAN */
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
