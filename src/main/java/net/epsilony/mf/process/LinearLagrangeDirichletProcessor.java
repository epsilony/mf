/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.process;

import gnu.trove.list.array.TIntArrayList;
import java.io.Serializable;
import java.util.Collection;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.process.integrate.unit.MFBoundaryIntegratePoint;
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
        if (pt.getBoundary() instanceof Line) {
            processLine(pt);
        } else if (pt.getBoundary() instanceof MFNode) {
            processNode(pt);
        }
    }

    private void processLine(MFBoundaryIntegratePoint pt) {
        Line line = (Line) pt.getBoundary();

        MFNode start = (MFNode) line.getStart();
        MFNode end = (MFNode) line.getEnd();
        lagrangeAssemblyIndes.add(start.getLagrangeAssemblyIndex());
        lagrangeAssemblyIndes.add(end.getLagrangeAssemblyIndex());
        lagrangeShapeFunctionValue[0] = 1 - pt.getBoundaryParameter();
        lagrangeShapeFunctionValue[1] = pt.getBoundaryParameter();
    }

    private void processNode(MFBoundaryIntegratePoint pt) {
        MFNode node = (MFNode) pt.getBoundary();

        lagrangeAssemblyIndes.add(node.getLagrangeAssemblyIndex());
        lagrangeShapeFunctionValue[0] =1;
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
