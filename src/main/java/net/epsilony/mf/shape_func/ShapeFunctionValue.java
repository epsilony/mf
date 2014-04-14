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

import net.epsilony.mf.util.math.PartialTuple;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public interface ShapeFunctionValue extends PartialTuple {
    int getNodeAssemblyIndex(int index);

    @Override
    default ShapeFunctionValue copy() {
        SimpShapeFunctionValue result = new SimpShapeFunctionValue();
        PartialTuple pt = this;
        PartialTuple copy = pt.copy();
        int[] indes = new int[size()];
        for (int i = 0; i < indes.length; i++) {
            indes[i] = getNodeAssemblyIndex(i);
        }
        result.setPartialValueTuple(copy);
        result.setAssemblyIndexGetter(asmId -> indes[asmId]);
        return result;
    }
}
