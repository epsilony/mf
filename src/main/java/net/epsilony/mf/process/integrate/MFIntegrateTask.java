/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrateTask {

    List<MFIntegratePoint> volumeTasks();

    List<MFBoundaryIntegratePoint> neumannTasks();

    List<MFBoundaryIntegratePoint> dirichletTasks();
}
