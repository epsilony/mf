/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.tb.quadrature.Quadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFDomainQuadratureTask {

    SynchronizedIterator<MFIntegratePoint<Segment2DQuadraturePoint>> neumannTasks();

    SynchronizedIterator<MFIntegratePoint<Segment2DQuadraturePoint>> dirichletTasks();

    SynchronizedIterator<Quadrature<Segment2DQuadraturePoint>> volumeDomainTask();
}
