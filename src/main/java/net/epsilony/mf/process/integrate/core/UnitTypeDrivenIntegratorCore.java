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

import java.util.HashMap;
import java.util.Map;

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.tb.solid.GeomUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class UnitTypeDrivenIntegratorCore extends AbstractMFIntegratorCore {

    public static Logger logger = LoggerFactory.getLogger(UnitTypeDrivenIntegratorCore.class);
    Map<Class<? extends MFIntegrateUnit>, MFIntegratorCore> typeCoreMap = new HashMap<>();
    boolean subCoresNeedPreparing = false;

    public UnitTypeDrivenIntegratorCore(MFProcessType processType) {
        this.processType = processType;
    }

    @Override
    public void integrate() {
        prepareSubCores();
        MFIntegratorCore mfIntegratorCore = typeCoreMap.get(integrateUnit.getClass());
        mfIntegratorCore.setIntegrateUnit(integrateUnit);
        mfIntegratorCore.integrate();
    }

    private void prepareSubCores() {
        if (!subCoresNeedPreparing) {
            return;
        }
        if (typeCoreMap.isEmpty()) {
            logger.warn("empty dispatch registry!!");
        }
        for (MFIntegratorCore integratorCore : typeCoreMap.values()) {
            if (integratorCore.getProcessType() != getProcessType()) {
                throw new IllegalStateException();
            }

            integratorCore.setAssemblersGroup(assemblersGroup);
            integratorCore.setMixer(mixer);
            integratorCore.setLoadMap(loadMap);
            integratorCore.setIntegralDegree(integralDegree);
        }
        subCoresNeedPreparing = false;
    }

    @Override
    public void setIntegralDegree(int integralDegree) {
        super.setIntegralDegree(integralDegree);
        subCoresNeedPreparing = true;
    }

    @Override
    public void setAssemblersGroup(Map<AssemblerType, Assembler> assemblersGroup) {
        super.setAssemblersGroup(assemblersGroup);
        subCoresNeedPreparing = true;
    }

    @Override
    public void setMixer(MFMixer mixer) {
        super.setMixer(mixer);
        subCoresNeedPreparing = true;
    }

    @Override
    public void setIntegrateUnit(MFIntegrateUnit integrateUnit) {
        super.setIntegrateUnit(integrateUnit);
        subCoresNeedPreparing = true;
    }

    @Override
    public void setLoadMap(Map<GeomUnit, LockableHolder<MFLoad>> loadMap) {
        super.setLoadMap(loadMap);
        subCoresNeedPreparing = true;
    }

    public MFIntegratorCore register(Class<? extends MFIntegrateUnit> key, MFIntegratorCore value) {
        MFIntegratorCore returnValue = typeCoreMap.put(key, value);
        subCoresNeedPreparing = true;
        return returnValue;
    }

    public void registerAll(Map<? extends Class<? extends MFIntegrateUnit>, ? extends MFIntegratorCore> m) {
        typeCoreMap.putAll(m);
        subCoresNeedPreparing = true;
    }

    public void clearRegistry() {
        typeCoreMap.clear();
        subCoresNeedPreparing = true;
    }
}
