/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.LinearLagrangeDirichletProcessor;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.slf4j.Logger;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMFIntegrator implements MFIntegrator {

    Assembler assembler;
    SynchronizedIterator<MFBoundaryIntegratePoint> dirichletSynchronizedIterator;
    LinearLagrangeDirichletProcessor lagProcessor;
    MFMixer mixer;
    SynchronizedIterator<MFBoundaryIntegratePoint> neumannSynchronizedIterator;
    MFIntegratorObserver observer;

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
            MFBoundaryIntegratePoint mfpt = dirichletSynchronizedIterator.nextItem();
            if (mfpt == null) {
                break;
            }
            MixResult mixResult = mixer.mix(mfpt.getCoord(), mfpt.getBoundary());
            assembler.setWeight(mfpt.getWeight());
            assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
            assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
            if (null != lagAssembler) {
                lagProcessor.process(mfpt);
                lagAssembler.setLagrangeShapeFunctionValue(lagProcessor.getLagrangeAssemblyIndes(), lagProcessor.getLagrangeShapeFunctionValue());
            }
            assembler.setLoad(mfpt.getLoad(), mfpt.getLoadValidity());
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
            MFBoundaryIntegratePoint mfpt = neumannSynchronizedIterator.nextItem();
            if (mfpt == null) {
                break;
            }
            MixResult mixResult = mixer.mix(mfpt.getCoord(), mfpt.getBoundary());
            assembler.setWeight(mfpt.getWeight());
            assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
            assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setLoad(mfpt.getLoad(), null);
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

    public void setDirichletSynchronizedIterator(SynchronizedIterator<MFBoundaryIntegratePoint> dirichletSynchronizedIterator) {
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }

    public void setLagrangeProcessor(LinearLagrangeDirichletProcessor lagProcessor) {
        this.lagProcessor = lagProcessor;
    }

    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
    }

    public void setNeumannSynchronizedIterator(SynchronizedIterator<MFBoundaryIntegratePoint> neumannSynchronizedIterator) {
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
    }

    public void setObserver(MFIntegratorObserver observer) {
        this.observer = observer;
    }
}
