/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.solver;

import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import no.uib.cipr.matrix.BandMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import org.ejml.data.DenseMatrix64F;
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
        logger.info("into solver {}", this);
        ReverseCuthillMcKeeMFMatrixResortor rcmResortor = new ReverseCuthillMcKeeMFMatrixResortor(mainMatrix);

        MFMatrix optMatrix = produceMatrixForSolving(rcmResortor.getOptimiziedBandWidth());
        MFMatrix optVector = produceVectorForSolving();

        rcmResortor.writeOptimizedMatrixTo(optMatrix);
        rcmResortor.sortVector(mainVector, optVector);

        logger.info("start inner solver");
        MFMatrix optResult = innerSolve(optMatrix, optVector);
        logger.info("inner solving accomplished!");
        result = MFMatries.wrap(new DenseMatrix64F(mainMatrix.numRows(), 1));
        rcmResortor.recoverVector(optResult, result);
    }

    @Override
    public MFMatrix getResult() {
        return result;
    }

    private MFMatrix produceMatrixForSolving(int bandWidth) {
        return MFMatries.wrap(new BandMatrix(mainMatrix.numRows(), bandWidth, bandWidth));
    }

    private MFMatrix produceVectorForSolving() {
        return MFMatries.wrap(new DenseVector(mainVector.numRows()));
    }

    private MFMatrix innerSolve(MFMatrix optMatrix, MFMatrix optVector) {
        WrapperMFMatrix<Matrix> wrapperMat = (WrapperMFMatrix<Matrix>) optMatrix;
        WrapperMFMatrix<DenseVector> wrapperVec = (WrapperMFMatrix<DenseVector>) optVector;
        DenseVector optResult = new DenseVector(wrapperVec.numRows());
        wrapperMat.getBackend().solve(wrapperVec.getBackend(), optResult);
        return MFMatries.wrap(optResult);
    }
}
