/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.quadrature_task;

import java.util.List;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFQuadratureTask {

    List<MFQuadraturePoint<QuadraturePoint>> volumeTasks();

    List<MFQuadraturePoint<Segment2DQuadraturePoint>> neumannTasks();

    List<MFQuadraturePoint<Segment2DQuadraturePoint>> dirichletTasks();
}
