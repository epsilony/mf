/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import gnu.trove.list.array.TIntArrayList;
import java.io.Serializable;
import java.util.Collection;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.tb.solid.Line;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LinearLagrangeDirichletProcessor implements Serializable {

    TIntArrayList lagrangeAssemblyIndes = new TIntArrayList();
    double[] lagrangeShapeFunctionValue = new double[2];

    public void process(MFBoundaryIntegratePoint pt) {
        lagrangeAssemblyIndes.resetQuick();
        lagrangeAssemblyIndes.ensureCapacity(2);
        Line line = (Line) pt.getBoundary();

        MFNode start = (MFNode) line.getStart();
        MFNode end = (MFNode) line.getEnd();
        lagrangeAssemblyIndes.add(start.getLagrangeAssemblyIndex());
        lagrangeAssemblyIndes.add(end.getLagrangeAssemblyIndex());
        lagrangeShapeFunctionValue[0] = 1 - pt.getBoundaryParameter();
        lagrangeShapeFunctionValue[1] = pt.getBoundaryParameter();
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
}
