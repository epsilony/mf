/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrator{

    void processVolume();

    void processNeumann();

    void processDirichlet();

    MFIntegratorCore getIntegrateCore();

    void setIntegrateCore(MFIntegratorCore core);

    void setVolumeIterator(SynchronizedIterator<MFIntegratePoint> volumeIter);

    void setNeumannIterator(SynchronizedIterator<MFIntegratePoint> neumIter);

    void setDirichletIterator(SynchronizedIterator<MFIntegratePoint> diriIter);
}
