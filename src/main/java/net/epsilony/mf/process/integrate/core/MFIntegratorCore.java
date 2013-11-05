/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.core;

import java.io.Serializable;
import java.util.Map;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.util.MFObservable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegratorCore extends Serializable, MFObservable<MFIntegratorObserver, Map<MFIntegratorObserverKey, Object>> {

    void setAssembler(Assembler assembler);

    Assembler getAssembler();

    void setMixer(MFMixer mixer);

    void setIntegrateUnit(MFIntegrateUnit integrateUnit);

    void integrate();
}
