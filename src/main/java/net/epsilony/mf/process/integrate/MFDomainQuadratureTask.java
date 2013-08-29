/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.List;
import net.epsilony.tb.quadrature.Quadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFDomainQuadratureTask {

    List<MFBoundaryIntegratePoint> neumannTasks();

    List<MFBoundaryIntegratePoint> dirichletTasks();

    List<Quadrature<Segment2DQuadraturePoint>> volumeDomainTask();
}
