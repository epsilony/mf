/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.core;

import java.util.Map;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.observer.MFIntegratorStatus;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpVolumeMFIntegratorCore extends AbstractMFIntegratorCore {

    @Override
    public void integrate() {
        MFIntegratePoint integratePoint = (MFIntegratePoint) integrateUnit;
        mixer.setDiffOrder(1);
        mixer.setCenter(integratePoint.getCoord());
        mixer.setBoundary(null);
        MixResult mixResult = mixer.mix();
        assembler.setWeight(integratePoint.getWeight());
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setLoad(integratePoint.getLoad(), null);
        assembler.assemble();

        Map<MFIntegratorObserverKey, Object> data = observable.getDefaultData();
        data.put(MFIntegratorObserverKey.COORD, integratePoint.getCoord());
        data.put(MFIntegratorObserverKey.MIX_RESULT, mixResult);
        data.put(MFIntegratorObserverKey.LOAD, integratePoint.getLoad());
        data.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.CORE_UNIT_INTEGRATED);
        data.put(MFIntegratorObserverKey.WEIGHT, integratePoint.getWeight());
        observable.apprise(data);
    }
}
