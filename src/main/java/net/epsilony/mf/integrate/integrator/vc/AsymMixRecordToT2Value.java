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

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.IntFunction;

import net.epsilony.mf.process.assembler.T2Value;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.math.ArrayPartialValue;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.math.PartialVectorFunction;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class AsymMixRecordToT2Value implements Function<IntegralMixRecordEntry, T2Value> {
    private final AdjustedT2Value adjustedT2Value = new AdjustedT2Value();
    private IntFunction<VCNode> assemblyIndexToIntegralNode;
    private final TestAdjustValue testAdjust = new TestAdjustValue();
    private IntFunction<PartialVectorFunction> assemblyIndexToVCBasesFunction;

    public AsymMixRecordToT2Value() {
        adjustedT2Value.setTestAdjust(testAdjust);
    }

    public void setAssemblyIndexToIntegralNode(IntFunction<VCNode> assemblyIndexToIntegralNode) {
        this.assemblyIndexToIntegralNode = assemblyIndexToIntegralNode;
    }

    public void setAssemblyIndexToVCBasesFunction(IntFunction<PartialVectorFunction> assemblyIndexToVCBasesFunction) {
        this.assemblyIndexToVCBasesFunction = assemblyIndexToVCBasesFunction;
    }

    @Override
    public T2Value apply(IntegralMixRecordEntry entry) {
        ShapeFunctionValue base = entry.getShapeFunctionValue();
        adjustedT2Value.setBase(base);
        testAdjust.refresh(base, entry.getGeomPoint().getCoord());
        return adjustedT2Value;
    }

    private class TestAdjustValue implements PartialTuple {
        final ArrayList<ArrayPartialValue> dataBuffer = new ArrayList<>();
        private ShapeFunctionValue base;

        public void refresh(ShapeFunctionValue base, double[] coord) {
            this.base = base;
            int spatialDimension = base.getSpatialDimension();
            int maxPartialOrder = base.getMaxPartialOrder();
            for (int i = 0; i < base.size(); i++) {
                ArrayPartialValue value;
                if (dataBuffer.size() > i) {
                    value = dataBuffer.get(i);
                    if (value.getMaxPartialOrder() != maxPartialOrder
                            || value.getSpatialDimension() != spatialDimension) {
                        value = new ArrayPartialValue(spatialDimension, maxPartialOrder);
                        dataBuffer.set(i, value);
                    }
                } else {
                    value = new ArrayPartialValue(spatialDimension, maxPartialOrder);
                    dataBuffer.add(value);
                }

                int nodeAssemblyIndex = base.getNodeAssemblyIndex(i);
                PartialVectorFunction vcBasesFunction = assemblyIndexToVCBasesFunction.apply(nodeAssemblyIndex);
                vcBasesFunction.setMaxPartialOrder(maxPartialOrder);
                PartialTuple vcBasesValue = vcBasesFunction.value(coord);
                double[] vc = assemblyIndexToIntegralNode.apply(nodeAssemblyIndex).getVC();
                dot(vcBasesValue, vc, value);
            }
        }

        private void dot(PartialTuple vcBasesValue, double[] vc, ArrayPartialValue result) {
            for (int pd = 0; pd < result.partialSize(); pd++) {
                double v = 0;
                for (int i = 0; i < vc.length; i++) {
                    v += vc[i] * vcBasesValue.get(i, pd);
                }
                result.set(pd, v);
            }
        }

        @Override
        public int size() {
            return base.size();
        }

        @Override
        public int getSpatialDimension() {
            return base.getSpatialDimension();
        }

        @Override
        public double get(int index, int partialIndex) {
            return dataBuffer.get(index).get(partialIndex);
        }

        @Override
        public int getMaxPartialOrder() {
            return base.getMaxPartialOrder();
        }

    }
}
