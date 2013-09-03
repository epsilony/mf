/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawMFIntegrateTask<V, N, D> implements MFIntegrateTask<V, N, D> {

    List<V> volumeTasks;
    List<N> neumannTasks;
    List<D> dirichletTasks;

    public void setVolumeTasks(List<V> volumeTasks) {
        this.volumeTasks = volumeTasks;
    }

    public void setNeumannTasks(List<N> neumannTasks) {
        this.neumannTasks = neumannTasks;
    }

    public void setDirichletTasks(List<D> dirichletTasks) {
        this.dirichletTasks = dirichletTasks;
    }

    @Override
    public List<V> volumeTasks() {
        return volumeTasks;
    }

    @Override
    public List<N> neumannTasks() {
        return neumannTasks;
    }

    @Override
    public List<D> dirichletTasks() {
        return dirichletTasks;
    }

    @Override
    public String toString() {
        return "RawMFIntegrateTask{" + "volumeTasks(" + volumeTasks.size() + "), neumannTasks(" + neumannTasks.size() + "), dirichletTasks(" + dirichletTasks.size() + ")}";
    }
}
