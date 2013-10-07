/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.List;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawMFIntegrateTask implements MFIntegrateTask {

    public RawMFIntegrateTask() {
    }

    public RawMFIntegrateTask(MFIntegrateTask task) {
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
    List<MFIntegratePoint> volumeTasks;
    List<MFIntegratePoint> neumannTasks;
    List<MFIntegratePoint> dirichletTasks;

    public void setVolumeTasks(List<MFIntegratePoint> volumeTasks) {
        this.volumeTasks = volumeTasks;
    }

    public void setNeumannTasks(List<MFIntegratePoint> neumannTasks) {
        this.neumannTasks = neumannTasks;
    }

    public void setDirichletTasks(List<MFIntegratePoint> dirichletTasks) {
        this.dirichletTasks = dirichletTasks;
    }

    public List<MFIntegratePoint> getVolumeTasks() {
        return volumeTasks;
    }

    public List<MFIntegratePoint> getNeumannTasks() {
        return neumannTasks;
    }

    public List<MFIntegratePoint> getDirichletTasks() {
        return dirichletTasks;
    }

    @Override
    public List<MFIntegratePoint> volumeTasks() {
        return volumeTasks;
    }

    @Override
    public List<MFIntegratePoint> neumannTasks() {
        return neumannTasks;
    }

    @Override
    public List<MFIntegratePoint> dirichletTasks() {
        return dirichletTasks;
    }

    @Override
    public String toString() {
        return "RawMFIntegrateTask{" + "volumeTasks(" + volumeTasks.size() + "), neumannTasks(" + neumannTasks.size() + "), dirichletTasks(" + dirichletTasks.size() + ")}";
    }

    public static void main(String[] args) {
//        RawMFIntegrateTask mfIntegrateTask = new RawMFIntegrateTask(SimpMFMechanicalProject.genTimoshenkoProjectFactory().produce().getMFIntegrateTask());
//        Configuration config = new Configuration();
//        config.configure();
//        config.setProperty("hibernate.connection.url", "jdbc:sqlite:raw_integrate_task.sqlite");
//        config.setProperty("hibernate.hbm2ddl.auto", "create");
//        SessionFactory factory = MFHibernateUtil.newSessionFactory(config);
//        long start = System.nanoTime();
//        Session session = factory.openSession();
//        session.beginTransaction();
//        session.saveOrUpdate(mfIntegrateTask);
//        session.getTransaction().commit();
//        session.close();
//        long end = System.nanoTime();
//        System.out.println("end-start = " + (end - start));
    }
}
