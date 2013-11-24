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

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class UnitTypeDrivenIntegratorCore extends AbstractMFIntegratorCore {

    Map<Class<? extends MFIntegrateUnit>, MFIntegratorCore> typeCoreMap = new HashMap<>();
    boolean subCoresNeedPreparing = false;

    public void setProcessType(MFProcessType processType) {
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
        for (MFIntegratorCore integratorCore : typeCoreMap.values()) {
            if (integratorCore.getProcessType() != getProcessType()) {
                throw new IllegalStateException();
            }
            integratorCore.setAssembler(assembler);
            integratorCore.setMixer(mixer);
            integratorCore.setLoadMap(loadMap);
        }
        subCoresNeedPreparing = false;
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
    }
}
