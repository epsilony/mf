/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrator {

    void integrate();

    void setIntegrateCore(MFIntegratorCore core);

    void setIntegrateUnits(SynchronizedIterator<MFIntegratePoint> volumeIter);
}
