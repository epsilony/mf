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

package net.epsilony.mf.util.matrix;

import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import java.util.Iterator;
import java.util.LinkedList;
import net.epsilony.mf.util.persistence.MFHibernateTestUtil;
import net.epsilony.mf.util.persistence.MFHibernateUtil;
import no.uib.cipr.matrix.MatrixEntry;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix64F;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFMatrixDataTest {

    public MFMatrixDataTest() {
    }
    // @Test
    // public void testSomeMethod() {
    // Configuration testConfig = MFHibernateTestUtil.genTestConfig();
    // SessionFactory factory = MFHibernateUtil.newSessionFactory(testConfig);
    // DenseMatrix64F denseMatrix64F = new DenseMatrix64F(3, 3);
    // denseMatrix64F.data = new double[]{11, 12, 13, 21, 22, 23, 31, 32, 33};
    // WrapperMFMatrix<Matrix64F> wrap = MFMatries.wrap(denseMatrix64F);
    // MFMatrixData data = new MFMatrixData();
    // data.setNumCols(wrap.numCols());
    // data.setNumRows(wrap.numRows());
    // data.setMatrixClass(denseMatrix64F.getClass());
    // LinkedList<MatrixEntry> entries = new LinkedList<>();
    // for (MatrixEntry me : wrap) {
    // entries.add(me);
    // }
    // data.setMatrixEntries(entries);
    // Session session = factory.openSession();
    // session.beginTransaction();
    // session.save(data);
    // session.getTransaction().commit();
    //
    // session.close();
    //
    // session = factory.openSession();
    // MFMatrixData newData = (MFMatrixData) session.get(MFMatrixData.class,
    // data.id);
    // session.close();
    // factory.close();
    //
    // assertEquals(data.id, newData.id);
    // assertEquals(data.numCols, newData.numCols);
    // assertEquals(data.numRows, newData.numRows);
    //
    // Iterator<MatrixEntry> dataIter = data.matrixEntries.iterator();
    // Iterator<MatrixEntry> newIter = newData.matrixEntries.iterator();
    //
    // while (dataIter.hasNext()) {
    // assertTrue(newIter.hasNext());
    // MatrixEntry dme = dataIter.next();
    // MatrixEntry nme = newIter.next();
    // assertEquals(dme.column(), nme.column());
    // assertEquals(dme.row(), nme.row());
    // assertEquals(dme.get(), nme.get(), 1e-14);
    // }
    // }
}