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

import java.util.EnumMap;
import java.util.Map;
import net.epsilony.mf.process.MFProcessType;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Assemblers {

    public static Map<MFProcessType, Assembler> mechanicalLagrangle() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, new MechanicalVolumeAssembler());
        result.put(MFProcessType.NEUMANN, new NeumannAssembler());
        result.put(MFProcessType.DIRICHLET, new LagrangleDirichletAssembler());
        return result;
    }

    public static Map<MFProcessType, Assembler> poissonLagrangle() {
        EnumMap<MFProcessType, Assembler> result = new EnumMap<>(MFProcessType.class);
        result.put(MFProcessType.VOLUME, new PoissonVolumeAssembler());
        result.put(MFProcessType.NEUMANN, new NeumannAssembler());
        result.put(MFProcessType.DIRICHLET, new LagrangleDirichletAssembler());
        return result;
    }
}
