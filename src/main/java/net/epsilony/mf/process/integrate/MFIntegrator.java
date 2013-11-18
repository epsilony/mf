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

import java.util.Map;

import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserver;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.util.MFObservable;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;
import net.epsilony.tb.Factory;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrator extends MFObservable<MFIntegratorObserver, Map<MFIntegratorObserverKey, Object>> {

    void setMixerFactory(Factory<? extends MFMixer> mixerFactory);

    void setAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroups);

    void setIntegratorCoresGroup(Map<MFProcessType, MFIntegratorCore> coresGroup);

    void setIntegrateUnitsGroup(Map<MFProcessType, SynchronizedIterator<MFIntegrateUnit>> integrateUnitsGroup);

    void setMainMatrixFactory(MatrixFactory<? extends MFMatrix> mainMatrixFactory);

    void setMainVectorFactory(MatrixFactory<? extends MFMatrix> mainVectorFactory);

    void setMainMatrixSize(int mainMatrixSize);

    void integrate();

    MFIntegrateResult getIntegrateResult();
}
