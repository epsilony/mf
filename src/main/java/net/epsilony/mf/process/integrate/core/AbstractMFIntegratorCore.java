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

package net.epsilony.mf.process.integrate.core;

import java.util.Map;

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.tb.solid.GeomUnit;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMFIntegratorCore implements MFIntegratorCore {

    protected int integralDegree;
    protected Map<MFProcessType, Assembler> assemblersGroup;
    protected MFMixer mixer;
    protected MFIntegrateUnit integrateUnit;
    protected MFProcessType processType;
    protected Map<GeomUnit, LockableHolder<MFLoad>> loadMap;

    @Override
    public void setIntegralDegree(int integralDegree) {
        this.integralDegree = integralDegree;
    }

    @Override
    public void setAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroup) {
        this.assemblersGroup = assemblersGroup;
    }

    @Override
    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
    }

    @Override
    public void setIntegrateUnit(MFIntegrateUnit integrateUnit) {
        this.integrateUnit = integrateUnit;
    }

    @Override
    public void setLoadMap(Map<GeomUnit, LockableHolder<MFLoad>> loadMap) {
        this.loadMap = loadMap;
    }

    @Override
    public MFProcessType getProcessType() {
        return processType;
    }
}
