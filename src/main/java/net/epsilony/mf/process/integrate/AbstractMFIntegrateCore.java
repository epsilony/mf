/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.LinearLagrangeDirichletProcessor;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;

/**
 *
 * @author epsilon
 */
public abstract class AbstractMFIntegrateCore<V> implements MFIntegratorCore<V, MFBoundaryIntegratePoint, MFBoundaryIntegratePoint> {
    Assembler<? extends Assembler<?>> assembler;
    LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();
    MFMixer mixer;

    public Assembler getAssembler() {
        return assembler;
    }

    @Override
    public void integrateDirichlet(MFBoundaryIntegratePoint mfpt) {
        mixer.setDiffOrder(assembler.getDirichletDiffOrder());
        boolean lagDiri = isAssemblyDirichletByLagrange();
        LagrangeAssembler lagAssembler = null;
        if (lagDiri) {
            lagAssembler = (LagrangeAssembler) assembler;
        }
        MixResult mixResult = mixer.mix(mfpt.getCoord(), mfpt.getBoundary());
        assembler.setWeight(mfpt.getWeight());
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        if (null != lagAssembler) {
            lagProcessor.process(mfpt);
            lagAssembler.setLagrangeShapeFunctionValue(lagProcessor.getLagrangeAssemblyIndes(), lagProcessor.getLagrangeShapeFunctionValue());
        }
        assembler.setLoad(mfpt.getLoad(), mfpt.getLoadValidity());
        assembler.assembleDirichlet();
    }

    @Override
    public void integrateNeumann(MFBoundaryIntegratePoint mfpt) {
        mixer.setDiffOrder(assembler.getNeumannDiffOrder());
        MixResult mixResult = mixer.mix(mfpt.getCoord(), mfpt.getBoundary());
        assembler.setWeight(mfpt.getWeight());
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setLoad(mfpt.getLoad(), null);
        assembler.assembleNeumann();
    }

    public boolean isAssemblyDirichletByLagrange() {
        return lagProcessor != null && assembler instanceof LagrangeAssembler;
    }

    @Override
    public void setAssembler(Assembler assembler) {
        this.assembler = assembler;
    }

    @Override
    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
    }
    
}
