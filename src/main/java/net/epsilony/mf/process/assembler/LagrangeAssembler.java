/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface LagrangeAssembler<T extends LagrangeAssembler> extends Assembler<T> {

    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            TDoubleArrayList lagrangeShapeFunctionValue);

    public void setDirichletNodesNum(int diriNum);

    public int getDirichletNodesNum();
}
