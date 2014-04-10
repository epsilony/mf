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

import net.epsilony.mf.util.math.ArrayPartialTuple.SingleArray;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public interface PartialTuple extends PartialVectorItem {
    double get(int index, int partialIndex);

    default PartialTuple copy() {
        SingleArray result = new ArrayPartialTuple.SingleArray(size(), getSpatialDimension(), getMaxPartialOrder());
        for (int pd = 0; pd < partialSize(); pd++) {
            for (int idx = 0; idx < size(); idx++) {
                result.set(idx, pd, get(idx, pd));
            }
        }
        return result;
    }

    default PartialValue sub(int index) {
        return new TupleWrapperPartialValue(this, index);
    }
}
