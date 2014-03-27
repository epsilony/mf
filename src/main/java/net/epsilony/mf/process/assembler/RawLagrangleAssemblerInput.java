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

import net.epsilony.mf.model.load.DirichletLoadValue;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class RawLagrangleAssemblerInput extends RawAssemblerInput implements LagrangleAssemblyInput {
    private T2Value lagrangleT2Value;

    @Override
    public T2Value getLagrangleT2Value() {
        return lagrangleT2Value;
    }

    public void setLagrangleT2Value(T2Value lagrangleT2Value) {
        this.lagrangleT2Value = lagrangleT2Value;
    }

    public RawLagrangleAssemblerInput(double weight, T2Value t2Value, DirichletLoadValue loadValue,
            T2Value lagrangleT2Value) {
        super(weight, t2Value, loadValue);
        this.lagrangleT2Value = lagrangleT2Value;
    }

    public RawLagrangleAssemblerInput() {
    }

}
