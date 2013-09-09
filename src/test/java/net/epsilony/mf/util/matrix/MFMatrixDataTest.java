/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import java.util.Iterator;
import java.util.LinkedList;
import net.epsilony.mf.util.persistence.MFHibernateUtil;
import no.uib.cipr.matrix.MatrixEntry;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix64F;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFMatrixDataTest {

    public MFMatrixDataTest() {
    }

    @Test
    public void testSomeMethod() {

        SessionFactory factory = MFHibernateUtil.getSessionFactory();
        DenseMatrix64F denseMatrix64F = new DenseMatrix64F(3, 3);
        denseMatrix64F.data = new double[]{11, 12, 13, 21, 22, 23, 31, 32, 33};
        WrapperMFMatrix<Matrix64F> wrap = MFMatries.wrap(denseMatrix64F);
        MFMatrixData data = new MFMatrixData();
        data.setNumCols(wrap.getNumCols());
        data.setNumRows(wrap.getNumRows());
        data.setMatrixClass(denseMatrix64F.getClass());
        LinkedList<RawMatrixEntry> entries = new LinkedList<>();
        for (MatrixEntry me : wrap) {
            entries.add((RawMatrixEntry) me);
        }
        data.setMatrixEntries(entries);
        Session session = factory.openSession();
        session.beginTransaction();
        session.save(data);
        session.getTransaction().commit();

        session.close();

        session = factory.openSession();
        MFMatrixData newData = (MFMatrixData) session.get(MFMatrixData.class, data.id);
        session.close();

        assertEquals(data.id, newData.id);
        assertEquals(data.numCols, newData.numCols);
        assertEquals(data.numRows, newData.numRows);

        Iterator<RawMatrixEntry> dataIter = data.matrixEntries.iterator();
        Iterator<RawMatrixEntry> newIter = newData.matrixEntries.iterator();

        while (dataIter.hasNext()) {
            assertTrue(newIter.hasNext());
            RawMatrixEntry dme = dataIter.next();
            RawMatrixEntry nme = newIter.next();
            assertEquals(dme.col, nme.col);
            assertEquals(dme.row, nme.row);
            assertEquals(dme.entryValue, nme.entryValue, 1e-14);
        }

    }
}