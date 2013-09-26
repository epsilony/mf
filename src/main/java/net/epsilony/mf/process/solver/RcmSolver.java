/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.solver;

import java.util.List;
import net.epsilony.mf.model.MFNode;
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
    int nodeValueDimension = 2;
    private boolean upperSymmetric;
    List<? extends MFNode> nodes;
    protected DenseVector result;

    @Override
    public void setUpperSymmetric(boolean upperSymmetric) {
        this.upperSymmetric = upperSymmetric;
    }

    public void setNodeValueDimension(int nodeValueDimension) {
        this.nodeValueDimension = nodeValueDimension;
    }

    @Override
    public void setNodes(List<? extends MFNode> nodes) {
        this.nodes = nodes;
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

        for (MFNode node : nodes) {
            int nodeValueIndex = node.getAssemblyIndex() * nodeValueDimension;
            if (nodeValueIndex >= 0) {
                double[] nodeValue = new double[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    nodeValue[i] = result.get(i + nodeValueIndex);
                    node.setValue(nodeValue);
                }
            }
            int lagrangeValueIndex = node.getLagrangeAssemblyIndex();
            if (lagrangeValueIndex >= 0) {
                double[] lagrangeValue = new double[nodeValueDimension];
                boolean[] lagrangeValueValidity = new boolean[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    int index = lagrangeValueIndex * nodeValueDimension + i;
                    lagrangeValue[i] = result.get(index);
                    lagrangeValueValidity[i] = mainMatrix.get(index, index) == 0;  //a prototyle of validity
                }
                node.setLagrangeValue(lagrangeValue);
                node.setLagrangeValueValidity(lagrangeValueValidity);
            }
        }
        logger.info("filled nodes values to nodes processor data map");
    }

    @Override
    public DenseVector getResult() {
        return result;
    }
}
