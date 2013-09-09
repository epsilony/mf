/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Coord3DTypeTest {

    public Coord3DTypeTest() {
    }

    @Test
    public void testPersistAndRecovery() {
        SessionFactory factory = MFHibernateUtil.getSessionFactory();

        Session session = factory.openSession();
        double[][] values = new double[][]{
            {0.5, 1.13, 12},
            {0.23},
            {0.24, 0.15},
            null
        };
        Coord3DTestEntity[] items = new Coord3DTestEntity[]{
            new Coord3DTestEntity("3d", values[0]),
            new Coord3DTestEntity("1d", values[1]),
            new Coord3DTestEntity("2d", values[2]),
            new Coord3DTestEntity("null_", values[3])
        };
        session.beginTransaction();
        for (Coord3DTestEntity item : items) {
            session.save(item);
        }
        session.getTransaction().commit();
        session.close();

        session = factory.openSession();
        for (Coord3DTestEntity item : items) {
            Coord3DTestEntity newItem = (Coord3DTestEntity) session.get(Coord3DTestEntity.class, item.id);
            assertTrue(newItem != item);
            assertArrayEquals(item.coord, newItem.coord, 1e-14);
            assertEquals(item.name, newItem.name);
        }
        session.close();
    }
}