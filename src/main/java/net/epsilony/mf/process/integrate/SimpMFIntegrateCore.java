/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFIntegrateCore extends AbstractMFIntegrateCore<MFIntegratePoint> {

    @Override
    public void integrateVolume(MFIntegratePoint mfpt) {
        mixer.setDiffOrder(1);
        mixer.setCenter(mfpt.getCoord());
        mixer.setBoundary(null);
        MixResult mixResult = mixer.mix();
        assembler.setWeight(mfpt.getWeight());
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setLoad(mfpt.getLoad(), null);
        assembler.assembleVolume();
    }
}
