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

import java.util.Map;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangleAssembler;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.observer.MFIntegratorStatus;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFIntegrator extends AbstractMFIntegrator {

    public static Logger logger = LoggerFactory.getLogger(SimpMFIntegrator.class);

    @Override
    public void integrate() {
        initIntegrateResult();
        addObserversToCores();
        Map<MFIntegratorObserverKey, Object> observeData = observable.getDefaultData();
        observeData.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.STARTED);
        observable.apprise(observeData);
        for (MFProcessType type : MFProcessType.values()) {
            integrateByType(type);
        }

        observeData = observable.getDefaultData();
        observeData.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.FINISHED);
        observable.apprise(observeData);
    }

    private void initIntegrateResult() {
        integrateResult = new RawMFIntegrateResult();
        Assembler dirichletAssembler = assemblersGroup.get(MFProcessType.DIRICHLET);
        LagrangleAssembler lagAssembler = (LagrangleAssembler) dirichletAssembler;
        integrateResult.setLagrangleDimension(lagAssembler.getLagrangeDimension());
        boolean lagrangle = dirichletAssembler != null && dirichletAssembler instanceof LagrangleAssembler;
        integrateResult.setLagrangle(lagrangle);

        mainMatrixFactory.setNumRows(mainMatrixSize);
        mainMatrixFactory.setNumCols(mainMatrixSize);
        integrateResult.setMainMatrix(mainMatrixFactory.produce());
        logger.info("main matrix :{}", integrateResult.getMainMatrix());

        mainVectorFactory.setNumCols(1);
        mainVectorFactory.setNumRows(mainMatrixSize);
        integrateResult.setMainVector(mainVectorFactory.produce());
        logger.info("main vector :{}", integrateResult.getMainVector());
    }

    private void addObserversToCores() {
        for (MFIntegratorCore core : integratorCoresGroup.values()) {
            core.addObservers(observable.getObservers());
        }
    }

    private void integrateByType(MFProcessType type) {
        MFIntegratorCore core = integratorCoresGroup.get(type);
        SynchronizedIterator<MFIntegratePoint> integrateUnits = integrateUnitsGroup.get(type);

        if (null == integrateUnits) {
            return;
        }
        Assembler assembler = assemblersGroup.get(type);
        assembler.setMainMatrix(integrateResult.getMainMatrix());
        assembler.setMainVector(integrateResult.getMainVector());
        core.setAssembler(assembler);
        core.setMixer(mixerFactory.produce());

        Map<MFIntegratorObserverKey, Object> observeData = observable.getDefaultData();
        observeData.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.PROCESS_TYPE_SWITCHTED);
        observeData.put(MFIntegratorObserverKey.PROCESS_TYPE, type);
        observeData.put(MFIntegratorObserverKey.INTEGRATE_UNITS_NUM, integrateUnits.getEstimatedSize());
        observeData.put(MFIntegratorObserverKey.ASSEMBLER, assembler);
        observable.apprise(observeData);

        MFIntegratePoint integrateUnit = integrateUnits.nextItem();
        while (integrateUnit != null) {
            core.setIntegrateUnit(integrateUnit);
            core.integrate();

            observeData = observable.getDefaultData();
            observeData.put(MFIntegratorObserverKey.PROCESS_TYPE, type);
            observeData.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.UNIT_INTEGRATED);
            observeData.put(MFIntegratorObserverKey.INTEGRATE_UNIT, integrateUnit);
            observable.apprise(observeData);

            integrateUnit = integrateUnits.nextItem();
        }
    }

    @Override
    public MFIntegrateResult getIntegrateResult() {
        return integrateResult;

    }
}
