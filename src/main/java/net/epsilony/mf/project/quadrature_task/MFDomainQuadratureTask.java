/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.quadrature_task;

import net.epsilony.tb.quadrature.Quadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFDomainQuadratureTask {

    SynchronizedIterator<MFQuadraturePoint<Segment2DQuadraturePoint>> neumannTasks();

    SynchronizedIterator<MFQuadraturePoint<Segment2DQuadraturePoint>> dirichletTasks();

    SynchronizedIterator<Quadrature<Segment2DQuadraturePoint>> volumeDomainTask();
}
