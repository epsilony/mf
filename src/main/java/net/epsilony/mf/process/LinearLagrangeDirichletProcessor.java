/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.Collection;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LinearLagrangeDirichletProcessor implements SynchronizedClonable<LinearLagrangeDirichletProcessor> {

    TIntArrayList lagrangleAssemblyIndes = new TIntArrayList();
    TDoubleArrayList lagrangleShapeFunctionValue = new TDoubleArrayList();

    public void process(WeakformQuadraturePoint pt) {
        lagrangleAssemblyIndes.resetQuick();
        lagrangleShapeFunctionValue.resetQuick();
        lagrangleAssemblyIndes.ensureCapacity(2);
        lagrangleShapeFunctionValue.ensureCapacity(2);
        MFNode start = (MFNode) pt.segment.getStart();
        MFNode end = (MFNode) pt.segment.getEnd();
        lagrangleAssemblyIndes.add(start.getLagrangeAssemblyIndex());
        lagrangleAssemblyIndes.add(end.getLagrangeAssemblyIndex());
        lagrangleShapeFunctionValue.add(1 - pt.segmentParameter);
        lagrangleShapeFunctionValue.add(pt.segmentParameter);
    }

    public TIntArrayList getLagrangleAssemblyIndes() {
        return lagrangleAssemblyIndes;
    }

    public TDoubleArrayList getLagrangleShapeFunctionValue() {
        return lagrangleShapeFunctionValue;
    }

    public static int calcDirichletNodesSize(Collection<? extends MFNode> nodes) {
        int num = 0;
        for (MFNode node : nodes) {
            if (node.getLagrangeAssemblyIndex() >= 0) {
                num++;
            }
        }
        return num;
    }

    @Override
    public LinearLagrangeDirichletProcessor synchronizeClone() {
        return new LinearLagrangeDirichletProcessor();
    }
}
