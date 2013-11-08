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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
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
        Configuration testConfig = MFHibernateTestUtil.genTestConfig();
        testConfig.addAnnotatedClass(Coord3DTestEntity.class);
        SessionFactory factory = MFHibernateUtil.newSessionFactory(testConfig);

        Session session = factory.openSession();
        double[][] values = new double[][] { { 0.5, 1.13, 12 }, { 0.23 }, { 0.24, 0.15 }, null };
        Coord3DTestEntity[] items = new Coord3DTestEntity[] { new Coord3DTestEntity("3d", values[0]),
                new Coord3DTestEntity("1d", values[1]), new Coord3DTestEntity("2d", values[2]),
                new Coord3DTestEntity("null_", values[3]) };
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
        factory.close();
    }
}