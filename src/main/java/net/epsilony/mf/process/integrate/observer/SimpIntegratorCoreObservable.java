/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.util.AbstractObservable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpIntegratorCoreObservable extends AbstractObservable<MFIntegratorObserver, Map<MFIntegratorObserverKey, Object>> implements Serializable {

    EnumMap<MFIntegratorObserverKey, Object> data = new EnumMap<>(MFIntegratorObserverKey.class);
    MFIntegratorCore core;

    public SimpIntegratorCoreObservable(MFIntegratorCore core) {
        this.core = core;
    }

    public Map<MFIntegratorObserverKey, Object> getDefaultData() {
        data.clear();
        data.put(MFIntegratorObserverKey.CORE, core);
        return data;
    }
}
