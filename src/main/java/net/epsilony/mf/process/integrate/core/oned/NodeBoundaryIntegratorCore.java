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

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.core.AbstractMFIntegratorCore;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpDirichletIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpNeumannIntegratorCore;
import net.epsilony.mf.process.integrate.unit.NodeIntegrateUnit;
import net.epsilony.mf.process.integrate.unit.RawMFBoundaryIntegratePoint;
import net.epsilony.mf.util.LockableHolder;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class NodeBoundaryIntegratorCore extends AbstractMFIntegratorCore {

    MFIntegratorCore subIntegratorCore;
    private final RawMFBoundaryIntegratePoint integralPoint = new RawMFBoundaryIntegratePoint();

    public NodeBoundaryIntegratorCore(MFProcessType processType) {

        if (processType != MFProcessType.DIRICHLET && processType != MFProcessType.NEUMANN) {
            throw new IllegalArgumentException();
        }
        this.processType = processType;
        subIntegratorCore = processType == MFProcessType.NEUMANN ? new SimpNeumannIntegratorCore()
                : new SimpDirichletIntegratorCore();
    }

    @Override
    public void integrate() {
        NodeIntegrateUnit nodeSubdomain = (NodeIntegrateUnit) integrateUnit;
        integralPoint.setBoundaryParameter(0);
        integralPoint.setCoord(nodeSubdomain.getNode().getCoord());
        integralPoint.setWeight(1);
        LockableHolder<MFLoad> lockableHolder = loadMap.get(nodeSubdomain.getNode());
        if (lockableHolder == null) {
            integralPoint.setLoad(null);
            integralPoint.setLoadValidity(null);
        } else {
            ReentrantLock lock = lockableHolder.getLock();
            try {
                lock.lock();
                NodeLoad load = (NodeLoad) lockableHolder.getData();
                load.setNode(nodeSubdomain.getNode());
                integralPoint.setBoundary(nodeSubdomain.getNode());
                integralPoint.setLoad(load.getValue());
                if (processType == MFProcessType.DIRICHLET) {
                    integralPoint.setLoadValidity(load.getValidity());
                } else {
                    integralPoint.setLoadValidity(null);
                }
                integralPoint.setLoadValidity(load.getValidity());
            } finally {
                lock.unlock();
            }
        }
        subIntegratorCore.setIntegrateUnit(integralPoint);
        subIntegratorCore.integrate();
    }

    @Override
    public void setAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroup) {
        super.setAssemblersGroup(assemblersGroup);
        subIntegratorCore.setAssemblersGroup(assemblersGroup);
    }

    @Override
    public void setMixer(MFMixer mixer) {
        super.setMixer(mixer);
        subIntegratorCore.setMixer(mixer);
    }

}
