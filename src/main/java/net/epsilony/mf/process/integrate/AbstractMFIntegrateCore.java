/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.LinearLagrangeDirichletProcessor;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangeAssembler;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <V>
 */
public abstract class AbstractMFIntegrateCore implements MFIntegratorCore {

    transient Assembler assembler;
    LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();
    transient MFMixer mixer;

    @Override
    public Assembler getAssembler() {
        return assembler;
    }

    @Override
    public void integrateDirichlet(MFIntegratePoint pt) {
        MFBoundaryIntegratePoint mfpt = (MFBoundaryIntegratePoint) pt;
        mixer.setDiffOrder(0);
        boolean lagDiri = isAssemblyDirichletByLagrange();
        LagrangeAssembler lagAssembler = null;
        if (lagDiri) {
            lagAssembler = (LagrangeAssembler) assembler;
        }
        mixer.setCenter(mfpt.getCoord());
        mixer.setBoundary(mfpt.getBoundary());
        MixResult mixResult = mixer.mix();
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
    public void integrateNeumann(MFIntegratePoint pt) {
        MFBoundaryIntegratePoint mfpt = (MFBoundaryIntegratePoint) pt;
        mixer.setDiffOrder(0);
        mixer.setCenter(mfpt.getCoord());
        mixer.setBoundary(mfpt.getBoundary());
        MixResult mixResult = mixer.mix();
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
        assembler.prepare();
    }

    @Override
    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
    }
}
