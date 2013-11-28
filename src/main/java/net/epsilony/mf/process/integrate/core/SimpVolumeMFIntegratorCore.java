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

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpVolumeMFIntegratorCore extends AbstractMFIntegratorCore {

    public SimpVolumeMFIntegratorCore() {
        super();
        processType = MFProcessType.VOLUME;
    }

    @Override
    public void integrate() {
        MFIntegratePoint integratePoint = (MFIntegratePoint) integrateUnit;
        mixer.setDiffOrder(1);
        mixer.setCenter(integratePoint.getCoord());
        mixer.setBoundary(null);
        MixResult mixResult = mixer.mix();
        Assembler assembler = assemblersGroup.get(AssemblerType.ASM_VOLUME);
        assembler.setWeight(integratePoint.getWeight());
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.assemble();

        double[] load = integratePoint.getLoad();
        if (null != load) {
            Assembler loadAssembler = assemblersGroup.get(AssemblerType.ASM_VOLUME_LOAD);
            loadAssembler.setWeight(integratePoint.getWeight());
            loadAssembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
            loadAssembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
            loadAssembler.setLoad(integratePoint.getLoad(), null);
            loadAssembler.assemble();
        }

    }
}
