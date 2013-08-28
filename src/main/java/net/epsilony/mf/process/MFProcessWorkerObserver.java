/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFProcessWorkerObserver {

    void volumeProcessed(MFIntegrator mfProcessRunnable);

    void neumannProcessed(MFIntegrator mfProcessRunnable);

    void dirichletProcessed(MFIntegrator mfProcessRunnable);
}
