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

import net.epsilony.mf.integrate.integrator.LineLagrangleShapeFunction;
import net.epsilony.mf.integrate.integrator.NodeLagrangleShapeFunction;
import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.assembler.LagrangleAssemblyInput;
import net.epsilony.mf.process.assembler.RawLagrangleAssemblyInput;
import net.epsilony.mf.process.assembler.SymmetricT2Value;
import net.epsilony.mf.process.assembler.T2Value;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.function.TypeMapFunction;
import net.epsilony.mf.model.geom.MFLine;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MixRecordToLagrangleAssemblyInput implements Function<IntegralMixRecordEntry, LagrangleAssemblyInput> {

    private Function<IntegralMixRecordEntry, T2Value> mixRecordToT2Value;
    private final RawLagrangleAssemblyInput lagrangleAsmInput = new RawLagrangleAssemblyInput();
    private Function<? super GeomPoint, ? extends LoadValue> loadValueCalculator;
    Function<? super GeomPoint, ? extends T2Value> lagrangleValueCalculator = defaultLagrangleValueCalculator()
            .andThen(SymmetricT2Value::new);

    @Override
    public LagrangleAssemblyInput apply(IntegralMixRecordEntry input) {
        T2Value t2Value = mixRecordToT2Value.apply(input);
        T2Value lagT2Value = lagrangleValueCalculator.apply(input.getGeomPoint());
        lagrangleAsmInput.setLagrangleT2Value(lagT2Value);
        lagrangleAsmInput.setT2Value(t2Value);
        lagrangleAsmInput.setLoadValue(loadValueCalculator.apply(input.getGeomPoint()));
        lagrangleAsmInput.setWeight(input.getWeight());
        return lagrangleAsmInput;
    }

    private Function<GeomPoint, ShapeFunctionValue> defaultLagrangleValueCalculator() {
        TypeMapFunction<GeomPoint, ShapeFunctionValue> result = new TypeMapFunction<>();
        result.register(MFNode.class, new NodeLagrangleShapeFunction());
        result.register(MFLine.class, new LineLagrangleShapeFunction());
        result.setTypeGetter((gp) -> gp.getGeomUnit().getClass());
        return result;
    }

    public void setMixRecordToT2Value(Function<IntegralMixRecordEntry, T2Value> mixRecordToT2Value) {
        this.mixRecordToT2Value = mixRecordToT2Value;
    }

    public void setLoadValueCalculator(Function<? super GeomPoint, ? extends LoadValue> loadValueCalculator) {
        this.loadValueCalculator = loadValueCalculator;
    }

    public void setLagrangleValueCalculator(Function<? super GeomPoint, ? extends T2Value> lagrangleValueCalculator) {
        this.lagrangleValueCalculator = lagrangleValueCalculator;
    }

}
