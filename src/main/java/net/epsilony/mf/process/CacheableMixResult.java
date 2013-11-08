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
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.analysis.WithDiffOrderUtil;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class CacheableMixResult implements MixResult {

    public CacheableMixResult() {
        int maxDiffSize = WithDiffOrderUtil.outputLength(3, 1);
        pools = new ArrayList<>(maxDiffSize);
        for (int i = 0; i < maxDiffSize; i++) {
            pools.add(new TIntObjectHashMap<double[][]>());
        }
    }

    @Override
    public List<MFNode> getNodes() {
        return nodes;
    }

    @Override
    public double[][] getShapeFunctionValues() {
        prepare();
        return shapeFunctionValues;
    }

    @Override
    public TIntArrayList getNodesAssemblyIndes() {
        return nodesAssemblyIndes;
    }

    private void prepare() {
        if (dimensionOrDiffOrderChanged) {
            pool = pools.get(WithDiffOrderUtil.outputLength(dimension, diffOrder) - 1);
            nodesSizeChanged = true;
        }
        if (nodesSizeChanged) {
            shapeFunctionValues = pool.get(nodesSize);
            if (null == shapeFunctionValues) {
                shapeFunctionValues = new double[WithDiffOrderUtil.outputLength(dimension, diffOrder)][nodesSize];
            }
            pool.put(nodesSize, shapeFunctionValues);
        }
        dimensionOrDiffOrderChanged = false;
        nodesSizeChanged = false;
    }

    private List<MFNode> nodes;
    private TIntArrayList nodesAssemblyIndes = new TIntArrayList(50);
    private double[][] shapeFunctionValues;
    private ArrayList<TIntObjectHashMap<double[][]>> pools;
    private TIntObjectHashMap<double[][]> pool;
    private boolean nodesSizeChanged = true;
    private boolean dimensionOrDiffOrderChanged = true;
    private int nodesSize = -1;
    private int dimension = 2;
    private int diffOrder = 0;

    public void setNodes(List<MFNode> nodes) {
        this.nodes = nodes;
        nodesAssemblyIndes.resetQuick();
        nodesAssemblyIndes.ensureCapacity(nodes.size());
        for (MFNode nd : nodes) {
            nodesAssemblyIndes.add(nd.getAssemblyIndex());
        }
        setNodesSize(nodes.size());
    }

    private void setNodesSize(int nodesSize) {
        if (this.nodesSize == nodesSize) {
            return;
        }
        this.nodesSize = nodesSize;
        nodesSizeChanged = true;
    }

    public void setDiffOrder(int diffOrder) {
        if (diffOrder < 0 || diffOrder > 1) {
            throw new IllegalArgumentException("diffOrder should 0 or 1, not " + diffOrder);
        }
        if (this.diffOrder == diffOrder) {
            return;
        }
        this.diffOrder = diffOrder;
        dimensionOrDiffOrderChanged = true;
    }

    public void setDimension(int dimension) {
        if (dimension < 1) {
            throw new IllegalArgumentException("dimension should be positve, not " + dimension);
        }
        if (this.dimension == dimension) {
            return;
        }
        this.dimension = dimension;
        dimensionOrDiffOrderChanged = true;
    }
}
