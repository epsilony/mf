/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractLagrangeAssembler
        extends AbstractAssembler
        implements LagrangeAssembler {

    protected LagrangeAssemblerCore core = new LagrangeAssemblerCore();

    protected AbstractLagrangeAssembler() {
        core.decorator = this;
    }

    @Override
    public TIntArrayList getLagrangeAssemblyIndes() {
        return core.getLagrangeAssemblyIndes();
    }

    @Override
    public double[] getLagrangeShapeFunctionValue() {
        return core.getLagrangeShapeFunctionValue();
    }

    @Override
    public int getLagrangeNodesSize() {
        return core.getLagrangeNodesSize();
    }

    @Override
    public void setLagrangeNodesSize(int dirichletNodesSize) {
        core.setLagrangeNodesSize(dirichletNodesSize);
    }

    @Override
    public void prepare() {
        super.prepare();
        core.prepareSupply();
    }

    @Override
    public void mergeWithBrother(Assembler otherAssembler) {
        super.mergeWithBrother(otherAssembler);
        core.mergeWithBrotherSupply(otherAssembler);
    }

    @Override
    protected int getMainMatrixSize() {
        return core.getMainMatrixSize();
    }

    @Override
    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            double[] lagrangeShapeFunctionValue) {
        core.setLagrangeShapeFunctionValue(lagrangeAssemblyIndes, lagrangeShapeFunctionValue);
    }

    @Override
    public void assembleDirichlet() {
        core.assembleDirichlet();
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d, "
                + "mat dense/sym: %b/%b, "
                + "dirichlet nodes size: %d, "
                + "main matrix size: %d}",
                getNodesNum(),
                getDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmetric(),
                getLagrangeNodesSize(),
                getMainMatrixSize());
    }
}
