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
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.core.AbstractMFIntegratorCore;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpDirichletIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpNeumannIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpVolumeMFIntegratorCore;
import net.epsilony.mf.process.integrate.tool.LinearQuadratureSupport;
import net.epsilony.mf.process.integrate.unit.GeomUnitSubdomain;
import net.epsilony.mf.process.integrate.unit.RawMFIntegratePoint;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LineIntegratorCore extends AbstractMFIntegratorCore {
    private final MFIntegratorCore subIntegratorCore;
    private final RawMFIntegratePoint integratePoint = new RawMFIntegratePoint();
    private final LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();

    public LineIntegratorCore(MFProcessType processType) {
        super();
        this.processType = processType;
        switch (processType) {
        case VOLUME:
            subIntegratorCore = new SimpVolumeMFIntegratorCore();
            break;
        case NEUMANN:
            subIntegratorCore = new SimpNeumannIntegratorCore();
            break;
        case DIRICHLET:
            subIntegratorCore = new SimpDirichletIntegratorCore();
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void integrate() {
        GeomUnitSubdomain geomUnitSubdomain = (GeomUnitSubdomain) integrateUnit;
        Line line = (Line) geomUnitSubdomain.getGeomUnit();
        linearQuadratureSupport.setStartEndCoords(line.getStartCoord(), line.getEndCoord());
        linearQuadratureSupport.reset();
        while (linearQuadratureSupport.hasNext()) {
            linearQuadratureSupport.next();
            integratePoint.setCoord(linearQuadratureSupport.getLinearCoord());
            double linearParameter = linearQuadratureSupport.getLinearParameter();
            integratePoint.setWeight(linearQuadratureSupport.getLinearWeight());

            LockableHolder<MFLoad> lockableHolder = loadMap.get(line);
            if (null == lockableHolder) {
                lockableHolder = loadMap.get(line.getParent());
            }
            if (null == lockableHolder) {
                integratePoint.setLoad(null);
            } else {
                ReentrantLock lock = lockableHolder.getLock();
                try {
                    lock.lock();
                    SegmentLoad load = (SegmentLoad) lockableHolder.getData();
                    load.setSegment(line);
                    load.setParameter(linearParameter);
                    integratePoint.setLoad(load.getValue());
                    integratePoint.setLoadValidity(load.getValidity());
                } finally {
                    lock.unlock();
                }
            }
            subIntegratorCore.setIntegrateUnit(integratePoint);
            subIntegratorCore.integrate();
        }
    }

    @Override
    public void setIntegralDegree(int integralDegree) {
        super.setIntegralDegree(integralDegree);
        linearQuadratureSupport.setQuadratureDegree(integralDegree);
    }

    @Override
    public void setAssembler(Assembler assembler) {
        super.setAssembler(assembler);
        subIntegratorCore.setAssembler(assembler);
    }

    @Override
    public void setMixer(MFMixer mixer) {
        super.setMixer(mixer);
        subIntegratorCore.setMixer(mixer);
    }
}
