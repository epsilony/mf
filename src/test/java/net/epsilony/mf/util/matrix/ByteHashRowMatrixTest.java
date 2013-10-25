/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.util.Random;
import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ByteHashRowMatrixTest {

    public ByteHashRowMatrixTest() {
    }

    @Test
    public void testWithDenseMatrix64() {
        Long seed = 147l;
        int numCols = 40;
        int numRows = 40;
        double zeroPortion = 0.8;

        MFMatrix exp = MFMatries.wrap(new DenseMatrix64F(numRows, numCols));
        MFMatrix act = new HashRowMatrix(numRows, numCols);

        addRandomByteValue(exp, zeroPortion, new Random(seed));
        addRandomByteValue(act, zeroPortion, new Random(seed));

        MFMatrixTestUtil.assertNonzeroMatries(exp, act);
    }

    public static void addRandomByteValue(MFMatrix matrix, double zeroPortion, Random random) {
        for (int row = 0; row < matrix.numRows(); row++) {
            for (int col = 0; col < matrix.numCols(); col++) {
                if (random.nextDouble() <= zeroPortion) {
                    matrix.add(row, col, (byte) random.nextInt());
                }
            }
        }
    }

}
