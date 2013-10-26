/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.util.Random;
import org.ejml.data.DenseMatrix64F;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ByteHashRowMatrixTest {

    public ByteHashRowMatrixTest() {
    }

    @Test
    public void test() {
        int testTimes = 10;
        boolean tested = false;
        for (int i = 0; i < testTimes; i++) {
            testWithDenseMatrix64();
            tested = true;
        }
        assertTrue(tested);
    }
    
    public void testWithDenseMatrix64() {
//        Long seed = 147l;
        Long seed = new Random().nextLong();
        int[] sizeRange = new int[]{20, 40};
        int numCols = new Random().nextInt(sizeRange[1] - sizeRange[0]) + sizeRange[0];
        int numRows = new Random().nextInt(sizeRange[1] - sizeRange[0]) + sizeRange[0];
        double zeroPortion = 0.8;

        MFMatrix exp = MFMatries.wrap(new DenseMatrix64F(numRows, numCols));
        MFMatrix act = new HashRowMatrix(numRows, numCols);

        addRandomByteValue(exp, zeroPortion, new Random(seed));
        addRandomByteValue(act, zeroPortion, new Random(seed));

        MFMatrixTestUtil.assertMatries(exp, act);
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
