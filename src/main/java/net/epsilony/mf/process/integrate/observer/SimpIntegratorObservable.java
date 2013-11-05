/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import net.epsilony.mf.util.AbstractObservable;
import java.util.EnumMap;
import java.util.Map;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpIntegratorObservable extends AbstractObservable<MFIntegratorObserver, Map<MFIntegratorObserverKey, Object>> {

    EnumMap<MFIntegratorObserverKey, Object> data = new EnumMap<>(MFIntegratorObserverKey.class);
    MFIntegrator integrator;
    MFIntegratorCore core;

    public SimpIntegratorObservable(MFIntegrator integrator) {
        this.integrator = integrator;
    }

    public Map<MFIntegratorObserverKey, Object> getDefaultData() {
        data.clear();
        data.put(MFIntegratorObserverKey.INTEGRATOR, integrator);
        data.put(MFIntegratorObserverKey.CORE, core);
        return data;
    }
}
