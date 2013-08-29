/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrateTask {

    SynchronizedIterator<MFIntegratePoint<QuadraturePoint>> volumeTasks();

    SynchronizedIterator<MFIntegratePoint<Segment2DQuadraturePoint>> neumannTasks();

    SynchronizedIterator<MFIntegratePoint<Segment2DQuadraturePoint>> dirichletTasks();
}
