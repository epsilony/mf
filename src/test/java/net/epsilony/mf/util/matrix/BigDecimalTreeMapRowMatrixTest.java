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
import org.ejml.data.DenseMatrix64F;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class BigDecimalTreeMapRowMatrixTest {

    public BigDecimalTreeMapRowMatrixTest() {
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
        // Long seed = 147l;
        Long seed = new Random().nextLong();
        int[] sizeRange = new int[] { 10, 20 };
        int numCols = new Random().nextInt(sizeRange[1] - sizeRange[0]) + sizeRange[0];
        int numRows = new Random().nextInt(sizeRange[1] - sizeRange[0]) + sizeRange[0];
        double zeroPortion = 0.8;

        MFMatrix exp = MFMatries.wrap(new DenseMatrix64F(numRows, numCols));
        MFMatrix act = new BigDecimalTreeMapRowMatrix(numRows, numCols);

        MFMatrixTestUtil.addRandomValue(exp, zeroPortion, new Random(seed));
        MFMatrixTestUtil.addRandomValue(act, zeroPortion, new Random(seed));

        MFMatrixTestUtil.assertMatries(exp, act);
    }

}
