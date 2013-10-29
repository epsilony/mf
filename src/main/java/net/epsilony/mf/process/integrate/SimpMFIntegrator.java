/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Map;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangleAssembler;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.observer.MFIntegratorStatus;
import net.epsilony.mf.process.integrate.observer.SimpIntegratorObservable;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFIntegrator extends AbstractMFIntegrator {

    public static Logger logger = LoggerFactory.getLogger(SimpMFIntegrator.class);
    private final SimpIntegratorObservable observable = new SimpIntegratorObservable(this);

    @Override
    public void integrate() {
        initIntegrateResult();
        for (MFProcessType type : MFProcessType.values()) {
            integrateByType(type);
        }
        Map<MFIntegratorObserverKey, Object> observeData = observable.getObserveData();
        observeData.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.FINISHED);
        observable.apprise();
    }

    private void initIntegrateResult() {
        integrateResult = new RawMFIntegrateResult();
        Assembler dirichletAssembler = assemblersGroup.get(MFProcessType.DIRICHLET);
        LagrangleAssembler lagAssembler = (LagrangleAssembler) dirichletAssembler;
        integrateResult.setLagrangleDimension(lagAssembler.getLagrangeDimension());
        boolean lagrangle = dirichletAssembler != null && dirichletAssembler instanceof LagrangleAssembler;
        integrateResult.setLagrangle(lagrangle);
        integrateResult.mainMatrix = mainMatrixFactory.produce();
        logger.info("main matrix :{}", integrateResult.mainMatrix);
        integrateResult.mainVector = mainVectorFactory.produce();
        logger.info("main vector :{}", integrateResult.mainVector);
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

        Map<MFIntegratorObserverKey, Object> observeData = observable.getObserveData();
        observeData.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.PROCESS_TYPE_SWITCHTED);
        observeData.put(MFIntegratorObserverKey.PROCESS_TYPE, type);
        observeData.put(MFIntegratorObserverKey.INTEGRATE_UNITS_NUM, integrateUnits.getEstimatedSize());
        observeData.put(MFIntegratorObserverKey.ASSEMBLER, assembler);
        observable.apprise();

        MFIntegratePoint integrateUnit = integrateUnits.nextItem();
        while (integrateUnit != null) {
            core.setIntegrateUnit(integrateUnit);
            core.integrate();

            observeData = observable.getObserveData();
            observeData.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.AN_UNIT_IS_INTEGRATED);
            observeData.put(MFIntegratorObserverKey.INTEGRATE_UNIT, integrateUnit);
            observable.apprise();

            integrateUnit = integrateUnits.nextItem();
        }
    }

    @Override
    public MFIntegrateResult getIntegrateResult() {
        return integrateResult;

    }

    public boolean addObserver(MFIntegratorObserver observer) {
        return observable.add(observer);
    }

    public boolean removeObserver(MFIntegratorObserver observer) {
        return observable.remove(observer);
    }

    public void clearObserver() {
        observable.clear();
    }
}
