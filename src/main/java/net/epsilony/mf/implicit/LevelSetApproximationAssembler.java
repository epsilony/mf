/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

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
        double[] shapeFunc = shapeFunctionValues[0];
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i);
            double rowShapeFunc = shapeFunc[i];
            mainVector.add(row, wholeWeight * aimFunc * rowShapeFunc);
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j);
                if (isUpperSymmetric() && row > col) {
                    continue;
                }
                double colShapeFunc = shapeFunc[j];
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
        double[] shapeFunc = shapeFunctionValues[0];

        for (int j = 0; j < lagrangeAssemblyIndes.size(); j++) {
            int col = lagrangeAssemblyIndes.getQuick(j);
            double colShapeFunc = lagrangeShapeFunctionValue.getQuick(j);
            mainVector.add(col, -vectorWeight * colShapeFunc);
            for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
                int row = nodesAssemblyIndes.getQuick(i);
                double rowShapeFunc = shapeFunc[i];
                double matrixValue = -rowShapeFunc * colShapeFunc * weight;
                mainMatrix.add(row, col, matrixValue);
                if (!isUpperSymmetric()) {
                    mainMatrix.add(col, row, matrixValue);
                }
            }
        }
    }

    @Override
    public void assembleNeumann() {
    }

    @Override
    public int getVolumeDiffOrder() {
        return 0;
    }

    @Override
    public int getDirichletDiffOrder() {
        return 0;
    }

    @Override
    public int getNodeValueDimension() {
        return 1;
    }

    @Override
    public LevelSetApproximationAssembler synchronizeClone() {
        LevelSetApproximationAssembler result = new LevelSetApproximationAssembler();
        result.setWeightFunction(weightFunction.synchronizeClone());
        result.setNodesNum(nodesNum);
        result.setDirichletDimensionSize(dirichletDimensionSize);
        result.prepare();
        return result;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D: %d/%d/%d, "
                + "mat dense/sym: %b/%b, dirichlet lagrangian dimension size: %d  weight function: %s}",
                getNodesNum(),
                getNodeValueDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmetric(),
                getDirichletDimensionSize(),
                weightFunction);
    }
}
