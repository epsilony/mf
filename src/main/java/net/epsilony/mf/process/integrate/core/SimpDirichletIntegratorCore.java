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

import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.process.LinearLagrangeDirichletProcessor;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.assembler.LagrangleAssembler;
import net.epsilony.mf.process.integrate.unit.MFBoundaryIntegratePoint;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpDirichletIntegratorCore extends AbstractMFIntegratorCore {

    LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();

    public SimpDirichletIntegratorCore() {
        super();
        processType = MFProcessType.DIRICHLET;
    }

    @Override
    public void integrate() {
        MFBoundaryIntegratePoint mfpt = (MFBoundaryIntegratePoint) integrateUnit;
        mixer.setDiffOrder(0);
        boolean lagDiri = isAssemblyDirichletByLagrange();
        Assembler assembler = assemblersGroup.get(AssemblerType.ASM_DIRICHLET);
        LagrangleAssembler lagAssembler = null;
        if (lagDiri) {
            lagAssembler = (LagrangleAssembler) assembler;
        }
        mixer.setCenter(mfpt.getCoord());
        mixer.setBoundary(mfpt.getBoundary());
        MixResult mixResult = mixer.mix();
        assembler.setWeight(mfpt.getWeight());
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        TIntArrayList lagrangeAssemblyIndes = null;
        double[] lagrangeShapeFunctionValue = null;
        if (null != lagAssembler) {
            lagProcessor.process(mfpt);
            lagrangeAssemblyIndes = lagProcessor.getLagrangeAssemblyIndes();
            lagrangeShapeFunctionValue = lagProcessor.getLagrangeShapeFunctionValue();
            lagAssembler.setLagrangeShapeFunctionValue(lagrangeAssemblyIndes, lagrangeShapeFunctionValue);
        }
        assembler.setLoad(mfpt.getLoad(), mfpt.getLoadValidity());
        assembler.assemble();
    }

    public boolean isAssemblyDirichletByLagrange() {
        return lagProcessor != null && assemblersGroup.get(AssemblerType.ASM_DIRICHLET) instanceof LagrangleAssembler;
    }

}
