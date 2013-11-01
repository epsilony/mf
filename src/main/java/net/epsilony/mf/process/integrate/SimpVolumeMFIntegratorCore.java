/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Map;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.observer.MFIntegratorStatus;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpVolumeMFIntegratorCore extends AbstractMFIntegratorCore {

    @Override
    public void integrate() {
        mixer.setDiffOrder(1);
        mixer.setCenter(integrateUnit.getCoord());
        mixer.setBoundary(null);
        MixResult mixResult = mixer.mix();
        assembler.setWeight(integrateUnit.getWeight());
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setLoad(integrateUnit.getLoad(), null);
        assembler.assemble();

        Map<MFIntegratorObserverKey, Object> data = observable.getDefaultData();
        data.put(MFIntegratorObserverKey.COORD, integrateUnit.getCoord());
        data.put(MFIntegratorObserverKey.MIX_RESULT, mixResult);
        data.put(MFIntegratorObserverKey.LOAD, integrateUnit.getLoad());
        data.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.CORE_UNIT_INTEGRATED);
        data.put(MFIntegratorObserverKey.WEIGHT, integrateUnit.getWeight());
        observable.apprise(data);
    }
}
