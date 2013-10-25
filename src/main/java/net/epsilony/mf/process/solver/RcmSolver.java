/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.solver;

import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import net.epsilony.tb.matrix.ReverseCuthillMcKeeSolver;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RcmSolver implements MFSolver {

    private static final Logger logger = LoggerFactory.getLogger(RcmSolver.class);
    MFMatrix mainMatrix;
    MFMatrix mainVector;
    protected MFMatrix result;

    @Override
    public void setMainMatrix(MFMatrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    @Override
    public void setMainVector(MFMatrix mainVector) {
        this.mainVector = mainVector;
    }

    @Override
    public void solve() {
        WrapperMFMatrix<Matrix> wrapperMainMatrix = (WrapperMFMatrix<Matrix>) mainMatrix;
        ReverseCuthillMcKeeSolver rcm = new ReverseCuthillMcKeeSolver(wrapperMainMatrix.getBackend(), mainMatrix.isUpperSymmetric());
        logger.info("solving main matrix:{}, bandwidth ori/opt: {}/{}",
                rcm,
                rcm.getOriginalBandWidth(),
                rcm.getOptimizedBandWidth());

        WrapperMFMatrix<Vector> wrapperMainVector = (WrapperMFMatrix<Vector>) mainVector;
        DenseVector denseVectorResult = rcm.solve(wrapperMainVector.getBackend());
        result = MFMatries.wrap(denseVectorResult);
        logger.info("solved main matrix");
    }

    @Override
    public MFMatrix getResult() {
        return result;
    }
}
