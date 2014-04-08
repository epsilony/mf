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

import java.util.function.IntUnaryOperator;

import net.epsilony.mf.util.math.PartialValueTuple;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SimpShapeFunctionValue implements ShapeFunctionValue {

    private PartialValueTuple partialValueTuple;
    private IntUnaryOperator assemblyIndexGetter;

    public SimpShapeFunctionValue(PartialValueTuple partialValueTuple, IntUnaryOperator assemblyIndexGetter) {
        this.partialValueTuple = partialValueTuple;
        this.assemblyIndexGetter = assemblyIndexGetter;
    }

    public void setPartialValueTuple(PartialValueTuple partialValueTuple) {
        this.partialValueTuple = partialValueTuple;
    }

    public void setAssemblyIndexGetter(IntUnaryOperator assemblyIndexGetter) {
        this.assemblyIndexGetter = assemblyIndexGetter;
    }

    public SimpShapeFunctionValue() {
    }

    @Override
    public int size() {
        return partialValueTuple.size();
    }

    @Override
    public int getSpatialDimension() {
        return partialValueTuple.getSpatialDimension();
    }

    @Override
    public int getMaxPartialOrder() {
        return partialValueTuple.getMaxPartialOrder();
    }

    @Override
    public double get(int index, int partialIndex) {
        return partialValueTuple.get(index, partialIndex);
    }

    @Override
    public int getNodeAssemblyIndex(int index) {
        return assemblyIndexGetter.applyAsInt(index);
    }

    @Override
    public ShapeFunctionValue copy() {
        int[] nodesAsmIds = new int[size()];
        for (int i = 0; i < size(); i++) {
            nodesAsmIds[i] = assemblyIndexGetter.applyAsInt(i);
        }
        return new SimpShapeFunctionValue(partialValueTuple.copy(), (i) -> nodesAsmIds[i]);
    }
}
