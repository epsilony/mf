/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.List;
import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.mf.util.persistence.MFHibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawMFIntegrateTask<V, N, D> implements MFIntegrateTask<V, N, D> {

    String taskName;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public RawMFIntegrateTask() {
    }

    public RawMFIntegrateTask(MFIntegrateTask<V, N, D> task) {
        volumeTasks = task.volumeTasks();
        neumannTasks = task.neumannTasks();
        dirichletTasks = task.dirichletTasks();
    }
    int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
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

    public List<V> getVolumeTasks() {
        return volumeTasks;
    }

    public List<N> getNeumannTasks() {
        return neumannTasks;
    }

    public List<D> getDirichletTasks() {
        return dirichletTasks;
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

    public static void main(String[] args) {
        RawMFIntegrateTask mfIntegrateTask = new RawMFIntegrateTask(SimpMFMechanicalProject.genTimoshenkoProjectFactory().produce().getMFIntegrateTask());
        Configuration config = new Configuration();
        config.configure();
        config.setProperty("hibernate.connection.url", "jdbc:sqlite:raw_integrate_task.sqlite");
        config.setProperty("hibernate.hbm2ddl.auto", "create");
        SessionFactory factory = MFHibernateUtil.newSessionFactory(config);
        long start = System.nanoTime();
        Session session = factory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(mfIntegrateTask);
        session.getTransaction().commit();
        session.close();
        long end = System.nanoTime();
        System.out.println("end-start = " + (end - start));

    }
}
