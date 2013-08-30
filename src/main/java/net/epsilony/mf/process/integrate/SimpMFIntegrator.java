/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.tb.synchron.SynchronizedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFIntegrator<V, N, D> implements MFIntegrator<V, N, D> {

    SynchronizedIterator<V> volumeSynchronizedIterator;
    SynchronizedIterator<D> dirichletSynchronizedIterator;
    SynchronizedIterator<N> neumannSynchronizedIterator;
    public static Logger logger = LoggerFactory.getLogger(SimpMFIntegrator.class);
    MFIntegratorCore<V, N, D> core;
    MFIntegratorObserver observer;

    @Override
    public void processVolume() {
        for (V volumeObj = volumeSynchronizedIterator.nextItem(); volumeObj != null; volumeObj = volumeSynchronizedIterator.nextItem()) {
            core.integrateVolume(volumeObj);
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
        for (N neuObject = neumannSynchronizedIterator.nextItem(); neuObject != null; neuObject = neumannSynchronizedIterator.nextItem()) {
            core.integrateNeumann(neuObject);
            if (null != observer) {
                observer.neumannProcessed(this);
            }
        }
    }

    @Override
    public void processDirichlet() {
        if (null == dirichletSynchronizedIterator) {
            return;
        }
        for (D diriObject = dirichletSynchronizedIterator.nextItem(); diriObject != null; diriObject = dirichletSynchronizedIterator.nextItem()) {
            core.integrateDirichlet(diriObject);
        }
    }

    @Override
    public void run() {
        logger.info("processing with :{}", core);
        processVolume();
        logger.info("processed volume");
        processNeumann();
        logger.info("processed neumann");
        processDirichlet();
        logger.info("processed dirichlet");
    }

    @Override
    public void setDirichletIterator(SynchronizedIterator<D> dirichletSynchronizedIterator) {
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }

    @Override
    public void setNeumannIterator(SynchronizedIterator<N> neumannSynchronizedIterator) {
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
    }

    public void setObserver(MFIntegratorObserver observer) {
        this.observer = observer;
    }

    @Override
    public void setVolumeIterator(
            SynchronizedIterator<V> volumeSynchronizedIterator) {
        this.volumeSynchronizedIterator = volumeSynchronizedIterator;
    }

    @Override
    public void setIntegrateCore(MFIntegratorCore<V, N, D> core) {
        this.core = core;
    }

    @Override
    public MFIntegratorCore<V, N, D> getIntegrateCore() {
        return core;
    }
}
