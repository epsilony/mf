/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegratorObserver {

    void volumeProcessed(MFIntegrator mfProcessRunnable);

    void neumannProcessed(MFIntegrator mfProcessRunnable);

    void dirichletProcessed(MFIntegrator mfProcessRunnable);
}
