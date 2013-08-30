/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFStrainStabilizeIntegrateTask {

    List<MFBoundaryIntegratePoint> neumannTasks();

    List<MFBoundaryIntegratePoint> dirichletTasks();

    List<MFStrainStabilizeIntegrateDomain> volumeDomainTask();
}
