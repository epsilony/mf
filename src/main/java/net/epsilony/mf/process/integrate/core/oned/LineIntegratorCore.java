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
package net.epsilony.mf.process.integrate.core.oned;

import java.util.concurrent.locks.ReentrantLock;

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.unit.GeomUnitSubdomain;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LineIntegratorCore extends AbstractLineIntegratorCore {
    public LineIntegratorCore(MFProcessType processType) {
        super(processType);
    }

    @Override
    public void integrate() {
        GeomUnitSubdomain geomUnitSubdomain = (GeomUnitSubdomain) integrateUnit;
        Line line = (Line) geomUnitSubdomain.getGeomUnit();
        linearQuadratureSupport.setStartEndCoords(line.getStartCoord(), line.getEndCoord());
        linearQuadratureSupport.reset();
        while (linearQuadratureSupport.hasNext()) {
            linearQuadratureSupport.next();
            fillWeightAndCoord();
            fillLoadAndIntegrate(line, linearQuadratureSupport.getLinearParameter());
        }
    }
}
