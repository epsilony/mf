/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import java.util.Map;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegratorObserver {

    void update(Map<MFIntegratorObserverKey, Object> data);
}
