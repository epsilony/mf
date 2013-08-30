/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrator<V, N, D> extends Runnable {

    void processVolume();

    void processNeumann();

    void processDirichlet();

    MFIntegratorCore<V, N, D> getIntegrateCore();

    void setIntegrateCore(MFIntegratorCore<V, N, D> core);

    void setVolumeIterator(SynchronizedIterator<V> volumeIter);

    void setNeumannIterator(SynchronizedIterator<N> neumIter);

    void setDirichletIterator(SynchronizedIterator<D> diriIter);
}
