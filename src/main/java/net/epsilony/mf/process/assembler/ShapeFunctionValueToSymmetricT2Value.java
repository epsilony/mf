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
package net.epsilony.mf.process.assembler;

import java.util.function.Function;

import net.epsilony.mf.shape_func.ShapeFunctionValue;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 *
 */
public class ShapeFunctionValueToSymmetricT2Value implements Function<ShapeFunctionValue, T2Value> {

    @Override
    public T2Value apply(ShapeFunctionValue t) {
        return new SymmetricT2Value(t);
    }

}
