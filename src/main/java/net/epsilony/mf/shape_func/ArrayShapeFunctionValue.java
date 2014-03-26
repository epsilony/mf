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
package net.epsilony.mf.shape_func;

import net.epsilony.mf.process.assembler.ShapeFunctionValue;
import net.epsilony.tb.analysis.WithDiffOrderUtil;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ArrayShapeFunctionValue implements ShapeFunctionValue {
    private final int spatialDimension;
    private final int maxPdOrder;
    private final double[][] data;
    private final int[] nodesAsmIds;

    @Override
    public double getValue(int nd, int pd) {
        return data[pd][nd];
    }

    @Override
    public int getNodeAssemblyIndex(int nd) {
        return nodesAsmIds[nd];
    }

    @Override
    public int getNodesSize() {
        return data[0].length;
    }

    @Override
    public int getSpatialDimension() {
        return spatialDimension;
    }

    @Override
    public int getMaxPdOrder() {
        return maxPdOrder;
    }

    @Override
    public double[][] arrayForm() {
        return data;
    }

    @Override
    public int[] nodesAsmIds() {
        return nodesAsmIds;
    }

    public ArrayShapeFunctionValue(int spatialDimension, int maxPdOrder, double[][] data, int[] nodesAsmIds) {
        this.spatialDimension = spatialDimension;
        this.maxPdOrder = maxPdOrder;
        int diffSize = WithDiffOrderUtil.outputLength(spatialDimension, maxPdOrder);
        this.data = data;
        if (data.length != diffSize) {
            throw new IllegalArgumentException();
        }
        this.nodesAsmIds = nodesAsmIds;
    }

}
