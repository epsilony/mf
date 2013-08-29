/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.MixResult;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFSimpIntegrator extends AbstractMFIntegrator {

    SynchronizedIterator<MFIntegratePoint<QuadraturePoint>> volumeSynchronizedIterator;
    public static Logger logger = LoggerFactory.getLogger(MFSimpIntegrator.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void processVolume() {
        if (null == volumeSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assembler.getVolumeDiffOrder());
        while (true) {
            MFIntegratePoint mfpt = volumeSynchronizedIterator.nextItem();
            if (mfpt == null) {
                break;
            }
            QuadraturePoint pt = mfpt.quadraturePoint;
            MixResult mixResult = mixer.mix(pt.coord, null);
            assembler.setWeight(pt.weight);
            assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
            assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setLoad(mfpt.load, null);
            assembler.assembleVolume();
            if (null != observer) {
                observer.volumeProcessed(this);
            }
        }
    }

    public void setVolumeSynchronizedIterator(
            SynchronizedIterator<MFIntegratePoint<QuadraturePoint>> volumeSynchronizedIterator) {
        this.volumeSynchronizedIterator = volumeSynchronizedIterator;
    }
}
