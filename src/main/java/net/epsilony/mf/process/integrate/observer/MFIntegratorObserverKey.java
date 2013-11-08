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

package net.epsilony.mf.process.integrate.observer;

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.util.MFKey;
import net.epsilony.tb.solid.GeomUnit;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public enum MFIntegratorObserverKey implements MFKey {

PROCESS_TYPE(MFProcessType.class), ASSEMBLER(Assembler.class), INTEGRATOR(MFIntegrator.class), STATUS(
        MFIntegratorStatus.class), INTEGRATE_UNIT(MFIntegratePoint.class), INTEGRATE_UNITS_NUM(Integer.class), MIX_RESULT(
        MixResult.class), CORE(MFIntegratorCore.class), COORD(double[].class), BOUNDARY(GeomUnit.class), OUT_NORMAL(
        double[].class), LOAD(double[].class), LOAD_VALIDITY(boolean[].class), WEIGHT(Double.class), LAGRANGLE_SHAPE_FUNCTION(
        double[].class), LAGRANGLE_INDES(TIntArrayList.class);

private MFIntegratorObserverKey(Class<?> valueType) {
    this.valueType = valueType;
}

private final Class<?> valueType;

@Override
public String getName() {
    return name();
}

@Override
public Class<?> getValueType() {
    return valueType;
}

}
