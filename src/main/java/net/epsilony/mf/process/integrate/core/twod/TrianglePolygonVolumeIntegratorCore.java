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
package net.epsilony.mf.process.integrate.core.twod;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SpatialLoad;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.integrate.core.AbstractMFIntegratorCore;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpVolumeMFIntegratorCore;
import net.epsilony.mf.process.integrate.tool.SymmetricTriangleQuadratureSupport;
import net.epsilony.mf.process.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.process.integrate.unit.RawMFIntegratePoint;
import net.epsilony.mf.util.LockableHolder;

/**
 * @author epsilon At epsilony.net
 * 
 */
public class TrianglePolygonVolumeIntegratorCore extends AbstractMFIntegratorCore {
    MFIntegratorCore subIntegratorCore = new SimpVolumeMFIntegratorCore();
    SymmetricTriangleQuadratureSupport quadratureSupport = new SymmetricTriangleQuadratureSupport();
    RawMFIntegratePoint pt = new RawMFIntegratePoint();

    public TrianglePolygonVolumeIntegratorCore() {
        super();
        processType = MFProcessType.VOLUME;
    }

    @Override
    public void integrate() {
        PolygonIntegrateUnit polygonUnit = (PolygonIntegrateUnit) integrateUnit;
        if (polygonUnit.getVertesSize() != 3) {
            throw new IllegalStateException();
        }

        LockableHolder<MFLoad> lockableHolder = loadMap.get(polygonUnit.getEmbededIn());
        quadratureSupport.setTriangleVertes(polygonUnit.getVertesCoords());
        while (quadratureSupport.hasNext()) {
            quadratureSupport.next();

            double[] coordinate = quadratureSupport.getCoordinate();
            pt.setCoord(coordinate);
            pt.setWeight(quadratureSupport.getWeight());
            if (null != lockableHolder) {
                ReentrantLock lock = lockableHolder.getLock();
                try {
                    lock.lock();
                    SpatialLoad vmLoad = (SpatialLoad) lockableHolder.getData();
                    vmLoad.setCoord(coordinate);
                    pt.setLoad(vmLoad.getValue());
                } finally {
                    lock.unlock();
                }
            } else {
                pt.setLoad(null);
            }
            subIntegratorCore.setIntegrateUnit(pt);
            subIntegratorCore.integrate();
        }
    }

    @Override
    public void setIntegralDegree(int integralDegree) {
        super.setIntegralDegree(integralDegree);
        quadratureSupport.setQuadratureDegree(integralDegree);
    }

    @Override
    public void setAssemblersGroup(Map<AssemblerType, Assembler> assemblersGroup) {
        super.setAssemblersGroup(assemblersGroup);
        subIntegratorCore.setAssemblersGroup(assemblersGroup);
    }

    @Override
    public void setMixer(MFMixer mixer) {
        super.setMixer(mixer);
        subIntegratorCore.setMixer(mixer);
    }
}
