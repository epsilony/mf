/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.analysis.WithDiffOrderUtil;
import net.epsilony.tb.solid.Segment;

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

    public double[] value(double[] center, Segment bnd) {
        MixResult mixResult = mix(center, bnd);
        double[] output = new double[WithDiffOrderUtil.outputLength2D(getDiffOrder()) * nodeValueDimension];
        int i = 0;
        if (getDiffOrder() > 1) {
            throw new UnsupportedOperationException();
        }
        for (MFNode node : mixResult.nodes) {
            double[] value = node.getValue();

            double sv = mixResult.shapeFunctionValueLists[0].get(i);
            for (int valueDim = 0; valueDim < nodeValueDimension; valueDim++) {
                output[valueDim] += value[valueDim] * sv;
                if (getDiffOrder() >= 1) {
                    for (int varDim = 0; varDim < VARIABLE_DIMENSION; varDim++) {
                        double s_p = mixResult.shapeFunctionValueLists[varDim + 1].get(i);
                        output[(valueDim + 1) * nodeValueDimension + varDim] += s_p * value[valueDim];
                    }
                }
            }
            i++;
        }
        return output;
    }
}
