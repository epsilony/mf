/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.util.Random;
import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class HashRowMatrixTest {

    public HashRowMatrixTest() {
    }

    @Test
    public void testWithDenseMatrix64() {
        Long seed = 47l;
        int numCols = 40;
        int numRows = 40;
        double zeroPortion = 0.8;

        MFMatrix exp = MFMatries.wrap(new DenseMatrix64F(numRows, numCols));
        MFMatrix act = new HashRowMatrix(numRows, numCols);

        MFMatrixTestUtil.addRandomValue(exp, zeroPortion, new Random(seed));
        MFMatrixTestUtil.addRandomValue(act, zeroPortion, new Random(seed));

        MFMatrixTestUtil.assertNonzeroMatries(exp, act);
    }

}
