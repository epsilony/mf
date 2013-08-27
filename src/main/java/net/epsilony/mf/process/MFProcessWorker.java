/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.project.quadrature_task.MFQuadraturePoint;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.synchron.SynchronizedIteratorWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFProcessWorker implements Runnable {

    public static Logger logger = LoggerFactory.getLogger(MFProcessWorker.class);
    Assembler assembler;
    Mixer mixer;
    LinearLagrangeDirichletProcessor lagProcessor;
    SynchronizedIteratorWrapper<MFQuadraturePoint<QuadraturePoint>> volumeSynchronizedIterator;
    SynchronizedIteratorWrapper<MFQuadraturePoint<Segment2DQuadraturePoint>> neumannSynchronizedIterator;
    SynchronizedIteratorWrapper<MFQuadraturePoint<Segment2DQuadraturePoint>> dirichletSynchronizedIterator;
    MFProcessWorkerObserver observer;

    public void setObserver(MFProcessWorkerObserver observer) {
        this.observer = observer;
    }

    public boolean isAssemblyDirichletByLagrange() {
        return lagProcessor != null && assembler instanceof LagrangeAssembler;
    }

    public void processVolume() {
        if (null == volumeSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assembler.getVolumeDiffOrder());
        while (true) {
            MFQuadraturePoint mfpt = volumeSynchronizedIterator.nextItem();
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

    public void processNeumann() {
        if (null == neumannSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assembler.getNeumannDiffOrder());
        while (true) {
            MFQuadraturePoint<Segment2DQuadraturePoint> mfpt = neumannSynchronizedIterator.nextItem();
            if (mfpt == null) {
                break;
            }
            Segment2DQuadraturePoint pt = mfpt.quadraturePoint;
            MixResult mixResult = mixer.mix(pt.coord, pt.segment);
            assembler.setWeight(pt.weight);
            assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
            assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setLoad(mfpt.load, null);
            assembler.assembleNeumann();
            if (null != observer) {
                observer.neumannProcessed(this);
            }
        }
    }

    public void processDirichlet() {
        if (null == dirichletSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assembler.getDirichletDiffOrder());
        boolean lagDiri = isAssemblyDirichletByLagrange();
        LagrangeAssembler lagAssembler = null;
        if (lagDiri) {
            lagAssembler = (LagrangeAssembler) assembler;
        }
        while (true) {
            MFQuadraturePoint<Segment2DQuadraturePoint> mfpt = dirichletSynchronizedIterator.nextItem();
            if (mfpt == null) {
                break;
            }
            Segment2DQuadraturePoint pt = mfpt.quadraturePoint;
            MixResult mixResult = mixer.mix(pt.coord, pt.segment);

            assembler.setWeight(pt.weight);
            assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
            assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
            if (null != lagAssembler) {
                lagProcessor.process(pt);
                lagAssembler.setLagrangeShapeFunctionValue(
                        lagProcessor.getLagrangeAssemblyIndes(),
                        lagProcessor.getLagrangeShapeFunctionValue());
            }
            assembler.setLoad(mfpt.load, mfpt.loadValidity);
            assembler.assembleDirichlet();
            if (null != observer) {
                observer.dirichletProcessed(this);
            }
        }
    }

    @Override
    public void run() {
        logger.info("processing with :{}", mixer);
        processVolume();
        logger.info("processed volume");
        processNeumann();
        logger.info("processed neumann");
        processDirichlet();
        logger.info("processed dirichlet");
    }

    public void setAssembler(Assembler assembler) {
        this.assembler = assembler;
    }

    public Assembler getAssembler() {
        return assembler;
    }

    public void setMixer(Mixer mixer) {
        this.mixer = mixer;
    }

    public void setLagrangeProcessor(LinearLagrangeDirichletProcessor lagProcessor) {
        this.lagProcessor = lagProcessor;
    }

    public void setVolumeSynchronizedIterator(
            SynchronizedIteratorWrapper<MFQuadraturePoint<QuadraturePoint>> volumeSynchronizedIterator) {
        this.volumeSynchronizedIterator = volumeSynchronizedIterator;
    }

    public void setNeumannSynchronizedIterator(
            SynchronizedIteratorWrapper<MFQuadraturePoint<Segment2DQuadraturePoint>> neumannSynchronizedIterator) {
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
    }

    public void setDirichletSynchronizedIterator(
            SynchronizedIteratorWrapper<MFQuadraturePoint<Segment2DQuadraturePoint>> dirichletSynchronizedIterator) {
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }
}
