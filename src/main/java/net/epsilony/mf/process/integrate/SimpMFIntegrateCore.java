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
        mixer.setDiffOrder(assembler.getVolumeDiffOrder());
        MixResult mixResult = mixer.mix(mfpt.getCoord(), null);
        assembler.setWeight(mfpt.getWeight());
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setLoad(mfpt.getLoad(), null);
        assembler.assembleVolume();
    }
}
