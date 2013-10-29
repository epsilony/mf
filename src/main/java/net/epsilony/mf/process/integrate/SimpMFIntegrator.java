/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangleAssembler;
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
        MFIntegratePoint integrateUnit = integrateUnits.nextItem();
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
