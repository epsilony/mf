/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrateTask<V, N, D> {

    List<V> volumeTasks();

    List<N> neumannTasks();

    List<D> dirichletTasks();
}
