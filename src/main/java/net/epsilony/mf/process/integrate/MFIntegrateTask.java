/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrateTask {

    SynchronizedIterator<MFIntegratePoint> volumeTasks();

    SynchronizedIterator<MFBoundaryIntegratePoint> neumannTasks();

    SynchronizedIterator<MFBoundaryIntegratePoint> dirichletTasks();
}
