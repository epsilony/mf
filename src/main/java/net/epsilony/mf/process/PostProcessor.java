/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.model.MFBoundary;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.analysis.WithDiffOrderUtil;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PostProcessor extends Mixer {

    private static final int VARIABLE_DIMENSION = 2;
    int nodeValueDimension;

    public int getNodeValueDimension() {
        return nodeValueDimension;
    }

    public void setNodeValueDimension(int nodeValueDimension) {
        this.nodeValueDimension = nodeValueDimension;
    }

    public double[] value(double[] center, MFBoundary bnd) {
        MixResult mixResult = mix(center, bnd);
        double[] output = new double[WithDiffOrderUtil.outputLength2D(getDiffOrder()) * nodeValueDimension];
        int i = 0;
        if (getDiffOrder() > 1) {
            throw new UnsupportedOperationException();
        }

        double[][] shapeFunctionValues = mixResult.getShapeFunctionValues();
        for (MFNode node : mixResult.getNodes()) {
            double[] value = node.getValue();

            double sv = shapeFunctionValues[0][i];
            for (int valueDim = 0; valueDim < nodeValueDimension; valueDim++) {
                output[valueDim] += value[valueDim] * sv;
                if (getDiffOrder() >= 1) {
                    for (int varDim = 0; varDim < VARIABLE_DIMENSION; varDim++) {
                        double s_p = shapeFunctionValues[varDim + 1][i];
                        output[(valueDim + 1) * nodeValueDimension + varDim] += s_p * value[valueDim];
                    }
                }
            }
            i++;
        }
        return output;
    }
}
