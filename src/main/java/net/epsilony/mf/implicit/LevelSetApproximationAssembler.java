/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.process.assembler.AbstractLagrangeAssembler;
import net.epsilony.tb.common_func.RadialBasisCore;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LevelSetApproximationAssembler extends AbstractLagrangeAssembler {

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
    public boolean isUpperSymmetric() {
        return true;
    }

    @Override
    public void assembleVolume() {
        double aimFunc = load[0];
        double wholeWeight = weight * weightFunction.valuesByDistance(aimFunc, weightFunctionValue)[0];
        double[] rShapeFunc = trialShapeFunctionValues[0];
        double[] lShapeFunc = testShapeFunctionValues[0];
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i);
            double rowShapeFunc = lShapeFunc[i];
            mainVector.add(row, wholeWeight * aimFunc * rowShapeFunc);
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j);
                if (isUpperSymmetric() && row > col) {
                    continue;
                }
                double colShapeFunc = rShapeFunc[j];
                mainMatrix.add(row, col, wholeWeight * rowShapeFunc * colShapeFunc);
            }
        }
    }

    @Override
    public void assembleDirichlet() {
        if (!loadValidity[0]) {
            return;
        }
        double aimFunc = load[0];
        double vectorWeight = aimFunc * weight;
        double[] lShapeFunc = testShapeFunctionValues[0];
        TIntArrayList lagrangeAssemblyIndes = core.getLagrangeAssemblyIndes();
        double[] lagrangeShapeFunctionValue = core.getLagrangeShapeFunctionValue();
        for (int j = 0; j < lagrangeAssemblyIndes.size(); j++) {
            int col = lagrangeAssemblyIndes.getQuick(j);
            double colShapeFunc = lagrangeShapeFunctionValue[j];
            mainVector.add(col, vectorWeight * colShapeFunc);
            for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
                int row = nodesAssemblyIndes.getQuick(i);
                double rowShapeFunc = lShapeFunc[i];
                double matrixValue = rowShapeFunc * colShapeFunc * weight;
                mainMatrix.add(row, col, matrixValue);
                if (!isUpperSymmetric()) {
                    mainMatrix.add(col, row, matrixValue);
                }
                int diag = Math.max(col, row);
                mainMatrix.set(diag, diag, 0);
            }
        }
    }

    @Override
    public void assembleNeumann() {
    }

    @Override
    public int getValueDimension() {
        return 1;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, "
                + "mat dense/sym: %b/%b, dirichlet lagrangian dimension size: %d  weight function: %s}",
                getNodesNum(),
                getValueDimension(),
                isMatrixDense(),
                isUpperSymmetric(),
                getLagrangeNodesSize(),
                weightFunction);
    }

    @Override
    public void setSpatialDimension(int dim) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
