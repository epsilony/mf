/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.MixResult;

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
        assembler.assembleVolume();
    }
}
