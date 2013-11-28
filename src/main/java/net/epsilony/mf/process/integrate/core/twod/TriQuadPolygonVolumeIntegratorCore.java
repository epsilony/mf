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

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.core.AbstractMFIntegratorCore;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.process.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TriQuadPolygonVolumeIntegratorCore extends AbstractMFIntegratorCore {
    QuadranglePolygonVolumeIntegratorCore quadranglePolygonVolumeIntegratorCore = new QuadranglePolygonVolumeIntegratorCore();
    TrianglePolygonVolumeIntegratorCore trianglePolygonVolumeIntegratorCore = new TrianglePolygonVolumeIntegratorCore();
    MFIntegratorCore currentIntegrator;

    public TriQuadPolygonVolumeIntegratorCore() {
        super();
        processType = MFProcessType.VOLUME;
    }

    @Override
    public void integrate() {
        currentIntegrator.integrate();
    }

    @Override
    public void setIntegralDegree(int integralDegree) {
        // TODO Auto-generated method stub
        super.setIntegralDegree(integralDegree);
        trianglePolygonVolumeIntegratorCore.setIntegralDegree(integralDegree);
        quadranglePolygonVolumeIntegratorCore.setIntegralDegree(integralDegree);
    }

    @Override
    public void setAssembler(Assembler assembler) {
        // TODO Auto-generated method stub
        super.setAssembler(assembler);
        trianglePolygonVolumeIntegratorCore.setAssembler(assembler);
        quadranglePolygonVolumeIntegratorCore.setAssembler(assembler);
    }

    @Override
    public void setMixer(MFMixer mixer) {
        super.setMixer(mixer);
        trianglePolygonVolumeIntegratorCore.setMixer(mixer);
        quadranglePolygonVolumeIntegratorCore.setMixer(mixer);
    }

    @Override
    public void setIntegrateUnit(MFIntegrateUnit integrateUnit) {
        super.setIntegrateUnit(integrateUnit);
        PolygonIntegrateUnit polygonIntegrateUnit = (PolygonIntegrateUnit) integrateUnit;
        switch (polygonIntegrateUnit.getVertesSize()) {
        case 3:
            trianglePolygonVolumeIntegratorCore.setIntegrateUnit(polygonIntegrateUnit);
            currentIntegrator = trianglePolygonVolumeIntegratorCore;
            break;
        case 4:
            currentIntegrator = quadranglePolygonVolumeIntegratorCore;
            quadranglePolygonVolumeIntegratorCore.setIntegrateUnit(polygonIntegrateUnit);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void setLoadMap(Map<GeomUnit, LockableHolder<MFLoad>> loadMap) {
        super.setLoadMap(loadMap);
        quadranglePolygonVolumeIntegratorCore.setLoadMap(loadMap);
        trianglePolygonVolumeIntegratorCore.setLoadMap(loadMap);
    }

}
