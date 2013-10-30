/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.observer.CounterIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.tb.Factory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFIntegratorFactory implements Factory<MFIntegrator> {

    Integer threadNum = null;
    Map<MFProcessType, MFIntegratorCore> coresGroup = null;
    Set<MFIntegratorObserver> observers = new HashSet<>();

    public MFIntegratorFactory() {
        observers.add(new CounterIntegratorObserver());
    }

    public Map<MFProcessType, MFIntegratorCore> getCoresGroup() {
        return coresGroup;
    }

    public void setCoresGroup(Map<MFProcessType, MFIntegratorCore> coresGroup) {
        this.coresGroup = coresGroup;
    }

    public Integer getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
    }

    @Override
    public MFIntegrator produce() {
        MFIntegrator integrator = byThreadsNum();
        integrator.setIntegratorCoresGroup(genCoresGroup());
        integrator.addObservers(observers);
        return integrator;
    }

    private MFIntegrator byThreadsNum() {
        if (threadNum == null || threadNum > 1) {
            return new MultithreadMFIntegrator(threadNum);
        } else {
            return new SimpMFIntegrator();
        }
    }

    private Map<MFProcessType, MFIntegratorCore> genCoresGroup() {
        if (coresGroup != null) {
            return coresGroup;
        } else {
            return MFIntegrateCores.commonCoresGroup();
        }
    }

    public boolean addObserver(MFIntegratorObserver e) {
        return observers.add(e);
    }

}
