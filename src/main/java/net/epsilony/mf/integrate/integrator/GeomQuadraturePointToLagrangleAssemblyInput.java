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
package net.epsilony.mf.integrate.integrator;

import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.load.DirichletLoadValue;
import net.epsilony.mf.process.assembler.LagrangleAssemblyInput;
import net.epsilony.mf.process.assembler.RawLagrangleAssemblyInput;
import net.epsilony.mf.process.assembler.SymmetricT2Value;
import net.epsilony.mf.process.assembler.T2Value;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.function.TypeMapFunction;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class GeomQuadraturePointToLagrangleAssemblyInput implements
        Function<GeomQuadraturePoint, LagrangleAssemblyInput> {

    Function<? super GeomPoint, ? extends DirichletLoadValue> loadValueCalculator;
    Function<? super GeomPoint, ? extends T2Value> t2ValueCalculator;
    Function<? super GeomPoint, ? extends T2Value> lagrangleValueCalculator = defaultLagrangleValueCalculator()
            .andThen(SymmetricT2Value::new);

    @Override
    public LagrangleAssemblyInput apply(GeomQuadraturePoint input) {
        T2Value t2Value = t2ValueCalculator.apply(input.getGeomPoint());
        T2Value lagT2Value = lagrangleValueCalculator.apply(input.getGeomPoint());
        return new RawLagrangleAssemblyInput(input.getWeight(), t2Value, loadValueCalculator.apply(input
                .getGeomPoint()), lagT2Value);
    }

    private Function<GeomPoint, ShapeFunctionValue> defaultLagrangleValueCalculator() {
        TypeMapFunction<GeomPoint, ShapeFunctionValue> result = new TypeMapFunction<>();
        result.register(MFNode.class, new NodeLagrangleShapeFunction());
        result.register(Line.class, new LineLagrangleShapeFunction());
        result.setTypeGetter((gp) -> gp.getGeomUnit().getClass());
        return result;
    }

    public void setLoadValueCalculator(Function<? super GeomPoint, ? extends DirichletLoadValue> loadValueCalculator) {
        this.loadValueCalculator = loadValueCalculator;
    }

    public void setT2ValueCalculator(Function<? super GeomPoint, ? extends T2Value> t2ValueCalculator) {
        this.t2ValueCalculator = t2ValueCalculator;
    }

    public void setLagrangleValueCalculator(Function<? super GeomPoint, ? extends T2Value> lagrangleValueCalculator) {
        this.lagrangleValueCalculator = lagrangleValueCalculator;
    }

}
