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

    SynchronizedIterator<MFIntegratePoint> integrateUnits;
    public static Logger logger = LoggerFactory.getLogger(SimpMFIntegrator.class);
    MFIntegratorCore core;
    MFIntegratorObserver observer;

    @Override
    public void integrate() {
        if (null == integrateUnits) {
            return;
        }
        for (MFIntegratePoint integrateUnit = integrateUnits.nextItem(); integrateUnit != null; integrateUnit = integrateUnits.nextItem()) {
            core.setIntegrateUnit(integrateUnit);
            core.integrate();
            if (null != observer) {
                observer.integrated(this);
            }
        }
    }

    public void setObserver(MFIntegratorObserver observer) {
        this.observer = observer;
    }

    @Override
    public void setIntegrateUnits(
            SynchronizedIterator<MFIntegratePoint> volumeSynchronizedIterator) {
        this.integrateUnits = volumeSynchronizedIterator;
    }

    @Override
    public void setIntegrateCore(MFIntegratorCore core) {
        this.core = core;
    }

    @Override
    public String toString() {
        return "SimpMFIntegrator{" + "core=" + core + ", observer=" + observer + '}';
    }
}
