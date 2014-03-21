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
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.assembler.RawAssemblerInput;
import net.epsilony.mf.process.assembler.ShapeFunctionValue;
import java.util.function.Function;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class GeomUnitQuadraturePointToAssemblyInput<L extends LoadValue> implements
        Function<GeomQuadraturePoint, AssemblyInput<L>> {
    Function<? super GeomPoint, ? extends L> loadValueCalculator;
    Function<? super GeomPoint, ? extends List<? extends ShapeFunctionValue>> t2ValueCalculator;

    @Override
    public AssemblyInput<L> apply(GeomQuadraturePoint input) {
        List<? extends ShapeFunctionValue> ttValue = t2ValueCalculator.apply(input.getGeomPoint());
        return new RawAssemblerInput<L>(input.getWeight(), ttValue.get(0), ttValue.get(1),
                loadValueCalculator.apply(input.getGeomPoint()));
    }

    public void setLoadValueCalculator(Function<? super GeomPoint, ? extends L> loadValueCalculator) {
        this.loadValueCalculator = loadValueCalculator;
    }

    public void setT2ValueCalculator(
            Function<? super GeomPoint, ? extends List<? extends ShapeFunctionValue>> ttValueCalculator) {
        this.t2ValueCalculator = ttValueCalculator;
    }

    public GeomUnitQuadraturePointToAssemblyInput(Function<? super GeomPoint, ? extends L> loadValueCalculator,
            Function<? super GeomPoint, ? extends List<? extends ShapeFunctionValue>> t2ValueCalculator) {
        this.loadValueCalculator = loadValueCalculator;
        this.t2ValueCalculator = t2ValueCalculator;
    }

    public GeomUnitQuadraturePointToAssemblyInput() {
    }
}
