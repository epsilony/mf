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

    TIntArrayList lagrangeAssemblyIndes = new TIntArrayList();
    double[] lagrangeShapeFunctionValue = new double[2];

    public void process(MFQuadraturePoint pt) {
        lagrangeAssemblyIndes.resetQuick();
        lagrangeAssemblyIndes.ensureCapacity(2);

        MFNode start = (MFNode) pt.segment.getStart();
        MFNode end = (MFNode) pt.segment.getEnd();
        lagrangeAssemblyIndes.add(start.getLagrangeAssemblyIndex());
        lagrangeAssemblyIndes.add(end.getLagrangeAssemblyIndex());
        lagrangeShapeFunctionValue[0] = 1 - pt.segmentParameter;
        lagrangeShapeFunctionValue[1] = pt.segmentParameter;
    }

    public TIntArrayList getLagrangeAssemblyIndes() {
        return lagrangeAssemblyIndes;
    }

    public double[] getLagrangeShapeFunctionValue() {
        return lagrangeShapeFunctionValue;
    }

    public static int calcLagrangeNodesNum(Collection<? extends MFNode> nodes) {
        int size = 0;
        for (MFNode node : nodes) {
            if (node.getLagrangeAssemblyIndex() >= 0) {
                size++;
            }
        }
        return size;
    }

    @Override
    public LinearLagrangeDirichletProcessor synchronizeClone() {
        return new LinearLagrangeDirichletProcessor();
    }
}