/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangleDirichletAssembler
        extends AbstractAssembler
        implements LagrangleAssembler {

    protected int lagrangeNodesSize;
    protected TIntArrayList lagrangeAssemblyIndes;
    protected double[] lagrangeShapeFunctionValue;

    @Override
    public void setMainMatrix(MFMatrix mainMatrix) {
        super.setMainMatrix(mainMatrix);
        prepareMainMatrixLarangeDiagConvention();
    }

    private void prepareMainMatrixLarangeDiagConvention() {
        final int mainMatrixSize = mainMatrix.numRows();
        for (int i = mainMatrixSize - lagrangeNodesSize * valueDimension; i < mainMatrixSize; i++) {
            mainMatrix.set(i, i, 1);
        }
    }

    @Override
    public int getRequiredMatrixSize() {
        return valueDimension * (nodesNum + lagrangeNodesSize);
    }

    @Override
    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            double[] lagrangeShapeFunctionValue) {
        this.lagrangeAssemblyIndes = lagrangeAssemblyIndes;
        this.lagrangeShapeFunctionValue = lagrangeShapeFunctionValue;
    }

    @Override
    public void assemble() {

        boolean upperSymmetric = mainMatrix.isUpperSymmetric();
        for (int i = 0; i < lagrangeAssemblyIndes.size(); i++) {
            int lagIndex = lagrangeAssemblyIndes.getQuick(i);
            double lagShapeFunc = lagrangeShapeFunctionValue[i];
            double vecValue = lagShapeFunc * weight;
            for (int dim = 0; dim < valueDimension; dim++) {
                if (loadValidity[dim]) {
                    mainVector.add(lagIndex * valueDimension + dim, 0, vecValue * load[dim]);
                }
            }
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int testIndex = nodesAssemblyIndes.getQuick(j);
                double testValue = testShapeFunctionValues[0][j];

                int trialIndex = nodesAssemblyIndes.getQuick(j);
                double trialValue = trialShapeFunctionValues[0][j];

                double matValueDownLeft = lagShapeFunc * trialValue * weight;
                double matValueUpRight = lagShapeFunc * testValue * weight;

                for (int dim = 0; dim < valueDimension; dim++) {
                    int rowDownLeft = lagIndex * valueDimension + dim;
                    int colDownLeft = trialIndex * valueDimension + dim;
                    int rowUpRight = testIndex * valueDimension + dim;
                    int colUpRight = rowDownLeft;
                    if (loadValidity[dim]) {
                        if (!upperSymmetric) {
                            mainMatrix.add(rowDownLeft, colDownLeft, matValueDownLeft);
                        }
                        mainMatrix.add(rowUpRight, colUpRight, matValueUpRight);
                        mainMatrix.set(rowDownLeft, rowDownLeft, 0);
                    }
                }
            }
        }
    }

    @Override
    public void setAllLagrangleNodesNum(int size) {
        this.lagrangeNodesSize = size;
    }

    @Override
    public int getLagrangeDimension() {
        return lagrangeNodesSize * valueDimension;
    }
}
