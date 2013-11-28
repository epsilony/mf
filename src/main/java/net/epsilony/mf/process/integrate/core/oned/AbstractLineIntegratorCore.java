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
import net.epsilony.mf.process.integrate.unit.RawMFBoundaryIntegratePoint;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public abstract class AbstractLineIntegratorCore extends AbstractMFIntegratorCore {

    protected final MFIntegratorCore subIntegratorCore;
    protected final RawMFBoundaryIntegratePoint integratePoint = new RawMFBoundaryIntegratePoint();
    protected final LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();

    /**
     * 
     */
    public AbstractLineIntegratorCore(MFProcessType processType) {
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
    
    protected void fillLoadAndIntegrate(Line line, double parameter) {
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
		        load.setParameter(parameter);
		        integratePoint.setLoad(load.getValue());
		        integratePoint.setLoadValidity(load.getValidity());
		    } finally {
		        lock.unlock();
		    }
		}
		if (processType == MFProcessType.NEUMANN || processType == MFProcessType.DIRICHLET) {
		    integratePoint.setBoundary(line);
		    integratePoint.setBoundaryParameter(linearQuadratureSupport.getLinearParameter());
		}
		subIntegratorCore.setIntegrateUnit(integratePoint);
		subIntegratorCore.integrate();
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

	protected void fillWeightAndCoord() {
		integratePoint.setCoord(linearQuadratureSupport.getLinearCoord());
		integratePoint.setWeight(linearQuadratureSupport.getLinearWeight());
	}

}