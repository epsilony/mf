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

package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangleAssembler;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFIntegrator extends AbstractMFIntegrator {

    @Override
    public void integrate() {
        initIntegrateResult();
        for (MFProcessType type : MFProcessType.values()) {
            integrateByType(type);
        }
    }

    private void initIntegrateResult() {
        integrateResult = new RawMFIntegrateResult();
        Assembler dirichletAssembler = assemblersGroup.get(MFProcessType.DIRICHLET);
        LagrangleAssembler lagAssembler = (LagrangleAssembler) dirichletAssembler;
        integrateResult.setLagrangleDimension(lagAssembler.getLagrangeDimension());
        boolean lagrangle = dirichletAssembler != null && dirichletAssembler instanceof LagrangleAssembler;
        integrateResult.setLagrangle(lagrangle);
        integrateResult.setMainMatrix(mainMatrix);
        integrateResult.setMainVector(mainVector);
    }

    private void integrateByType(MFProcessType type) {
        MFIntegratorCore core = integratorCoresGroup.get(type);
        SynchronizedIterator<MFIntegrateUnit> integrateUnits = integrateUnitsGroup.get(type);

        if (null == integrateUnits) {
            return;
        }
        Assembler assembler = assemblersGroup.get(type);
        assembler.setMainMatrix(mainMatrix);
        assembler.setMainVector(mainVector);
        core.setAssembler(assembler);
        core.setMixer(mixer);

        MFIntegrateUnit integrateUnit = integrateUnits.nextItem();
        while (integrateUnit != null) {
            core.setIntegrateUnit(integrateUnit);
            core.integrate();
            integrateUnit = integrateUnits.nextItem();
        }
    }

    @Override
    public MFIntegrateResult getIntegrateResult() {
        return integrateResult;

    }
}
