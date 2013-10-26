/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrixData;
import net.epsilony.mf.util.matrix.MFMatrixTestUtil;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import org.ejml.data.DenseMatrix64F;
import org.junit.Test;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import org.ejml.data.Matrix64F;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WrapperMFMatrixTest {

    double[][] values = new double[][]{
        {110, 120, 130},
        {210, 220, 230},
        {310, 320, 330}};
    WrapperMFMatrix[] sampleWrappers = new WrapperMFMatrix[]{
        new MTJMatrixWrapper(new DenseMatrix(values)),
        new MTJVectorWrapper(new DenseVector(values[2])),
        new MTJMatrixWrapper(new FlexCompRowMatrix(new DenseMatrix(values))),
        new EJMLMatrix64FWrapper(new DenseMatrix64F(values))
    };

    @Test
    public void testWrappers() {
        for (WrapperMFMatrix wrapper : sampleWrappers) {
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
