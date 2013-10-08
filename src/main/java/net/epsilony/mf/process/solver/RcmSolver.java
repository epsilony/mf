/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.solver;

import static net.epsilony.mf.process.MFIntegrateProcessor.logger;
import net.epsilony.tb.matrix.ReverseCuthillMcKeeSolver;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RcmSolver implements MFSolver {

    Matrix mainMatrix;
    DenseVector mainVector;
    private boolean upperSymmetric;
    protected DenseVector result;

    @Override
    public void setUpperSymmetric(boolean upperSymmetric) {
        this.upperSymmetric = upperSymmetric;
    }

    @Override
    public void setMainMatrix(Matrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    @Override
    public void setMainVector(DenseVector mainVector) {
        this.mainVector = mainVector;
    }

    @Override
    public void solve() {

        ReverseCuthillMcKeeSolver rcm = new ReverseCuthillMcKeeSolver(mainMatrix, upperSymmetric);
        logger.info("solving main matrix:{}, bandwidth ori/opt: {}/{}",
                rcm,
                rcm.getOriginalBandWidth(),
                rcm.getOptimizedBandWidth());
        result = rcm.solve(mainVector);
        logger.info("solved main matrix");
    }

    @Override
    public DenseVector getResult() {
        return result;
    }
}
