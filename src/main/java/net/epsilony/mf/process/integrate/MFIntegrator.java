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

import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.util.LockableHolder;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.synchron.SynchronizedIterator;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrator {

    void setIntegralDegree(int integralDegree);

    void setIntegratorCoresGroup(Map<MFProcessType, MFIntegratorCore> coresGroup);

    void setAssemblersGroup(Map<AssemblerType, Assembler> assemblersGroups);

    void setMixer(MFMixer mixer);

    void setIntegrateUnitsGroup(Map<MFProcessType, SynchronizedIterator<?>> integrateUnitsGroup);

    void setLoadMap(Map<GeomUnit, LockableHolder<MFLoad>> loadMap);

    void setMainMatrix(MFMatrix mainMatrixFactory);

    void setMainVector(MFMatrix mainVectorFactory);

    void integrate();

    MFIntegrateResult getIntegrateResult();
}
