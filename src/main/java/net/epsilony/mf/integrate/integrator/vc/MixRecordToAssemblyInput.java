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
package net.epsilony.mf.integrate.integrator.vc;

import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.assembler.RawAssemblyInput;
import net.epsilony.mf.process.assembler.T2Value;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MixRecordToAssemblyInput implements Function<IntegralMixRecordEntry, AssemblyInput> {

    private Function<IntegralMixRecordEntry, T2Value> mixRecordToT2Value;
    private final RawAssemblyInput assemblerInput = new RawAssemblyInput();
    private Function<? super GeomPoint, ? extends LoadValue> loadValueCalculator;

    public void setLoadValueCalculator(Function<? super GeomPoint, ? extends LoadValue> loadValueCalculator) {
        this.loadValueCalculator = loadValueCalculator;
    }

    public void setMixRecordToT2Value(Function<IntegralMixRecordEntry, T2Value> mixRecordToT2Value) {
        this.mixRecordToT2Value = mixRecordToT2Value;
    }

    @Override
    public AssemblyInput apply(IntegralMixRecordEntry entry) {

        assemblerInput.setT2Value(mixRecordToT2Value.apply(entry));
        assemblerInput.setWeight(entry.getWeight());
        assemblerInput.setLoadValue(loadValueCalculator.apply(entry.getGeomPoint()));

        return assemblerInput;
    }

}
