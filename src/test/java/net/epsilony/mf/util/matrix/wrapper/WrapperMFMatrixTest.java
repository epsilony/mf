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

package net.epsilony.mf.util.matrix.wrapper;

import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrixData;
import net.epsilony.mf.util.matrix.MFMatrixTestUtil;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix64F;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WrapperMFMatrixTest {

    double[][] values = new double[][] { { 110, 120, 130 }, { 210, 220, 230 }, { 310, 320, 330 } };
    WrapperMFMatrix<?>[] sampleWrappers = new WrapperMFMatrix[] { new MTJMatrixWrapper(new DenseMatrix(values)),
            new MTJVectorWrapper(new DenseVector(values[2])),
            new MTJMatrixWrapper(new FlexCompRowMatrix(new DenseMatrix(values))),
            new EJMLMatrix64FWrapper(new DenseMatrix64F(values)) };

    @Test
    public void testWrappers() {
        for (WrapperMFMatrix<?> wrapper : sampleWrappers) {
            MFMatrixData data = wrapper.genMatrixData();
            Object allocateMatrix = MFMatries.allocateMatrix(data);

            if (allocateMatrix instanceof Matrix) {
                MFMatrixTestUtil.assertMatries(wrapper, MFMatries.wrap((Matrix) allocateMatrix));
            } else if (allocateMatrix instanceof Vector) {
                MFMatrixTestUtil.assertMatries(wrapper, MFMatries.wrap((Vector) allocateMatrix));
            } else if (allocateMatrix instanceof Matrix64F) {
                MFMatrixTestUtil.assertMatries(wrapper, MFMatries.wrap((Matrix64F) allocateMatrix));
            }
        }
    }
}
