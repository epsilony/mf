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

import java.util.function.Supplier;

import net.epsilony.mf.process.assembler.T2Value;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.math.PartialTuple;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class AdjustedT2Value implements T2Value {

    private ShapeFunctionValue base;
    private PartialTuple testAdjust;
    private PartialTuple trialAdjust;

    private final ShapeFunctionValue testAdjusted = new AdjustedShapeFunction(this::getTestAdjust);
    private final ShapeFunctionValue trialAdjusted = new AdjustedShapeFunction(this::getTrialAdjust);

    public void setBase(ShapeFunctionValue base) {
        this.base = base;
    }

    public PartialTuple getTestAdjust() {
        return testAdjust;
    }

    public void setTestAdjust(PartialTuple testAdjust) {
        this.testAdjust = testAdjust;
    }

    public PartialTuple getTrialAdjust() {
        return trialAdjust;
    }

    public void setTrialAdjust(PartialTuple trialAdjust) {
        this.trialAdjust = trialAdjust;
    }

    @Override
    public ShapeFunctionValue getTestValue() {
        if (testAdjust == null) {
            return base;
        } else {
            return testAdjusted;
        }
    }

    @Override
    public ShapeFunctionValue getTrialValue() {
        if (null == trialAdjust) {
            return base;
        } else {
            return trialAdjusted;
        }
    }

    private class AdjustedShapeFunction implements ShapeFunctionValue {

        private final Supplier<PartialTuple> adjustGetter;

        public AdjustedShapeFunction(Supplier<PartialTuple> adjustGetter) {
            this.adjustGetter = adjustGetter;
        }

        @Override
        public double get(int index, int partialIndex) {
            return base.get(index, partialIndex) + adjustGetter.get().get(index, partialIndex);
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
        public int getMaxPartialOrder() {
            return base.getMaxPartialOrder();
        }

        @Override
        public int getNodeAssemblyIndex(int index) {
            return base.getNodeAssemblyIndex(index);
        }

    }
}
