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
        for (MFNode node : mixResult.nodes) {
            double[] value = node.getValue();
            for (int j = 0; j < mixResult.shapeFunctionValueLists.length; j++) {
                double sv = mixResult.shapeFunctionValueLists[j].get(i);
                for (int k = 0; k < nodeValueDimension; k++) {
                    output[j * nodeValueDimension + k] += value[k] * sv;
                }
            }
            i++;
        }
        return output;
    }
}
