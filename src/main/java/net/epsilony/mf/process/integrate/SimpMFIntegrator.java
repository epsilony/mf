/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFIntegrator implements MFIntegrator {

    SynchronizedIterator<MFIntegratePoint> volumeSynchronizedIterator;
    SynchronizedIterator<MFIntegratePoint> dirichletSynchronizedIterator;
    SynchronizedIterator<MFIntegratePoint> neumannSynchronizedIterator;
    public static Logger logger = LoggerFactory.getLogger(SimpMFIntegrator.class);
    MFIntegratorCore core;
    MFIntegratorObserver observer;

    @Override
    public void processVolume() {
        for (MFIntegratePoint volumeObj = volumeSynchronizedIterator.nextItem(); volumeObj != null; volumeObj = volumeSynchronizedIterator.nextItem()) {
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
        for (MFIntegratePoint neuObject = neumannSynchronizedIterator.nextItem(); neuObject != null; neuObject = neumannSynchronizedIterator.nextItem()) {
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
        for (MFIntegratePoint diriObject = dirichletSynchronizedIterator.nextItem(); diriObject != null; diriObject = dirichletSynchronizedIterator.nextItem()) {
            core.integrateDirichlet(diriObject);
        }
    }

    @Override
    public void run() {
        core.getAssembler().prepare();
        logger.info("processing with :{}", core);
        processVolume();
        logger.info("processed volume");
        processNeumann();
        logger.info("processed neumann");
        processDirichlet();
        logger.info("processed dirichlet");
    }

    @Override
    public void setDirichletIterator(SynchronizedIterator<MFIntegratePoint> dirichletSynchronizedIterator) {
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }

    @Override
    public void setNeumannIterator(SynchronizedIterator<MFIntegratePoint> neumannSynchronizedIterator) {
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
    }

    public void setObserver(MFIntegratorObserver observer) {
        this.observer = observer;
    }

    @Override
    public void setVolumeIterator(
            SynchronizedIterator<MFIntegratePoint> volumeSynchronizedIterator) {
        this.volumeSynchronizedIterator = volumeSynchronizedIterator;
    }

    @Override
    public void setIntegrateCore(MFIntegratorCore core) {
        this.core = core;
    }

    @Override
    public MFIntegratorCore getIntegrateCore() {
        return core;
    }

    @Override
    public String toString() {
        return "SimpMFIntegrator{" + "core=" + core + ", observer=" + observer + '}';
    }
}
