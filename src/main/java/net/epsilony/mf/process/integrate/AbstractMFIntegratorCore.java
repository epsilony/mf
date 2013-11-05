/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Collection;
import java.util.List;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.SimpIntegratorCoreObservable;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMFIntegratorCore implements MFIntegratorCore {

    protected Assembler assembler;
    protected MFMixer mixer;
    protected MFIntegrateUnit integrateUnit;
    protected SimpIntegratorCoreObservable observable = new SimpIntegratorCoreObservable(this);

    @Override
    public Assembler getAssembler() {
        return assembler;
    }

    @Override
    public void setAssembler(Assembler assembler) {
        this.assembler = assembler;
    }

    @Override
    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
    }

    @Override
    public void setIntegrateUnit(MFIntegrateUnit integrateUnit) {
        this.integrateUnit = integrateUnit;
    }

    @Override
    public boolean addObserver(MFIntegratorObserver observer) {
        return observable.addObserver(observer);
    }

    @Override
    public boolean addObservers(Collection<? extends MFIntegratorObserver> c) {
        return observable.addObservers(c);
    }

    @Override
    public boolean removeObserver(MFIntegratorObserver observer) {
        return observable.removeObserver(observer);
    }

    @Override
    public void removeObservers() {
        observable.removeObservers();
    }

    @Override
    public List<MFIntegratorObserver> getObservers() {
        return observable.getObservers();
    }
}
