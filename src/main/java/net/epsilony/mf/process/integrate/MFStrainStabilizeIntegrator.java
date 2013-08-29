/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.tb.synchron.SynchronizedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFStrainStabilizeIntegrator extends AbstractMFIntegrator {

    public static Logger logger = LoggerFactory.getLogger(MFStrainStabilizeIntegrator.class);
    SynchronizedIterator<Iterable<MFIntegratePoint>> volumeDomainSynchronizedIterator;

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void processVolume() {
        //mixer.setDiffOrder(assembler.getVolumeDiffOrder());
//        mixer.setDiffOrder(0);
//        for (Quadrature<Segment2DQuadraturePoint> quadrature = volumeDomainSynchronizedIterator.nextItem();
//                quadrature != null;
//                quadrature = volumeDomainSynchronizedIterator.nextItem()) {
//            MFIntegratePoint mfpt = volumeSynchronizedIterator.nextItem();
//            if (mfpt == null) {
//                break;
//            }
//            QuadraturePoint pt = mfpt.quadraturePoint;
//            MixResult mixResult = mixer.mix(pt.coord, null);
//            assembler.setWeight(pt.weight);
//            assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
//            assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
//            assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
//            assembler.setLoad(mfpt.load, null);
//            assembler.assembleVolume();
//            if (null != observer) {
//                observer.volumeProcessed(this);
//            }
//        }
    }
}
