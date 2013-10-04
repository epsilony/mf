/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.util.matrix;

import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import org.ejml.data.Matrix64F;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

/**
 *
 * @author epsilon
 */
@Ignore
public class MFMatrixTestUtil {

    public static void assertMatries(MFMatrix expMat, MFMatrix actMat) {
        assertEquals(expMat.getNumRows(), actMat.getNumRows());
        assertEquals(expMat.getNumCols(), actMat.getNumCols());
        boolean tested = false;
        for (MatrixEntry me : expMat) {
            tested = true;
            assertEquals(me.get(), actMat.getEntry(me.row(), me.column()), 1e-14);
        }
        assertTrue(tested);
        for (MatrixEntry me : actMat) {
            assertEquals(expMat.getEntry(me.row(), me.column()), me.get(), 1e-14);
        }
    }

    public static void assertMatries(Matrix expMat, MFMatrix actMat) {
        assertEquals(expMat.numRows(), actMat.getNumRows());
        assertEquals(expMat.numColumns(), actMat.getNumCols());
        boolean tested = false;
        for (MatrixEntry me : expMat) {
            tested = true;
            assertEquals(me.get(), actMat.getEntry(me.row(), me.column()), 1e-14);
        }
        assertTrue(tested);
        for (MatrixEntry me : actMat) {
            assertEquals(expMat.get(me.row(), me.column()), me.get(), 1e-14);
        }
    }

    public static void assertMatries(Vector vector, MFMatrix actMat) {
        assertEquals(vector.size(), actMat.getNumRows());
        assertEquals(1, actMat.getNumCols());
        boolean tested = false;
        for (VectorEntry ve : vector) {
            assertEquals(ve.get(), actMat.getEntry(ve.index(), 0), 1e-14);
            tested = true;
        }
        assertTrue(tested);
        for (MatrixEntry me : actMat) {
            assertEquals(me.column(), 0);
            assertEquals(vector.get(me.row()), me.get(), 1e-14);
        }
    }

    public static void assertMatries(Matrix64F matrix, MFMatrix actMat) {
        assertEquals(matrix.numRows, actMat.getNumRows());
        assertEquals(matrix.numCols, actMat.getNumCols());
        boolean tested = false;
        for (int row = 0; row < matrix.numRows; row++) {
            for (int col = 0; col < matrix.numCols; col++) {
                assertEquals(matrix.get(row, col), actMat.getEntry(row, col), 1e-14);
                tested = true;
            }
        }
        assertTrue(tested);
    }
}
