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
package net.epsilony.mf.integrate.integrator.config;

import static net.epsilony.mf.util.function.FunctionConnectors.oneStreamConsumer;
import static net.epsilony.mf.util.function.FunctionConnectors.oneStreamOneOne;

import java.util.function.Function;
import java.util.stream.Stream;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.process.assembler.AssemblyInput;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ThreeStageIntegralCollection {
    private final MFFunctionGroup<Object, Stream<GeomQuadraturePoint>> unitToGeomQuadraturePointsGroup;
    private final MFFunctionGroup<GeomQuadraturePoint, AssemblyInput>  geomQuadraturePointToAssemblyInputGroup;
    private final MFConsumerGroup<AssemblyInput>                       assemblyGroup;

    public ThreeStageIntegralCollection(
            MFFunctionGroup<Object, Stream<GeomQuadraturePoint>> unitToGeomQuadraturePointsGroup,
            MFFunctionGroup<GeomQuadraturePoint, AssemblyInput> geomQuadraturePointToAssemblyInputGroup,
            MFConsumerGroup<AssemblyInput> assemblyGroup) {
        this.unitToGeomQuadraturePointsGroup = unitToGeomQuadraturePointsGroup;
        this.geomQuadraturePointToAssemblyInputGroup = geomQuadraturePointToAssemblyInputGroup;
        this.assemblyGroup = assemblyGroup;
    }

    public MFFunctionGroup<Object, Stream<GeomQuadraturePoint>> getUnitToGeomQuadraturePointsGroup() {
        return unitToGeomQuadraturePointsGroup;
    }

    public MFFunctionGroup<GeomQuadraturePoint, AssemblyInput> getGeomQuadraturePointToAssemblyInputGroup() {
        return geomQuadraturePointToAssemblyInputGroup;
    }

    public MFConsumerGroup<AssemblyInput> getAssemblyGroup() {
        return assemblyGroup;
    }

    public MFConsumerGroup<Object> asOneStageGroup() {
        Function<Object, Stream<AssemblyInput>> volToAsm = oneStreamOneOne(unitToGeomQuadraturePointsGroup.getVolume(),
                geomQuadraturePointToAssemblyInputGroup.getVolume());
        Function<Object, Stream<AssemblyInput>> neuToAsm = oneStreamOneOne(
                unitToGeomQuadraturePointsGroup.getNeumann(), geomQuadraturePointToAssemblyInputGroup.getNeumann());
        Function<Object, Stream<AssemblyInput>> diriToAsm = oneStreamOneOne(
                unitToGeomQuadraturePointsGroup.getDirichlet(), geomQuadraturePointToAssemblyInputGroup.getDirichlet());

        return new MFConsumerGroup<>(oneStreamConsumer(volToAsm, assemblyGroup.getVolume()), oneStreamConsumer(
                neuToAsm, assemblyGroup.getNeumann()), oneStreamConsumer(diriToAsm, assemblyGroup.getDirichlet()));
    }
}
