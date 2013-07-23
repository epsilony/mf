/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.project.quadrature_task.MFQuadraturePoint;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.Collection;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LinearLagrangeDirichletProcessor implements SynchronizedClonable<LinearLagrangeDirichletProcessor> {

    TIntArrayList lagrangleAssemblyIndes = new TIntArrayList();
    TDoubleArrayList lagrangleShapeFunctionValue = new TDoubleArrayList();

    public void process(MFQuadraturePoint pt) {
        lagrangleAssemblyIndes.resetQuick();
        lagrangleShapeFunctionValue.resetQuick();
        lagrangleAssemblyIndes.ensureCapacity(2);
        lagrangleShapeFunctionValue.ensureCapacity(2);
        MFNode start = (MFNode) pt.segment.getStart();
        MFNode end = (MFNode) pt.segment.getEnd();
        lagrangleAssemblyIndes.addAll(start.getLagrangeAssemblyIndes());
        lagrangleAssemblyIndes.addAll(end.getLagrangeAssemblyIndes());
        lagrangleShapeFunctionValue.add(1 - pt.segmentParameter);
        lagrangleShapeFunctionValue.add(pt.segmentParameter);
    }

    public TIntArrayList getLagrangleAssemblyIndes() {
        return lagrangleAssemblyIndes;
    }

    public TDoubleArrayList getLagrangleShapeFunctionValue() {
        return lagrangleShapeFunctionValue;
    }

    public static int calcDirichletDimensionSize(Collection<? extends MFNode> nodes) {
        int size = 0;
        for (MFNode node : nodes) {
            int[] lagrangeAssemblyIndes = node.getLagrangeAssemblyIndes();
            if (null != lagrangeAssemblyIndes) {
                for (int i : lagrangeAssemblyIndes) {
                    if (i >= 0) {
                        size++;
                    }
                }
            }
        }
        return size;
    }

    @Override
    public LinearLagrangeDirichletProcessor synchronizeClone() {
        return new LinearLagrangeDirichletProcessor();
    }
}
