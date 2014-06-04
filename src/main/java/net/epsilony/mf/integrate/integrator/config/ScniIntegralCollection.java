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

import java.util.stream.Stream;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.process.assembler.AssemblyInput;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ScniIntegralCollection {
    private final MFFunctionGroup<Object, Stream<AssemblyInput>>       unitToAssemblyInputGroup;
    private final MFFunctionGroup<Object, Stream<GeomQuadraturePoint>> unitToMediaGeomQuadraturePointsGroup;
    private final MFConsumerGroup<AssemblyInput>                       assemblyGroup;

    public ScniIntegralCollection(MFFunctionGroup<Object, Stream<AssemblyInput>> unitToAssemblyInputGroup,
            MFFunctionGroup<Object, Stream<GeomQuadraturePoint>> unitToMediaGeomQuadraturePointsGroup,
            MFConsumerGroup<AssemblyInput> assemblyGroup) {
        this.unitToAssemblyInputGroup = unitToAssemblyInputGroup;
        this.unitToMediaGeomQuadraturePointsGroup = unitToMediaGeomQuadraturePointsGroup;
        this.assemblyGroup = assemblyGroup;
    }

    public MFFunctionGroup<Object, Stream<AssemblyInput>> getUnitToAssemblyInputGroup() {
        return unitToAssemblyInputGroup;
    }

    public MFFunctionGroup<Object, Stream<GeomQuadraturePoint>> getUnitToMediaGeomQuadraturePointsGroup() {
        return unitToMediaGeomQuadraturePointsGroup;
    }

    public MFConsumerGroup<AssemblyInput> getAssemblyGroup() {
        return assemblyGroup;
    }

    public MFConsumerGroup<Object> asOneGroup() {
        return new MFConsumerGroup<Object>(oneStreamConsumer(unitToAssemblyInputGroup.getVolume(),
                assemblyGroup.getVolume()), oneStreamConsumer(unitToAssemblyInputGroup.getNeumann(),
                assemblyGroup.getNeumann()), oneStreamConsumer(unitToAssemblyInputGroup.getDirichlet(),
                assemblyGroup.getDirichlet()));

    }
}
