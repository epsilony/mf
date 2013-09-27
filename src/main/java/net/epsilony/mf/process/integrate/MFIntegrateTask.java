/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.List;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.tb.IntIdentity;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrateTask extends IntIdentity {

    List<MFIntegratePoint> volumeTasks();

    List<MFIntegratePoint> neumannTasks();

    List<MFIntegratePoint> dirichletTasks();
}
