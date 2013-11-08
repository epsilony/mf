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
import java.util.List;
import net.epsilony.mf.model.MFNode;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawMixResult implements MixResult {

    List<MFNode> nodes;
    double[][] shapeFunctionValues;
    TIntArrayList nodesAssemblyIndes;

    @Override
    public List<MFNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<MFNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public double[][] getShapeFunctionValues() {
        return shapeFunctionValues;
    }

    public void setShapeFunctionValues(double[][] shapeFunctionValues) {
        this.shapeFunctionValues = shapeFunctionValues;
    }

    @Override
    public TIntArrayList getNodesAssemblyIndes() {
        return nodesAssemblyIndes;
    }

    public void setNodesAssemblyIndes(TIntArrayList nodesAssemblyIndes) {
        this.nodesAssemblyIndes = nodesAssemblyIndes;
    }

}
