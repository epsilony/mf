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
package net.epsilony.mf.process.post;

import java.util.ArrayList;
import java.util.Arrays;

import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.math.ArrayPartialValueTuple.RowForPartial;
import net.epsilony.mf.util.math.PartialValueTuple;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SimpPostProcessor {
    private final ArrayList<double[]> assemblyIndexToNodeValues;
    private final int valueDimension;
    private final int spatialDimension;
    private final MFMixer mixer;

    private RowForPartial partialTuple;

    public SimpPostProcessor(ArrayList<double[]> assemblyIndexToNodeValues, int valueDimension, int spatialDimension,
            MFMixer mixer) {
        this.assemblyIndexToNodeValues = assemblyIndexToNodeValues;
        this.valueDimension = valueDimension;
        this.spatialDimension = spatialDimension;
        this.mixer = mixer;
        setMaxPartialOrder(0);
    }

    public void setBoundary(GeomUnit boundary) {
        mixer.setBoundary(boundary);
    }

    public void setCenter(double[] center) {
        mixer.setCenter(center);
    }

    public PartialValueTuple value() {
        ShapeFunctionValue mix = mixer.mix();
        for (int pd = 0; pd < mix.partialSize(); pd++) {
            double[] data = partialTuple.getData()[pd];
            Arrays.fill(data, 0);
            for (int nd = 0; nd < mix.size(); nd++) {
                double[] ndValues = assemblyIndexToNodeValues.get(mix.getNodeAssemblyIndex(nd));
                double shapeFuncVal = mix.valueByIndexAndPartial(nd, pd);
                for (int vd = 0; vd < valueDimension; vd++) {
                    data[vd] += ndValues[vd] * shapeFuncVal;
                }
            }
        }
        return partialTuple;
    }

    public void setUnitOutNormal(double[] unitNormal) {
        mixer.setUnitOutNormal(unitNormal);
    }

    public void setMaxPartialOrder(int order) {
        if (partialTuple == null || partialTuple.getMaxPartialOrder() != order) {
            partialTuple = new RowForPartial(valueDimension, spatialDimension, order);
        }
        mixer.setDiffOrder(order);
    }

}
