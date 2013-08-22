/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TDoubleArrayList;
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

    protected int dirichletDimensionSize;
    protected TIntArrayList lagrangeAssemblyIndes;
    protected TDoubleArrayList lagrangeShapeFunctionValue;
//    protected ConstitutiveLaw constitutiveLaw;
//    protected DenseMatrix constitutiveLawMatrixCopy;

    @Override
    protected int getMainMatrixSize() {
        return getNodeValueDimension() * nodesNum + dirichletDimensionSize;
    }

    @Override
    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            TDoubleArrayList lagrangeShapeFunctionValue) {
        this.lagrangeAssemblyIndes = lagrangeAssemblyIndes;
        this.lagrangeShapeFunctionValue = lagrangeShapeFunctionValue;
    }

    @Override
    public int getDirichletDimensionSize() {
        return dirichletDimensionSize;
    }

    @Override
    public void setDirichletDimensionSize(int dirichletDimensionSize) {
        this.dirichletDimensionSize = dirichletDimensionSize;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d, "
                + "mat dense/sym: %b/%b, "
                + "dirichlet dimension size: %d}",
                getNodesNum(),
                getNodeValueDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmetric(),
                getDirichletDimensionSize());
    }

//    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
//        this.constitutiveLaw = constitutiveLaw;
//        constitutiveLawMatrixCopy = new DenseMatrix(constitutiveLaw.getMatrix());
//    }
}
