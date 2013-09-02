/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import java.io.Serializable;
//import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
//import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangeAssemblerCore implements Serializable {

    protected int lagrangeNodesSize;
    protected TIntArrayList lagrangeAssemblyIndes;
    protected double[] lagrangeShapeFunctionValue;
    Assembler decorator;

    public int getLagrangeNodesSize() {
        return lagrangeNodesSize;
    }

    public TIntArrayList getLagrangeAssemblyIndes() {
        return lagrangeAssemblyIndes;
    }

    public double[] getLagrangeShapeFunctionValue() {
        return lagrangeShapeFunctionValue;
    }

    public void setLagrangeNodesSize(int lagrangeNodesSize) {
        this.lagrangeNodesSize = lagrangeNodesSize;
    }

    public void prepareSupply() {
        final int mainMatrixSize = getMainMatrixSize();
        for (int i = mainMatrixSize - lagrangeNodesSize * decorator.getDimension(); i < mainMatrixSize; i++) {
            decorator.getMainMatrix().set(i, i, 1);
        }
    }

    public void mergeWithBrotherSupply(Assembler otherAssembler) {
        int mainMatrixSize = getMainMatrixSize();
        Matrix mainMatrix = decorator.getMainMatrix();
        for (int i = mainMatrixSize - lagrangeNodesSize * decorator.getDimension(); i < mainMatrixSize; i++) {
            double lagDiag = mainMatrix.get(i, i);
            if (lagDiag > 0) {
                mainMatrix.set(i, i, lagDiag - 1);
            }
        }
    }

    protected int getMainMatrixSize() {
        return decorator.getDimension() * (decorator.getNodesNum() + lagrangeNodesSize);
    }

    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            double[] lagrangeShapeFunctionValue) {
        this.lagrangeAssemblyIndes = lagrangeAssemblyIndes;
        this.lagrangeShapeFunctionValue = lagrangeShapeFunctionValue;
    }

    public void assembleDirichlet() {
        final int nodeValueDimension = decorator.getDimension();
        double weight = decorator.getWeight();
        DenseVector mainVector = decorator.getMainVector();
        Matrix mainMatrix = decorator.getMainMatrix();
        double[] load = decorator.getLoad();
        boolean[] loadValidity = decorator.getLoadValidity();
        TIntArrayList nodesAssemblyIndes = decorator.getNodesAssemblyIndes();
        double[] testShapeFunctionValues = decorator.getTestShapeFunctionValues()[0];
        double[] trialShapeFunctionValues = decorator.getTrialShapeFunctionValues()[0];
        boolean upperSymmetric = decorator.isUpperSymmetric();
        for (int i = 0; i < lagrangeAssemblyIndes.size(); i++) {
            int lagIndex = lagrangeAssemblyIndes.getQuick(i);
            double lagShapeFunc = lagrangeShapeFunctionValue[i];
            double vecValue = lagShapeFunc * weight;
            for (int dim = 0; dim < nodeValueDimension; dim++) {
                if (loadValidity[dim]) {
                    mainVector.add(lagIndex * nodeValueDimension + dim, vecValue * load[dim]);
                }
            }
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int testIndex = nodesAssemblyIndes.getQuick(j);
                double testValue = testShapeFunctionValues[j];
                if (upperSymmetric) {
                    double matValue = lagShapeFunc * testValue * weight;

                    for (int dim = 0; dim < nodeValueDimension; dim++) {
                        int row = lagIndex * nodeValueDimension + dim;
                        int col = testIndex * nodeValueDimension + dim;
                        if (loadValidity[dim]) {
                            mainMatrix.add(col, row, matValue);                //upper symmetric
                            mainMatrix.set(row, row, 0);
                        }
                    }
                } else {
                    int trialIndex = nodesAssemblyIndes.getQuick(j);
                    double trialValue = trialShapeFunctionValues[j];
                    double matValueDownLeft = lagShapeFunc * trialValue * weight;
                    double matValueUpRight = lagShapeFunc * testValue * weight;
                    for (int dim = 0; dim < nodeValueDimension; dim++) {
                        int rowDownLeft = lagIndex * nodeValueDimension + dim;
                        int colDownLeft = trialIndex * nodeValueDimension + dim;
                        int rowUpRight = testIndex * nodeValueDimension + dim;
                        int colUpRight = rowDownLeft;
                        if (loadValidity[dim]) {
                            mainMatrix.add(rowDownLeft, colDownLeft, matValueDownLeft);
                            mainMatrix.add(rowUpRight, colUpRight, matValueUpRight);
                            mainMatrix.set(rowDownLeft, rowDownLeft, 0);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d, "
                + "mat dense/sym: %b/%b, "
                + "dirichlet nodes size: %d}",
                decorator.getNodesNum(),
                decorator.getDimension(),
                decorator.getVolumeDiffOrder(),
                decorator.getNeumannDiffOrder(),
                decorator.getDirichletDiffOrder(),
                decorator.isMatrixDense(),
                decorator.isUpperSymmetric(),
                getLagrangeNodesSize());
    }
}
