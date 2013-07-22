/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.quadrature_task;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFQuadratureTask {

    List<MFQuadraturePoint> volumeTasks();

    List<MFQuadraturePoint> neumannTasks();

    List<MFQuadraturePoint> dirichletTasks();
}
