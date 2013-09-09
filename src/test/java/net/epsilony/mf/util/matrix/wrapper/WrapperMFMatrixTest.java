/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

import net.epsilony.mf.util.matrix.MFMatrixData;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import org.ejml.data.DenseMatrix64F;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.epsilony.mf.util.matrix.MFMatrixTestUtil.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WrapperMFMatrixTest {

    double[][] values = new double[][]{
        {110, 120, 130},
        {210, 220, 230},
        {310, 320, 330}};
    WrapperMFMatrix[] sameSizeWrappers = new WrapperMFMatrix[]{
        new MTJMatrixWrapper(new DenseMatrix(3, 3)),
        new MTJVectorWrapper(new DenseVector(3)),
        new MTJMatrixWrapper(new FlexCompRowMatrix(3, 3)),
        new EJMLMatrix64FWrapper(new DenseMatrix64F(3, 3))};
    WrapperMFMatrix[] emptyWrappers = new WrapperMFMatrix[]{
        new MTJMatrixWrapper(new DenseMatrix(1, 1)),
        new MTJVectorWrapper(new DenseVector(1)),
        new MTJMatrixWrapper(new FlexCompRowMatrix(1, 1)),
        new EJMLMatrix64FWrapper(new DenseMatrix64F(1, 1))};
    WrapperMFMatrix[] sampleWrappers = new WrapperMFMatrix[]{
        new MTJMatrixWrapper(new DenseMatrix(values)),
        new MTJVectorWrapper(new DenseVector(values[2])),
        new MTJMatrixWrapper(new FlexCompRowMatrix(new DenseMatrix(values))),
        new EJMLMatrix64FWrapper(new DenseMatrix64F(values))
    };

    @Test
    public void testWrappers() {
        for (int i = 0; i < sampleWrappers.length; i++) {
            WrapperMFMatrix wrapper = sampleWrappers[i];
            MFMatrixData data = wrapper.getMatrixData();

            WrapperMFMatrix recoverWrapper = emptyWrappers[i];
            recoverWrapper.setBackendReallocatable(true);
            recoverWrapper.setMatrixData(data);
            assertMatries(wrapper, recoverWrapper);

            WrapperMFMatrix sameSizeRecoverMapper = sameSizeWrappers[i];
            Object oldBackend = sameSizeRecoverMapper.getBackend();
            sameSizeRecoverMapper.setMatrixData(data);
            assertTrue(oldBackend == sameSizeRecoverMapper.getBackend());
            assertMatries(wrapper, sameSizeRecoverMapper);
        }
    }
}
