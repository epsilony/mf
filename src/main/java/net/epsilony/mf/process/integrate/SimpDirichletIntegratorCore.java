/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import gnu.trove.list.array.TIntArrayList;
import java.util.Map;
import net.epsilony.mf.process.LinearLagrangeDirichletProcessor;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.assembler.LagrangleAssembler;
import net.epsilony.mf.process.integrate.observer.MFIntegratorObserverKey;
import net.epsilony.mf.process.integrate.observer.MFIntegratorStatus;
import net.epsilony.mf.process.integrate.unit.MFBoundaryIntegratePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpDirichletIntegratorCore extends AbstractMFIntegratorCore {

    LinearLagrangeDirichletProcessor lagProcessor = new LinearLagrangeDirichletProcessor();

    @Override
    public void integrate() {
        MFBoundaryIntegratePoint mfpt = (MFBoundaryIntegratePoint) integrateUnit;
        mixer.setDiffOrder(0);
        boolean lagDiri = isAssemblyDirichletByLagrange();
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

        Map<MFIntegratorObserverKey, Object> data = observable.getDefaultData();
        data.put(MFIntegratorObserverKey.COORD, mfpt.getCoord());
        data.put(MFIntegratorObserverKey.BOUNDARY, mfpt.getBoundary());
        data.put(MFIntegratorObserverKey.OUT_NORMAL, mfpt.getUnitOutNormal());
        data.put(MFIntegratorObserverKey.MIX_RESULT, mixResult);
        data.put(MFIntegratorObserverKey.LOAD, mfpt.getLoad());
        data.put(MFIntegratorObserverKey.LOAD_VALIDITY, mfpt.getLoadValidity());
        data.put(MFIntegratorObserverKey.STATUS, MFIntegratorStatus.CORE_UNIT_INTEGRATED);
        data.put(MFIntegratorObserverKey.WEIGHT, mfpt.getWeight());
        data.put(MFIntegratorObserverKey.LAGRANGLE_INDES, lagrangeAssemblyIndes);
        data.put(MFIntegratorObserverKey.LAGRANGLE_SHAPE_FUNCTION,lagrangeShapeFunctionValue);
        observable.apprise(data);
    }

    public boolean isAssemblyDirichletByLagrange() {
        return lagProcessor != null && assembler instanceof LagrangleAssembler;
    }
}
