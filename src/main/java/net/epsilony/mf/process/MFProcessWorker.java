/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.project.quadrature_task.MFQuadraturePoint;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.assembler.Assembler;
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
    SynchronizedIteratorWrapper<MFQuadraturePoint> volumeSynchronizedIterator;
    SynchronizedIteratorWrapper<MFQuadraturePoint> neumannSynchronizedIterator;
    SynchronizedIteratorWrapper<MFQuadraturePoint> dirichletSynchronizedIterator;
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
            MFQuadraturePoint pt = volumeSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            MixResult mixResult = mixer.mix(pt.coord, pt.segment);
            assembler.setWeight(pt.weight);

            assembler.setShapeFunctionValue(mixResult.getNodesAssemblyIndes(), mixResult.getShapeFunctionValues());
            assembler.setLoad(pt.value, null);
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
            MFQuadraturePoint pt = neumannSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            MixResult mixResult = mixer.mix(pt.coord, pt.segment);
            assembler.setWeight(pt.weight);
            assembler.setShapeFunctionValue(mixResult.getNodesAssemblyIndes(), mixResult.getShapeFunctionValues());
            assembler.setLoad(pt.value, null);
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
            MFQuadraturePoint pt = dirichletSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            MixResult mixResult = mixer.mix(pt.coord, pt.segment);

            assembler.setWeight(pt.weight);
            assembler.setShapeFunctionValue(mixResult.getNodesAssemblyIndes(), mixResult.getShapeFunctionValues());
            if (null != lagAssembler) {
                lagProcessor.process(pt);
                lagAssembler.setLagrangeShapeFunctionValue(
                        lagProcessor.getLagrangleAssemblyIndes(),
                        lagProcessor.getLagrangleShapeFunctionValue());
            }
            assembler.setLoad(pt.value, pt.mark);
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
            SynchronizedIteratorWrapper<MFQuadraturePoint> volumeSynchronizedIterator) {
        this.volumeSynchronizedIterator = volumeSynchronizedIterator;
    }

    public void setNeumannSynchronizedIterator(
            SynchronizedIteratorWrapper<MFQuadraturePoint> neumannSynchronizedIterator) {
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
    }

    public void setDirichletSynchronizedIterator(
            SynchronizedIteratorWrapper<MFQuadraturePoint> dirichletSynchronizedIterator) {
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }
}
