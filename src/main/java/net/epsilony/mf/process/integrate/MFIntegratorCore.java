/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.util.MFObservable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegratorCore extends Serializable, MFObservable<MFIntegratorObserver, Map<MFIntegratorObserverKey, Object>> {

    void setAssembler(Assembler assembler);

    Assembler getAssembler();

    void setMixer(MFMixer mixer);

    void setIntegrateUnit(MFIntegratePoint integrateUnit);

    void integrate();
}
