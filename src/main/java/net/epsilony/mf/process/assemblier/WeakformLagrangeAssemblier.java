/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WeakformLagrangeAssemblier<T extends WeakformLagrangeAssemblier> extends WeakformAssemblier<T> {

    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            TDoubleArrayList lagrangeShapeFunctionValue);

    public void setDirichletNodesNum(int diriNum);

    public int getDirichletNodesNum();
}
