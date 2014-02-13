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

import net.epsilony.mf.integrate.unit.GeomUnitQuadraturePoint;
import net.epsilony.mf.model.load.DirichletLoadValue;
import net.epsilony.mf.process.assembler.LagrangleAssemblyInput;
import net.epsilony.mf.process.assembler.RawLagrangleAssemblerInput;
import net.epsilony.mf.process.assembler.ShapeFunctionValue;
import net.epsilony.mf.process.assembler.T2Value;
import net.epsilony.mf.util.convertor.Convertor;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class GeomUnitQuadraturePointToLagrangleAssemblyInput<G extends GeomUnit> implements
        Convertor<GeomUnitQuadraturePoint<G>, LagrangleAssemblyInput> {

    Convertor<? super GeomUnitQuadraturePoint<G>, ? extends DirichletLoadValue> loadValueCalculator;
    Convertor<? super GeomUnitQuadraturePoint<G>, ? extends T2Value> t2ValueCalculator;
    Convertor<? super GeomUnitQuadraturePoint<G>, ? extends ShapeFunctionValue> lagrangleValueCalculator;

    @Override
    public LagrangleAssemblyInput convert(GeomUnitQuadraturePoint<G> input) {
        return new RawLagrangleAssemblerInput(input.getWeight(), t2ValueCalculator.convert(input),
                loadValueCalculator.convert(input), lagrangleValueCalculator.convert(input));
    }

}
