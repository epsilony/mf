/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.List;
import net.epsilony.tb.IntIdentity;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrateTask<V, N, D> extends IntIdentity {

    List<V> volumeTasks();

    List<N> neumannTasks();

    List<D> dirichletTasks();
}
