/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.process.integrate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.SimpIntegratorObservable;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractMFIntegrator implements MFIntegrator {

    Map<MFProcessType, Assembler> assemblersGroup;
    Map<MFProcessType, MFIntegratorCore> integratorCoresGroup;
    Map<MFProcessType, SynchronizedIterator<MFIntegrateUnit>> integrateUnitsGroup;
    MFMatrix mainMatrix;
    MFMatrix mainVector;
    MFMixer mixer;
    RawMFIntegrateResult integrateResult;
    protected final SimpIntegratorObservable observable = new SimpIntegratorObservable(this);

    @Override
    public void setAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroup) {
        this.assemblersGroup = assemblersGroup;
    }

    @Override
    public void setIntegratorCoresGroup(Map<MFProcessType, MFIntegratorCore> integratorCoresGroup) {
        this.integratorCoresGroup = integratorCoresGroup;
    }

    @Override
    public void setIntegrateUnitsGroup(Map<MFProcessType, SynchronizedIterator<MFIntegrateUnit>> integrateUnitsGroup) {
        this.integrateUnitsGroup = integrateUnitsGroup;
    }

    @Override
    public void setMainMatrix(MFMatrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    @Override
    public void setMainVector(MFMatrix mainVector) {
        this.mainVector = mainVector;
    }

    @Override
    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
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
