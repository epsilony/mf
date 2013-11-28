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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SpatialLoad;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.core.AbstractMFIntegratorCore;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpVolumeMFIntegratorCore;
import net.epsilony.mf.process.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.process.integrate.unit.RawMFIntegratePoint;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.tb.quadrature.QuadrangleQuadrature;
import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class QuadranglePolygonVolumeIntegratorCore extends AbstractMFIntegratorCore {
    MFIntegratorCore subIntegratorCore = new SimpVolumeMFIntegratorCore();
    QuadrangleQuadrature quadrangleQuadrature = new QuadrangleQuadrature();
    RawMFIntegratePoint pt = new RawMFIntegratePoint();

    /**
     * 
     */
    public QuadranglePolygonVolumeIntegratorCore() {
        super();
        processType = MFProcessType.VOLUME;
    }

    @Override
    public void integrate() {
        PolygonIntegrateUnit polygonUnit = (PolygonIntegrateUnit) integrateUnit;
        if (polygonUnit.getVertesSize() != 4) {
            throw new IllegalStateException();
        }
        quadrangleQuadrature.setQuadrangle(polygonUnit.getVertexCoord(0)[0], polygonUnit.getVertexCoord(0)[1],
                polygonUnit.getVertexCoord(1)[0], polygonUnit.getVertexCoord(1)[1], polygonUnit.getVertexCoord(2)[0],
                polygonUnit.getVertexCoord(2)[1], polygonUnit.getVertexCoord(3)[0], polygonUnit.getVertexCoord(3)[1]);
        Iterator<QuadraturePoint> iter = quadrangleQuadrature.iterator();
        LockableHolder<MFLoad> lockableHolder = loadMap.get(polygonUnit.getEmbededIn());

        while (iter.hasNext()) {
            QuadraturePoint qp = iter.next();

            pt.setCoord(qp.coord);
            pt.setWeight(qp.weight);
            if (null != lockableHolder) {
                ReentrantLock lock = lockableHolder.getLock();
                try {
                    lock.lock();
                    SpatialLoad vmLoad = (SpatialLoad) lockableHolder.getData();
                    vmLoad.setCoord(qp.coord);
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
        quadrangleQuadrature.setDegree(integralDegree);
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
