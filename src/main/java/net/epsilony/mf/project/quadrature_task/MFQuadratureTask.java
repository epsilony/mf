/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.quadrature_task;

import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFQuadratureTask {

    SynchronizedIterator<MFQuadraturePoint<QuadraturePoint>> volumeTasks();

    SynchronizedIterator<MFQuadraturePoint<Segment2DQuadraturePoint>> neumannTasks();

    SynchronizedIterator<MFQuadraturePoint<Segment2DQuadraturePoint>> dirichletTasks();
}
