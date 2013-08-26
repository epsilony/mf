/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.solver;

import net.epsilony.mf.geomodel.MFNode;
import static net.epsilony.mf.process.MFProcessor.logger;
import net.epsilony.mf.process.ProcessResult;
import net.epsilony.tb.matrix.ReverseCuthillMcKeeSolver;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RcmSolver implements MFSolver {

    ProcessResult processResult;
    static final int DIMENSION = 2;

    @Override
    public void setProcessResult(ProcessResult pr) {
        processResult = pr;
    }

    @Override
    public void solve() {

        ReverseCuthillMcKeeSolver rcm = new ReverseCuthillMcKeeSolver(processResult.getMainMatrix(), processResult.isUpperSymmetric());
        logger.info("solving main matrix:{}, bandwidth ori/opt: {}/{}",
                rcm,
                rcm.getOriginalBandWidth(),
                rcm.getOptimizedBandWidth());
        DenseVector nodesValue = rcm.solve(processResult.getGeneralForce());
        logger.info("solved main matrix");
        int nodeValueDimension = processResult.getNodeValueDimension();
        Matrix mainMatrix = processResult.getMainMatrix();
        for (MFNode node : processResult.getNodes()) {

            int nodeValueIndex = node.getAssemblyIndex() * nodeValueDimension;
            if (nodeValueIndex >= 0) {
                double[] nodeValue = new double[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    nodeValue[i] = nodesValue.get(i + nodeValueIndex);
                    node.setValue(nodeValue);
                }
            }
            int lagrangeValueIndex = node.getLagrangeAssemblyIndex();
            if (lagrangeValueIndex >= 0) {
                double[] lagrangeValue = new double[nodeValueDimension];
                boolean[] lagrangeValueValidity = new boolean[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    int index = lagrangeValueIndex * nodeValueDimension + i;
                    lagrangeValue[i] = nodesValue.get(index);
                    lagrangeValueValidity[i] = mainMatrix.get(index, index) == 0;  //a prototyle of validity
                }
                node.setLagrangeValue(lagrangeValue);
                node.setLagrangeValueValidity(lagrangeValueValidity);
            }
        }
        logger.info("filled nodes values to nodes processor data map");
    }
}
