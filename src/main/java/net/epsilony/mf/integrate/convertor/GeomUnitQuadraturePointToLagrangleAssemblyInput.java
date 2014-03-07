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

import java.util.List;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.model.load.DirichletLoadValue;
import net.epsilony.mf.process.assembler.LagrangleAssemblyInput;
import net.epsilony.mf.process.assembler.RawLagrangleAssemblerInput;
import net.epsilony.mf.process.assembler.ShapeFunctionValue;
import net.epsilony.mf.util.convertor.Convertor;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class GeomUnitQuadraturePointToLagrangleAssemblyInput<G extends GeomUnit> implements
        Convertor<GeomQuadraturePoint<? extends G>, LagrangleAssemblyInput> {

    Convertor<? super GeomPoint<? extends G>, ? extends DirichletLoadValue> loadValueCalculator;
    Convertor<? super GeomPoint<? extends G>, ? extends List<? extends ShapeFunctionValue>> t2ValueCalculator;
    Convertor<? super GeomPoint<? extends G>, ? extends List<? extends ShapeFunctionValue>> lagrangleValueCalculator;

    @Override
    public LagrangleAssemblyInput convert(GeomQuadraturePoint<? extends G> input) {
        List<? extends ShapeFunctionValue> t2Value = t2ValueCalculator.convert(input.getGeomPoint());
        List<? extends ShapeFunctionValue> lagT2Value = lagrangleValueCalculator.convert(input.getGeomPoint());
        return new RawLagrangleAssemblerInput(input.getWeight(), t2Value.get(0), t2Value.get(1),
                loadValueCalculator.convert(input.getGeomPoint()), lagT2Value.get(0), lagT2Value.get(1));
    }
}
