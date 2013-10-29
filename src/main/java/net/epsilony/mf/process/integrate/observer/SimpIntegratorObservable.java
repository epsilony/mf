/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import net.epsilony.mf.process.integrate.MFIntegrator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpIntegratorObservable {

    ArrayList<MFIntegratorObserver> observers = new ArrayList<>();
    EnumMap<MFIntegratorObserverKey, Object> data = new EnumMap<>(MFIntegratorObserverKey.class);
    MFIntegrator integrator;

    public SimpIntegratorObservable(MFIntegrator integrator) {
        this.integrator = integrator;
    }

    public void apprise() {
        data.put(MFIntegratorObserverKey.INTEGRATOR, integrator);
        for (MFIntegratorObserver observer : observers) {
            observer.update(data);
        }
    }

    public Map<MFIntegratorObserverKey, Object> getObserveData() {
        return data;
    }

    public boolean add(MFIntegratorObserver observer) {
        if (null == observer) {
            throw new IllegalArgumentException();
        }
        return observers.add(observer);
    }

    public boolean remove(MFIntegratorObserver observer) {
        return observers.remove(observer);
    }

    public void clear() {
        observers.clear();
    }

}
