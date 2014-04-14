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
package net.epsilony.mf.integrate.integrator.vc;

import java.util.Collection;
import java.util.function.IntFunction;

import net.epsilony.mf.model.MFNode;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CommonVCAssemblyIndexMap {
    private VCNode[] vcNodesByAssemblyIndex;
    private MFNode[] mfNodesByAssemblyIndex;
    private int spatialDimension;
    private IntFunction<? extends VCNode> vcNodeFactoryByAssemblyIndex;

    public void setNodes(Collection<? extends MFNode> nodes) {
        vcNodesByAssemblyIndex = new VCNode[nodes.size()];
        mfNodesByAssemblyIndex = new MFNode[nodes.size()];
        for (MFNode node : nodes) {
            int asmId = node.getAssemblyIndex();
            mfNodesByAssemblyIndex[asmId] = node;
            vcNodesByAssemblyIndex[asmId] = vcNodeFactoryByAssemblyIndex.apply(asmId);
        }
    }

    public int getSpatialDimension() {
        return spatialDimension;
    }

    public void setSpatialDimension(int spatialDimension) {
        this.spatialDimension = spatialDimension;
    }

    public double[] getMFNodeCoord(int assemblyIndex) {
        return mfNodesByAssemblyIndex[assemblyIndex].getCoord();
    }

    public double getInfluenceRadius(int assemblyIndex) {
        return mfNodesByAssemblyIndex[assemblyIndex].getInfluenceRadius();
    }

    public VCNode getVCNode(int assemblyIndex) {
        return vcNodesByAssemblyIndex[assemblyIndex];
    }

    public void setVcNodeFactoryByAssemblyIndex(IntFunction<? extends VCNode> vcNodeFactoryByAssemblyIndex) {
        this.vcNodeFactoryByAssemblyIndex = vcNodeFactoryByAssemblyIndex;
    }

    public void solveVCNodes() {
        for (VCNode nd : vcNodesByAssemblyIndex) {
            nd.solve();
        }
    }

}
