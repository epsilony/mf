/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalLagrangeAssembler
        extends AbstractMechanicalAssembler<MechanicalLagrangeAssembler>
        implements LagrangeAssembler<MechanicalLagrangeAssembler> {

    int dirichletNodesSize;
    TIntArrayList lagrangeAssemblyIndes;
    double[] lagrangeShapeFunctionValue;

    @Override
    public void prepare() {
        super.prepare();
        final int mainMatrixSize = getMainMatrixSize();
        for (int i = mainMatrixSize - dirichletNodesSize * getNodeValueDimension(); i < mainMatrixSize; i++) {
            mainMatrix.set(i, i, 1);
        }
    }

    @Override
    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            double[] lagrangeShapeFunctionValue) {
        this.lagrangeAssemblyIndes = lagrangeAssemblyIndes;
        this.lagrangeShapeFunctionValue = lagrangeShapeFunctionValue;
    }

    @Override
    public void assembleDirichlet() {
        final int nodeValueDimension = getNodeValueDimension();
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
                double testValue = testShapeFunctionValues[0][j];
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
                    double trialValue = trialShapeFunctionValues[0][j];
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
//        for (int i = 0; i < getMainMatrixSize() - dirichletNodesSize * 2; i++) {
//            for (int j = getMainMatrixSize() - dirichletNodesSize * 2; j < getMainMatrixSize(); j++) {
//                System.out.print(String.format("% 6.5e ", mainMatrix.get(i, j)));
//            }
//            System.out.println("");
//        }
//        System.out.println("------------------------------");
//
//        for (int i = getMainMatrixSize() - dirichletNodesSize * 2; i < getMainMatrixSize(); i++) {
//            System.out.println("i,i = " + mainMatrix.get(i, i));
//        }
    }

    @Override
    public int getDirichletNodesSize() {
        return dirichletNodesSize;
    }

    @Override
    public void setDirichletNodesSize(int dirichletNodesSize) {
        this.dirichletNodesSize = dirichletNodesSize;
    }

    @Override
    protected int getMainMatrixSize() {
        return getNodeValueDimension() * (nodesNum + dirichletNodesSize);
    }

    @Override
    public MechanicalLagrangeAssembler synchronizeClone() {
        MechanicalLagrangeAssembler result = new MechanicalLagrangeAssembler();
        result.setConstitutiveLaw(constitutiveLaw);
        result.setDirichletNodesSize(dirichletNodesSize);
        result.setMatrixDense(isMatrixDense());
        result.setNodesNum(nodesNum);
        result.prepare();
        return result;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d,"
                + " mat dense/sym: %b/%b,"
                + " dirichlet nodes size: %d}",
                getNodesNum(),
                getNodeValueDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmetric(),
                getDirichletNodesSize());
    }

    @Override
    public void mergeWithBrother(Assembler otherAssembler) {
        super.mergeWithBrother(otherAssembler);
        int mainMatrixSize = getMainMatrixSize();
        for (int i = mainMatrixSize - dirichletNodesSize * getNodeValueDimension(); i < mainMatrixSize; i++) {
            double lagDiag = mainMatrix.get(i, i);
            if (lagDiag > 0) {
                mainMatrix.set(i, i, lagDiag - 1);
            }
        }
    }

    @Override
    public int getNodeValueDimension() {
        return 2;
    }
}
