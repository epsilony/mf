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
package net.epsilony.mf.opt.integrate;

import java.util.Arrays;
import java.util.stream.Stream;

import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.geom.MFLine;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NoRepetitatePreparationLvUnitsGroup implements LevelFunctionalIntegralUnitsGroup {
    private TriangleMarchingIntegralUnitsFactory factory;

    private double[]                             lastParameters = null;
    private boolean                              needPrepare    = true;

    public void resetLast() {
        lastParameters = null;
    }

    public void setParameters(double[] parameters) {
        if (lastParameters != null && Arrays.equals(lastParameters, parameters)) {
            needPrepare = false;
            return;
        } else {
            needPrepare = true;
        }

        if (null == lastParameters || lastParameters.length != parameters.length) {
            lastParameters = parameters.clone();
        } else {
            System.arraycopy(parameters, 0, lastParameters, 0, parameters.length);
        }

    }

    @Override
    public void prepare() {
        if (needPrepare) {
            factory.generateUnits();
            needPrepare = false;
        }
    }

    @Override
    public Stream<MFLine> boundary() {
        return factory.boundaryUnits().stream();
    }

    @Override
    public Stream<PolygonIntegrateUnit> volume() {
        return factory.volumeUnits().stream();
    }

    public void setFactory(TriangleMarchingIntegralUnitsFactory factory) {
        this.factory = factory;
    }
}
