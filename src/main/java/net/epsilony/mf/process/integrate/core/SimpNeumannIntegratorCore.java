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

package net.epsilony.mf.process.integrate.core;

import java.util.Map;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.observer.MFIntegratorStatus;
import net.epsilony.mf.process.integrate.unit.MFBoundaryIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpNeumannIntegratorCore extends AbstractMFIntegratorCore {

    @Override
    public void integrate() {
        MFBoundaryIntegratePoint mfpt = (MFBoundaryIntegratePoint) integrateUnit;
        mixer.setDiffOrder(0);
        mixer.setCenter(mfpt.getCoord());
        mixer.setBoundary(mfpt.getBoundary());
        mixer.setUnitOutNormal(mfpt.getUnitOutNormal());
        MixResult mixResult = mixer.mix();
        assembler.setWeight(mfpt.getWeight());
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setLoad(mfpt.getLoad(), null);
        assembler.assemble();

        Map<MFIntegratorObserverKey, Object> data = observable.getDefaultData();
        data.put(MFIntegratorObserverKey.COORD, mfpt.getCoord());
        data.put(MFIntegratorObserverKey.BOUNDARY, mfpt.getBoundary());
        data.put(MFIntegratorObserverKey.OUT_NORMAL, mfpt.getUnitOutNormal());
        data.put(MFIntegratorObserverKey.MIX_RESULT, mixResult);
        data.put(MFIntegratorObserverKey.LOAD, mfpt.getLoad());
        data.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.CORE_UNIT_INTEGRATED);
        data.put(MFIntegratorObserverKey.WEIGHT, mfpt.getWeight());
        observable.apprise(data);
    }
}
