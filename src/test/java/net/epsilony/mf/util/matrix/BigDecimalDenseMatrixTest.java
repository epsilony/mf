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
public class BigDecimalDenseMatrixTest {

    public BigDecimalDenseMatrixTest() {
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
        int[] sizeRange = new int[]{20, 40};
        Long seed = new Random().nextLong();
        int numCols = new Random().nextInt(sizeRange[1] - sizeRange[0]) + sizeRange[0];
        int numRows = new Random().nextInt(sizeRange[1] - sizeRange[0]) + sizeRange[0];
        double zeroPortion = 0.8;

        MFMatrix exp = MFMatries.wrap(new DenseMatrix64F(numRows, numCols));
        MFMatrix act = new HashRowMatrix(numRows, numCols);

        MFMatrixTestUtil.addRandomValue(exp, zeroPortion, new Random(seed));
        MFMatrixTestUtil.addRandomValue(act, zeroPortion, new Random(seed));

        MFMatrixTestUtil.assertMatries(exp, act);
    }
}
