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
package net.epsilony.mf.util.math;

import java.util.Arrays;

import net.epsilony.tb.analysis.WithDiffOrderUtil;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ArrayPartialValue implements PartialValue {
    private final int spatialDimension;
    private final int maxPartialOrder;
    private final int partialSize;
    private final double[] data;

    public ArrayPartialValue(int spatialDimension, int maxPartialOrder, double[] data) {
        if (spatialDimension < 1 || maxPartialOrder < 0) {
            throw new IllegalArgumentException();
        }
        this.spatialDimension = spatialDimension;
        this.maxPartialOrder = maxPartialOrder;
        this.partialSize = WithDiffOrderUtil.outputLength(spatialDimension, maxPartialOrder);
        if (data.length < partialSize) {
            throw new IllegalArgumentException();
        }
        this.data = data;
    }

    public ArrayPartialValue(int spatialDimension, int maxPartialOrder) {
        if (spatialDimension < 1 || maxPartialOrder < 0) {
            throw new IllegalArgumentException();
        }
        this.spatialDimension = spatialDimension;
        this.maxPartialOrder = maxPartialOrder;
        this.partialSize = WithDiffOrderUtil.outputLength(spatialDimension, maxPartialOrder);
        this.data = new double[partialSize];
    }

    @Override
    public int getSpatialDimension() {
        return spatialDimension;
    }

    @Override
    public int getMaxPartialOrder() {
        return maxPartialOrder;
    }

    @Override
    public int partialSize() {
        return partialSize;
    }

    public double[] getData() {
        return data;
    }

    @Override
    public double get(int partialIndex) {
        return data[partialIndex];
    }

    public void set(int partialIndex, double value) {
        data[partialIndex] = value;
    }

    public void add(int partialIndex, double value) {
        data[partialIndex] += value;
    }

    public void fill(double value) {
        Arrays.fill(data, value);
    }

}
