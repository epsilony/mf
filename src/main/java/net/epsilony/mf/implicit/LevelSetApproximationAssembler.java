/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

import net.epsilony.mf.process.assembler.AbstractAssembler;
import net.epsilony.tb.common_func.RadialBasisCore;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LevelSetApproximationAssembler extends AbstractAssembler {

    RadialBasisCore weightFunction;

    public RadialBasisCore getWeightFunction() {
        return weightFunction;
    }

    public void setWeightFunction(RadialBasisCore weightFunction) {
        this.weightFunction = weightFunction;
        weightFunction.setDiffOrder(0);
    }
    private double[] weightFunctionValue = new double[1];

    @Override
    public void assemble() {
        double aimFunc = load[0];
        double wholeWeight = weight * weightFunction.valuesByDistance(aimFunc, weightFunctionValue)[0];
        double[] rShapeFunc = trialShapeFunctionValues[0];
        double[] lShapeFunc = testShapeFunctionValues[0];
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i);
            double rowShapeFunc = lShapeFunc[i];
            mainVector.add(row, 0, wholeWeight * aimFunc * rowShapeFunc);
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j);
                if (mainMatrix.isUpperSymmetric() && row > col) {
                    continue;
                }
                double colShapeFunc = rShapeFunc[j];
                mainMatrix.add(row, col, wholeWeight * rowShapeFunc * colShapeFunc);
            }
        }
    }
}
