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

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TupleWrapperPartialValue implements PartialValue {
    private PartialTuple partialValueTuple;
    private int          index;

    @Override
    public int getSpatialDimension() {
        return partialValueTuple.getSpatialDimension();
    }

    @Override
    public int getMaxPartialOrder() {
        return partialValueTuple.getMaxPartialOrder();
    }

    @Override
    public int partialSize() {
        return partialValueTuple.partialSize();
    }

    @Override
    public double get(int partialIndex) {
        return partialValueTuple.get(index, partialIndex);
    }

    public TupleWrapperPartialValue(PartialTuple partialValueTuple, int index) {
        this.partialValueTuple = partialValueTuple;
        this.index = index;
    }

    public TupleWrapperPartialValue() {
    }

    public PartialVectorItem getPartialValueTuple() {
        return partialValueTuple;
    }

    public void setPartialValueTuple(PartialTuple partialValueTuple) {
        this.partialValueTuple = partialValueTuple;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
