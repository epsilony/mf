/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.project.quadrature_task.MFQuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.slf4j.Logger;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractProcessWorker implements MFIntegrator {

    Assembler assembler;
    SynchronizedIterator<MFQuadraturePoint<Segment2DQuadraturePoint>> dirichletSynchronizedIterator;
    LinearLagrangeDirichletProcessor lagProcessor;
    Mixer mixer;
    SynchronizedIterator<MFQuadraturePoint<Segment2DQuadraturePoint>> neumannSynchronizedIterator;
    MFProcessWorkerObserver observer;

    public Assembler getAssembler() {
        return assembler;
    }

    public boolean isAssemblyDirichletByLagrange() {
        return lagProcessor != null && assembler instanceof LagrangeAssembler;
    }

    @Override
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
                lagAssembler.setLagrangeShapeFunctionValue(lagProcessor.getLagrangeAssemblyIndes(), lagProcessor.getLagrangeShapeFunctionValue());
            }
            assembler.setLoad(mfpt.load, mfpt.loadValidity);
            assembler.assembleDirichlet();
            if (null != observer) {
                observer.dirichletProcessed(this);
            }
        }
    }

    @Override
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

    protected abstract Logger getLogger();

    
    @Override
    public void run() {
        Logger logger = getLogger();
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

    public void setDirichletSynchronizedIterator(SynchronizedIterator<MFQuadraturePoint<Segment2DQuadraturePoint>> dirichletSynchronizedIterator) {
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }

    public void setLagrangeProcessor(LinearLagrangeDirichletProcessor lagProcessor) {
        this.lagProcessor = lagProcessor;
    }

    public void setMixer(Mixer mixer) {
        this.mixer = mixer;
    }

    public void setNeumannSynchronizedIterator(SynchronizedIterator<MFQuadraturePoint<Segment2DQuadraturePoint>> neumannSynchronizedIterator) {
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
    }

    public void setObserver(MFProcessWorkerObserver observer) {
        this.observer = observer;
    }
}
