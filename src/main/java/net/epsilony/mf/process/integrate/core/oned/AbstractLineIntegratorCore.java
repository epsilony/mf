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
package net.epsilony.mf.process.integrate.core.oned;

import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.integrate.core.AbstractMFIntegratorCore;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpDirichletIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpNeumannIntegratorCore;
import net.epsilony.mf.process.integrate.core.SimpVolumeMFIntegratorCore;
import net.epsilony.mf.process.integrate.tool.LinearQuadratureSupport;
import net.epsilony.mf.process.integrate.unit.RawMFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.unit.RawMFIntegratePoint;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public abstract class AbstractLineIntegratorCore extends AbstractMFIntegratorCore {

    protected final MFIntegratorCore subIntegratorCore;
    protected final RawMFIntegratePoint integratePoint;
    protected final LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();

    /**
     * 
     */
    public AbstractLineIntegratorCore(MFProcessType processType) {
        super();
        this.processType = processType;
        switch (processType) {
        case VOLUME:
            subIntegratorCore = new SimpVolumeMFIntegratorCore();
            integratePoint = new RawMFIntegratePoint();
            break;
        case NEUMANN:
            subIntegratorCore = new SimpNeumannIntegratorCore();
            integratePoint = new RawMFBoundaryIntegratePoint();
            break;
        case DIRICHLET:
            subIntegratorCore = new SimpDirichletIntegratorCore();
            integratePoint = new RawMFBoundaryIntegratePoint();
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void setIntegralDegree(int integralDegree) {
        super.setIntegralDegree(integralDegree);
        linearQuadratureSupport.setQuadratureDegree(integralDegree);
    }

    @Override
    public void setAssembler(Assembler assembler) {
        super.setAssembler(assembler);
        subIntegratorCore.setAssembler(assembler);
    }

    @Override
    public void setMixer(MFMixer mixer) {
        super.setMixer(mixer);
        subIntegratorCore.setMixer(mixer);
    }

}