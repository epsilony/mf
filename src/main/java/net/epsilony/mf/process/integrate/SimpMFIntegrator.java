/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Map;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangleAssembler;
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
        integrateResult.mainMatrix = mainMatrixFactory.produce();
        logger.info("main matrix :{}", integrateResult.mainMatrix);

        mainVectorFactory.setNumCols(1);
        mainVectorFactory.setNumRows(mainMatrixSize);
        integrateResult.mainVector = mainVectorFactory.produce();
        logger.info("main vector :{}", integrateResult.mainVector);
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
        assembler.setMainMatrix(integrateResult.mainMatrix);
        assembler.setMainVector(integrateResult.mainVector);
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
