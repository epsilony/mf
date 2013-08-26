/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TIntArrayList;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface LagrangeAssembler<T extends LagrangeAssembler> extends Assembler<T> {

    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            double[] lagrangeShapeFunctionValue);

    public void setDirichletNodesSize(int size);

    public int getDirichletNodesSize();
}
