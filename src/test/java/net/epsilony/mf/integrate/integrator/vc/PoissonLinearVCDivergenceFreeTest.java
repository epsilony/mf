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
package net.epsilony.mf.integrate.integrator.vc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.epsilony.mf.integrate.integrator.config.IntegratorBaseConfig;
import net.epsilony.mf.integrate.integrator.config.IntegratorsGroup;
import net.epsilony.mf.integrate.integrator.vc.config.PoissonLinearVCConfig;
import net.epsilony.mf.integrate.integrator.vc.config.VCIntegratorBaseConfig;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.influence.config.InfluenceBaseConfig;
import net.epsilony.mf.model.sample.PoissonPatchModelFactory2D;
import net.epsilony.mf.model.sample.config.PoissonLinearSampleConfig;
import net.epsilony.mf.model.sample.config.SampleConfigBase;
import net.epsilony.mf.model.search.config.TwoDSimpSearcherConfig;
import net.epsilony.mf.process.assembler.T2Value;
import net.epsilony.mf.process.assembler.config.PoissonVolumeAssemblerConfig;
import net.epsilony.mf.process.assembler.matrix.MatrixHub;
import net.epsilony.mf.process.config.MixerConfig;
import net.epsilony.mf.process.config.ProcessConfigs;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.shape_func.config.ShapeFunctionBaseConfig;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.math.Pds2;
import net.epsilony.tb.common_func.BasesFunction;
import net.epsilony.tb.common_func.MonomialBases2D;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class PoissonLinearVCDivergenceFreeTest {
    private AnnotationConfigApplicationContext processorContext;
    private AnnotationConfigApplicationContext modelFactoryContext;
    private double influenceRadius;
    private int quadratureDegree;
    private CommonAnalysisModelHub modelHub;
    private PoissonPatchModelFactory2D modelFactory;
    private AnalysisModel model;
    private MatrixHub matrixHub;
    private IntegrateUnitsGroup integrateUnitsGroup;
    public static final Logger logger = LoggerFactory.getLogger(PoissonLinearVCDivergenceFreeTest.class);

    @Test
    public void testLinearDivergenceFree() {
        processorContext = new AnnotationConfigApplicationContext();
        processorContext.register(ProcessConfigs.simpConfigClasses(PoissonVolumeAssemblerConfig.class,
                ConstantInfluenceConfig.class, TwoDSimpSearcherConfig.class).toArray(new Class<?>[0]));
        processorContext.register(PoissonLinearVCConfig.class, LinearBasesConfig.class);
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonLinearSampleConfig.class,
                GridRowColNumConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;

        logger.debug(this.toString());

        modelHub = processorContext.getBean(CommonAnalysisModelHub.class);
        modelFactory = modelFactoryContext.getBean(PoissonPatchModelFactory2D.class);
        model = modelFactory.get();
        modelHub.setAnalysisModel(model);

        @SuppressWarnings("unchecked")
        WeakBus<Double> infRadBus = (WeakBus<Double>) processorContext
                .getBean(ConstantInfluenceConfig.CONSTANT_INFLUCENCE_RADIUS_BUS);

        infRadBus.post(influenceRadius);

        processorContext.getBean(InfluenceBaseConfig.INFLUENCE_PROCESSOR, Runnable.class).run();
        @SuppressWarnings("unchecked")
        WeakBus<Double> mixerRadiusBus = (WeakBus<Double>) processorContext.getBean(MixerConfig.MIXER_MAX_RADIUS_BUS);
        mixerRadiusBus.post(influenceRadius);

        matrixHub = processorContext.getBean(MatrixHub.class);
        matrixHub.post();

        @SuppressWarnings("unchecked")
        WeakBus<Integer> quadDegreeBus = (WeakBus<Integer>) processorContext
                .getBean(IntegratorBaseConfig.QUADRATURE_DEGREE_BUS);
        quadDegreeBus.post(quadratureDegree);
        integrateUnitsGroup = model.getIntegrateUnitsGroup();

        processorContext.getBean(VCIntegratorBaseConfig.VC_INTEGRATORS_GROUP_PROTO);
        @SuppressWarnings("unchecked")
        List<IntegratorsGroup> vcIntegratorGroups = (List<IntegratorsGroup>) processorContext
                .getBean(VCIntegratorBaseConfig.VC_INTEGRATORS_GROUPS);
        IntegratorsGroup vcIntegratorsGroup = vcIntegratorGroups.get(0);

        @SuppressWarnings("unchecked")
        Consumer<Object> vcVolume = (Consumer<Object>) vcIntegratorsGroup.getVolume();
        integrateUnitsGroup.getVolume().forEach(vcVolume);
        @SuppressWarnings("unchecked")
        Consumer<? super Object> vcNeumann = (Consumer<? super Object>) vcIntegratorsGroup.getNeumann();
        integrateUnitsGroup.getNeumann().forEach(vcNeumann);
        @SuppressWarnings("unchecked")
        Consumer<? super Object> vcDirichlet = (Consumer<? super Object>) vcIntegratorsGroup.getDirichlet();
        integrateUnitsGroup.getDirichlet().forEach(vcDirichlet);

        CommonVCAssemblyIndexMap commonVCAssemblyIndexMap = processorContext.getBean(
                VCIntegratorBaseConfig.COMMON_VC_ASSEMBLY_INDEX_MAP, CommonVCAssemblyIndexMap.class);
        commonVCAssemblyIndexMap.solveVCNodes();

        for (int asmId = 0; asmId < modelHub.getNodes().size(); asmId++) {
            VCIntegralNode vcNode = commonVCAssemblyIndexMap.getVCNode(asmId);
            assertEquals(asmId, vcNode.getAssemblyIndex());
            for (double d : vcNode.getVC()) {
                assertTrue(Double.isFinite(d));
            }
        }

        SimpIntegralMixRecorder volumeRecorder = processorContext.getBean(
                VCIntegratorBaseConfig.VOLUME_VC_MIX_RECORDER, SimpIntegralMixRecorder.class);
        SimpIntegralMixRecorder neumannRecorder = processorContext.getBean(
                VCIntegratorBaseConfig.NEUMANN_VC_MIX_RECORDER, SimpIntegralMixRecorder.class);
        SimpIntegralMixRecorder dirichletRecorder = processorContext.getBean(
                VCIntegratorBaseConfig.DIRICHLET_VC_MIX_RECORDER, SimpIntegralMixRecorder.class);
        ArrayList<IntegralMixRecordEntry> volumeRecords = volumeRecorder.gatherRecords();
        ArrayList<IntegralMixRecordEntry> neumannRecords = neumannRecorder.gatherRecords();
        ArrayList<IntegralMixRecordEntry> dirichletRecords = dirichletRecorder.gatherRecords();

        double[][] volumeInt = new double[modelHub.getNodes().size()][2];
        double[][] bndInt = new double[modelHub.getNodes().size()][2];
        double[][] volumeIntNoneVC = new double[modelHub.getNodes().size()][2];
        double[][] bndIntNoneVC = new double[modelHub.getNodes().size()][2];

        AsymMixRecordToT2Value asymMixRecordToT2Value = processorContext.getBean(AsymMixRecordToT2Value.class);
        volumeRecords.forEach(entry -> {
            double weight = entry.getWeight();
            ShapeFunctionValue sv = entry.getShapeFunctionValue();
            double[] coord = entry.getGeomPoint().getCoord();
            T2Value vcValue = asymMixRecordToT2Value.apply(entry);
            for (int i = 0; i < sv.size(); i++) {
                int asmId = sv.getNodeAssemblyIndex(i);
                PartialValue perNodeVC = vcValue.getTestValue().sub(i);
                for (int pd = 1; pd < sv.partialSize(); pd++) {
                    volumeInt[asmId][pd - 1] += perNodeVC.get(pd) * weight;
                    volumeIntNoneVC[asmId][pd - 1] += sv.get(i, pd) * weight;
                }
            }
        });

        Consumer<? super IntegralMixRecordEntry> bndInter = entry -> {
            double weight = entry.getWeight();
            ShapeFunctionValue sv = entry.getShapeFunctionValue();
            Segment seg = (Segment) entry.getGeomPoint().getGeomUnit();
            double[] outNorm = Segment2DUtils.chordUnitOutNormal(seg, null);
            T2Value vcValue = asymMixRecordToT2Value.apply(entry);
            for (int i = 0; i < sv.size(); i++) {
                int asmId = sv.getNodeAssemblyIndex(i);
                PartialValue perNodeVC = vcValue.getTestValue().sub(i);
                for (int pd : new int[] { Pds2.U_x, Pds2.U_y }) {
                    bndInt[asmId][pd - 1] += perNodeVC.get(0) * outNorm[pd - 1] * weight;
                    bndIntNoneVC[asmId][pd - 1] += sv.get(i, 0) * outNorm[pd - 1] * weight;
                }
            }
        };
        neumannRecords.forEach(bndInter);
        dirichletRecords.forEach(bndInter);

        double errorLimit = 1e-15;
        for (int asmIndex = 0; asmIndex < volumeInt.length; asmIndex++) {
            double[] vol = volumeInt[asmIndex];
            double[] bnd = bndInt[asmIndex];
            double[] volNonVc = volumeIntNoneVC[asmIndex];
            double[] bndNonVc = bndIntNoneVC[asmIndex];

            assertArrayEquals(vol, bnd, errorLimit);
            double distance = MathArrays.distance(vol, bnd);
            double distanceNonVc = MathArrays.distance(volNonVc, bndNonVc);
            assertTrue(distance < distanceNonVc);
        }
    }

    @Configuration
    public static class GridRowColNumConfig {
        @Bean(name = SampleConfigBase.RECT_SAMPLE_ROW_COL_NUM)
        public int gridRowColNum() {
            return 4;
        }
    }

    @Configuration
    public static class LinearBasesConfig {

        @Bean(name = ShapeFunctionBaseConfig.BASES_FUNCTION_PROTO)
        @Scope("prototype")
        public BasesFunction basesFunction() {
            return new MonomialBases2D(1);
        }
    }
}
