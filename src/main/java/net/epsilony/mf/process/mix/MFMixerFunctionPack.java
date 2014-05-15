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
package net.epsilony.mf.process.mix;

import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.math.ArrayPartialValue;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.tuple.SimpTwoTuple;
import net.epsilony.mf.util.tuple.TwoTuple;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFMixerFunctionPack {
    private MFMixer mixer;
    private double[] parameters;
    private int diffOrder;
    private int spatialDimension = 2;

    public PartialValue partialValue(double[] coord) {
        return valuePack(coord).getFirst();
    }

    private ArrayPartialValue partialValue;
    private SimpTwoTuple<PartialValue, ShapeFunctionValue> twoTuple = new SimpTwoTuple<PartialValue, ShapeFunctionValue>();

    public TwoTuple<PartialValue, ShapeFunctionValue> valuePack(double[] coord) {
        if (null == partialValue || partialValue.getMaxPartialOrder() != diffOrder
                || partialValue.getSpatialDimension() != spatialDimension) {
            partialValue = new ArrayPartialValue(spatialDimension, diffOrder);
        } else {
            partialValue.fill(0);
        }
        ShapeFunctionValue mix = shapeValue(coord);

        for (int i = 0; i < mix.size(); i++) {
            int index = mix.getNodeAssemblyIndex(i);
            double alpha = parameters[index];
            for (int j = 0; j < partialValue.partialSize(); j++) {
                partialValue.add(j, alpha * mix.get(i, j));
            }
        }

        twoTuple.setFirst(partialValue);
        twoTuple.setSecond(mix);
        return twoTuple;
    }

    public ShapeFunctionValue shapeValue(double[] coord) {
        mixer.setDiffOrder(diffOrder);
        mixer.setCenter(coord);
        ShapeFunctionValue mix = mixer.mix();
        return mix;
    }

    public double value(double[] coord) {
        mixer.setDiffOrder(0);
        mixer.setCenter(coord);
        ShapeFunctionValue mix = mixer.mix();
        double result = 0;
        for (int i = 0; i < mix.size(); i++) {
            int index = mix.getNodeAssemblyIndex(i);
            result += parameters[index] * mix.get(i, 0);
        }
        return result;
    }

    public MFMixer getMixer() {
        return mixer;
    }

    public void setMixer(MFMixer levelMixer) {
        this.mixer = levelMixer;
    }

    public double[] getParameters() {
        return parameters;
    }

    public void setParameters(double[] parameters) {
        this.parameters = parameters;
    }

    public int getDiffOrder() {
        return diffOrder;
    }

    public void setDiffOrder(int diffOrder) {
        this.diffOrder = diffOrder;
    }

    public int getSpatialDimension() {
        return spatialDimension;
    }

    public void setSpatialDimension(int spatialDimension) {
        this.spatialDimension = spatialDimension;
    }

}
