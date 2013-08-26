/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
//import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.tb.MiscellaneousUtils;
//import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractLagrangeAssembler<T extends AbstractLagrangeAssembler<T> & LagrangeAssembler<T>>
        extends AbstractAssembler<T>
        implements LagrangeAssembler<T> {

    protected int dirichletNodesSize;
    protected TIntArrayList lagrangeAssemblyIndes;
    protected double[] lagrangeShapeFunctionValue;

    @Override
    public int getDirichletNodesSize() {
        return dirichletNodesSize;
    }

    @Override
    public void setDirichletNodesSize(int dirichletNodesSize) {
        this.dirichletNodesSize = dirichletNodesSize;
    }

    //    protected DenseMatrix constitutiveLawMatrixCopy;
    //    protected DenseMatrix constitutiveLawMatrixCopy;
    @Override
    public void prepare() {
        super.prepare();
        final int mainMatrixSize = getMainMatrixSize();
        for (int i = mainMatrixSize - dirichletNodesSize; i < mainMatrixSize; i++) {
            mainMatrix.set(i, i, 1);
        }
    }

    @Override
    public void mergeWithBrother(Assembler otherAssembler) {
        super.mergeWithBrother(otherAssembler);
        int mainMatrixSize = getMainMatrixSize();
        for (int i = mainMatrixSize - dirichletNodesSize; i < mainMatrixSize; i++) {
            double lagDiag = mainMatrix.get(i, i);
            if (lagDiag > 0) {
                mainMatrix.set(i, i, lagDiag - 1);
            }
        }
    }

    @Override
    protected int getMainMatrixSize() {
        return getNodeValueDimension() * (nodesNum + dirichletNodesSize);
    }

    @Override
    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            double[] lagrangeShapeFunctionValue) {
        this.lagrangeAssemblyIndes = lagrangeAssemblyIndes;
        this.lagrangeShapeFunctionValue = lagrangeShapeFunctionValue;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d, "
                + "mat dense/sym: %b/%b, "
                + "dirichlet nodes size: %d}",
                getNodesNum(),
                getNodeValueDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmetric(),
                getDirichletNodesSize());
    }
//    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
//        this.constitutiveLaw = constitutiveLaw;
//        constitutiveLawMatrixCopy = new DenseMatrix(constitutiveLaw.getMatrix());
//    }
}
