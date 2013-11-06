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

import java.util.Random;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import org.ejml.data.Matrix64F;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Ignore
public class MFMatrixTestUtil {

    public static void assertMatries(MFMatrix expMat, MFMatrix actMat) {
        assertEquals(expMat.numRows(), actMat.numRows());
        assertEquals(expMat.numCols(), actMat.numCols());
        boolean tested1 = false;
        for (MatrixEntry expMatE : expMat) {
            tested1 = true;
            assertEquals(expMatE.get(), actMat.get(expMatE.row(), expMatE.column()), 1e-14);
        }
        boolean tested2 = false;
        for (MatrixEntry actMatE : actMat) {
            assertEquals(expMat.get(actMatE.row(), actMatE.column()), actMatE.get(), 1e-14);
            tested2 = true;
        }
        assertEquals(tested1, tested2);
    }

    public static void assertMatries(Matrix expMat, MFMatrix actMat) {
        assertEquals(expMat.numRows(), actMat.numRows());
        assertEquals(expMat.numColumns(), actMat.numCols());
        boolean tested1 = false;
        for (MatrixEntry me : expMat) {
            tested1 = true;
            assertEquals(me.get(), actMat.get(me.row(), me.column()), 1e-14);
        }
        boolean tested2 = false;
        for (MatrixEntry me : actMat) {
            assertEquals(expMat.get(me.row(), me.column()), me.get(), 1e-14);
            tested2 = true;
        }
        assertEquals(tested1, tested2);
    }

    public static void assertMatries(Vector vector, MFMatrix actMat) {
        assertEquals(vector.size(), actMat.numRows());
        assertEquals(1, actMat.numCols());
        boolean tested1 = false;
        for (VectorEntry ve : vector) {
            assertEquals(ve.get(), actMat.get(ve.index(), 0), 1e-14);
            tested1 = true;
        }
        boolean tested2 = false;
        for (MatrixEntry me : actMat) {
            assertEquals(me.column(), 0);
            assertEquals(vector.get(me.row()), me.get(), 1e-14);
            tested2 = true;
        }
        assertEquals(tested1, tested2);
    }

    public static void assertMatries(Matrix64F matrix, MFMatrix actMat) {
        assertEquals(matrix.numRows, actMat.numRows());
        assertEquals(matrix.numCols, actMat.numCols());
        for (int row = 0; row < matrix.numRows; row++) {
            for (int col = 0; col < matrix.numCols; col++) {
                assertEquals(matrix.get(row, col), actMat.get(row, col), 1e-14);
            }
        }
    }

    public static void addRandomValue(MFMatrix matrix, double zeroPortion, Random random) {
        for (int row = 0; row < matrix.numRows(); row++) {
            for (int col = 0; col < matrix.numCols(); col++) {
                if (random.nextDouble() <= zeroPortion) {
                    matrix.add(row, col, random.nextDouble());
                }
            }
        }
    }
}
