/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFProcessWorkerObserver {

    void volumeProcessed(MFProcessWorker mfProcessRunnable);

    void neumannProcessed(MFProcessWorker mfProcessRunnable);

    void dirichletProcessed(MFProcessWorker mfProcessRunnable);
}
