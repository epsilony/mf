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
package net.epsilony.mf.integrate.convertor;

import net.epsilony.mf.process.assembler.ShapeFunctionValue;
import net.epsilony.mf.process.assembler.SymmetricT2Value;
import net.epsilony.mf.process.assembler.T2Value;
import net.epsilony.mf.util.convertor.Convertor;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ShapeFunctionValueToT2Value implements Convertor<ShapeFunctionValue, T2Value> {

    @Override
    public T2Value convert(ShapeFunctionValue input) {
        return new SymmetricT2Value(input);
    }

}
