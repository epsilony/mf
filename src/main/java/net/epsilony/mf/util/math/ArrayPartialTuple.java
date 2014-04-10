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
public abstract class ArrayPartialTuple implements PartialTuple {
    protected final int size;
    protected final int spatialDimension;
    protected final int maxPartialOrder;
    protected final int partialSize;
    protected final TupleWrapperPartialValue partialValue = new TupleWrapperPartialValue(this, -1);

    protected ArrayPartialTuple(int size, int spatialDimension, int maxPartialOrder) {
        this.size = size;
        this.spatialDimension = spatialDimension;
        this.maxPartialOrder = maxPartialOrder;
        partialSize = WithDiffOrderUtil.outputLength(spatialDimension, maxPartialOrder);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int partialSize() {
        return partialSize;
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
    public PartialValue sub(int index) {
        partialValue.setIndex(index);
        return partialValue;
    }

    public abstract void set(int index, int partialIndex, double value);

    public abstract void add(int index, int partialIndex, double value);

    public abstract void fill(double value);

    public static class SingleArray extends ArrayPartialTuple {
        private final double[] data;

        public SingleArray(int size, int spatialDimension, int maxPartialOrder) {
            super(size, spatialDimension, maxPartialOrder);
            data = new double[size * partialSize()];
        }

        public SingleArray(int size, int spatialDimension, int maxPartialOrder, double[] data) {
            super(size, spatialDimension, maxPartialOrder);
            if (data.length < size * partialSize) {
                throw new IllegalArgumentException();
            }
            this.data = data;
        }

        @Override
        public double get(int index, int partialIndex) {
            return data[index * partialSize + partialIndex];
        }

        @Override
        public void set(int index, int partialIndex, double value) {
            data[index * partialSize + partialIndex] = value;
        }

        @Override
        public void fill(double value) {
            Arrays.fill(data, value);
        }

        @Override
        public void add(int index, int partialIndex, double value) {
            data[index * partialSize + partialIndex] += value;
        }

        @Override
        public PartialTuple copy() {
            return new ArrayPartialTuple.SingleArray(size, spatialDimension, maxPartialOrder, Arrays.copyOf(data,
                    partialSize));
        }

        public double[] getData() {
            return data;
        }

    }

    public static class RowForPartial extends ArrayPartialTuple {

        private final double[][] data;

        public RowForPartial(int size, int spatialDimension, int maxPartialOrder) {
            super(size, spatialDimension, maxPartialOrder);
            data = new double[partialSize][size];
        }

        public RowForPartial(int size, int spatialDimension, int maxPartialOrder, double[][] data) {
            super(size, spatialDimension, maxPartialOrder);
            if (data.length != partialSize) {
                throw new IllegalArgumentException();
            }
            for (double[] d : data) {
                if (d.length < size) {
                    throw new IllegalArgumentException();
                }
            }
            this.data = data;
        }

        @Override
        public double get(int index, int partialIndex) {
            return data[partialIndex][index];
        }

        @Override
        public void add(int index, int partialIndex, double value) {
            data[partialIndex][index] += value;
        }

        @Override
        public void set(int index, int partialIndex, double value) {
            data[partialIndex][index] = value;
        }

        @Override
        public void fill(double value) {
            for (double[] d : data) {
                Arrays.fill(d, value);
            }
        }

        public double[][] getData() {
            return data;
        }

        @Override
        public PartialTuple copy() {
            double[][] dataCopy = new double[data.length][];
            for (int i = 0; i < dataCopy.length; i++) {
                dataCopy[i] = Arrays.copyOf(data[i], size);
            }
            return new RowForPartial(size, spatialDimension, maxPartialOrder, dataCopy);
        }
    }

}
