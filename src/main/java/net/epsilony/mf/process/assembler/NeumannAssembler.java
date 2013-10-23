/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class NeumannAssembler extends AbstractAssembler {

    @Override
    public void assemble() {
        MFMatrix vec = mainVector;
        double[] neumannVal = load;
        double[] vs = testShapeFunctionValues[0];
        TIntArrayList indes = nodesAssemblyIndes;
        for (int i = 0; i < indes.size(); i++) {
            int vecIndex = indes.getQuick(i) * valueDimension;
            double v = vs[i];
            for (int valueDim = 0; valueDim < valueDimension; valueDim++) {
                vec.add(vecIndex + valueDim, 0, v * neumannVal[valueDim] * weight);
            }
        }
    }
}
