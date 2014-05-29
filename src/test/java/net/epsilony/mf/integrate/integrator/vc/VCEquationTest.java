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

import static net.epsilony.mf.util.function.FunctionConnectors.oneStreamConsumer;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import javax.annotation.Resource;

import net.epsilony.mf.integrate.integrator.config.IntegralBaseConfig;
import net.epsilony.mf.integrate.integrator.config.MFConsumerGroup;
import net.epsilony.mf.integrate.integrator.config.ThreeStageIntegralCollection;
import net.epsilony.mf.integrate.integrator.config.ThreeStageIntegralConfig;
import net.epsilony.mf.integrate.integrator.vc.config.LinearVCConfig;
import net.epsilony.mf.integrate.integrator.vc.config.QuadricVCConfig;
import net.epsilony.mf.integrate.integrator.vc.config.VCIntegratorBaseConfig;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.influence.config.InfluenceBaseConfig;
import net.epsilony.mf.model.sample.PoissonPatchModelFactory2D;
import net.epsilony.mf.model.sample.config.PoissonLinearSampleConfig;
import net.epsilony.mf.model.sample.config.SampleConfigBase;
import net.epsilony.mf.model.search.config.SearcherBaseHub;
import net.epsilony.mf.model.search.config.TwoDSimpSearcherConfig;
import net.epsilony.mf.process.assembler.T2Value;
import net.epsilony.mf.process.assembler.config.PoissonVolumeAssemblerConfig;
import net.epsilony.mf.process.config.ProcessConfigs;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.shape_func.config.ShapeFunctionBaseConfig;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.math.convention.Pds2;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class VCEquationTest {
    private AnnotationConfigApplicationContext processorContext;
    private AnnotationConfigApplicationContext modelFactoryContext;
    private double influenceRadius;
    private int quadratureDegree;
    private CommonAnalysisModelHub modelHub;
    private PoissonPatchModelFactory2D modelFactory;
    private AnalysisModel model;
    private IntegrateUnitsGroup integrateUnitsGroup;
    public static final Logger logger = LoggerFactory.getLogger(VCEquationTest.class);
    private ArrayList<IntegralMixRecordEntry> volumeRecords;
    private ArrayList<IntegralMixRecordEntry> neumannRecords;
    private ArrayList<IntegralMixRecordEntry> dirichletRecords;
    private double[][] volumeInts;
    private double[][] bndInts;
    private double[][] volumeIntNoneVCs;
    private double[][] bndIntNoneVCs;
    private double errorLimit;
    private final int lINEAR_VC_BASES_SIZE = 2;
    private final int QUADRIC_VC_BASES_SIZE = 6;

    @Test
    public void testLinearDivergenceFree() {
        initProcessContext();
        processorContext.register(LinearVCConfig.class, LinearBasesConfig.class);
        VCIntegratorBaseConfig.addVCBasesDefinition(processorContext, HeavisideXYTransDomainBases2D.class);
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonLinearSampleConfig.class,
                GridRowColNumConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;

        errorLimit = 1e-15;

        initModel();

        doVCIntegral();

        doIntegralOfLinearResults();

        doAsserts();
    }

    @Test
    public void testQuadricIntegralByParts() {
        initProcessContext();
        processorContext.register(QuadricVCConfig.class);
        VCIntegratorBaseConfig.addVCBasesDefinition(processorContext, HeavisideQuadricTransDomainBases2D.class);
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonLinearSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;

        errorLimit = 1e-13;

        initModel();

        doVCIntegral();

        doIntegralOfQuadricResults();

        doAsserts();
    }

    private void doIntegralOfLinearResults() {

        volumeInts = new double[modelHub.getNodes().size()][lINEAR_VC_BASES_SIZE];
        bndInts = new double[modelHub.getNodes().size()][lINEAR_VC_BASES_SIZE];
        volumeIntNoneVCs = new double[modelHub.getNodes().size()][lINEAR_VC_BASES_SIZE];
        bndIntNoneVCs = new double[modelHub.getNodes().size()][lINEAR_VC_BASES_SIZE];

        AsymMixRecordToT2Value asymMixRecordToT2Value = processorContext.getBean(AsymMixRecordToT2Value.class);
        volumeRecords.forEach(entry -> {
            double weight = entry.getWeight();
            ShapeFunctionValue sv = entry.getShapeFunctionValue();
            T2Value vcValue = asymMixRecordToT2Value.apply(entry);
            for (int i = 0; i < sv.size(); i++) {
                int asmId = sv.getNodeAssemblyIndex(i);
                PartialValue perNodeVC = vcValue.getTestValue().sub(i);
                double[] volumeInt = volumeInts[asmId];
                double[] volumeIntNonVc = volumeIntNoneVCs[asmId];

                volumeInt[0] += perNodeVC.get(Pds2.U_x) * weight;
                volumeInt[1] += perNodeVC.get(Pds2.U_y) * weight;

                volumeIntNonVc[0] += sv.get(i, Pds2.U_x) * weight;
                volumeIntNonVc[1] += sv.get(i, Pds2.U_y) * weight;
            }
        });

        Consumer<? super IntegralMixRecordEntry> bndInter = entry -> {
            double weight = entry.getWeight();
            ShapeFunctionValue sv = entry.getShapeFunctionValue();
            MFLine seg = (MFLine) entry.getGeomPoint().getGeomUnit();
            double[] outNorm = MFLine2DUtils.chordUnitOutNormal(seg, null);
            T2Value vcValue = asymMixRecordToT2Value.apply(entry);
            for (int i = 0; i < sv.size(); i++) {
                int asmId = sv.getNodeAssemblyIndex(i);
                PartialValue perNodeVC = vcValue.getTestValue().sub(i);

                bndInts[asmId][0] += perNodeVC.get(0) * outNorm[0] * weight;
                bndInts[asmId][1] += perNodeVC.get(0) * outNorm[1] * weight;
                bndIntNoneVCs[asmId][0] += sv.get(i, 0) * outNorm[0] * weight;
                bndIntNoneVCs[asmId][1] += sv.get(i, 0) * outNorm[1] * weight;

            }
        };
        neumannRecords.forEach(bndInter);
        dirichletRecords.forEach(bndInter);
    }

    private void doIntegralOfQuadricResults() {
        volumeInts = new double[modelHub.getNodes().size()][QUADRIC_VC_BASES_SIZE];
        bndInts = new double[modelHub.getNodes().size()][QUADRIC_VC_BASES_SIZE];
        volumeIntNoneVCs = new double[modelHub.getNodes().size()][QUADRIC_VC_BASES_SIZE];
        bndIntNoneVCs = new double[modelHub.getNodes().size()][QUADRIC_VC_BASES_SIZE];

        AsymMixRecordToT2Value asymMixRecordToT2Value = processorContext.getBean(AsymMixRecordToT2Value.class);
        volumeRecords.forEach(entry -> {
            double weight = entry.getWeight();
            ShapeFunctionValue sv = entry.getShapeFunctionValue();
            double[] coord = entry.getGeomPoint().getCoord();
            double x = coord[0];
            double y = coord[1];
            T2Value vcValue = asymMixRecordToT2Value.apply(entry);
            for (int i = 0; i < sv.size(); i++) {
                int asmId = sv.getNodeAssemblyIndex(i);
                PartialValue perNodeVC = vcValue.getTestValue().sub(i);
                double[] volumeInt = volumeInts[asmId];
                double[] volumeIntNonVc = volumeIntNoneVCs[asmId];

                volumeInt[0] += perNodeVC.get(Pds2.U_x) * weight;
                volumeInt[1] += perNodeVC.get(Pds2.U_y) * weight;
                volumeInt[2] += (perNodeVC.get(0) + x * perNodeVC.get(Pds2.U_x)) * weight;
                volumeInt[3] += perNodeVC.get(Pds2.U_x) * y * weight;
                volumeInt[4] += perNodeVC.get(Pds2.U_y) * x * weight;
                volumeInt[5] += (perNodeVC.get(0) + y * perNodeVC.get(Pds2.U_y)) * weight;

                volumeIntNonVc[0] += sv.get(i, Pds2.U_x) * weight;
                volumeIntNonVc[1] += sv.get(i, Pds2.U_y) * weight;
                volumeIntNonVc[2] += sv.get(i, 0) + x * sv.get(i, Pds2.U_x) * weight;
                volumeIntNonVc[3] += sv.get(i, Pds2.U_x) * y * weight;
                volumeIntNonVc[4] += sv.get(i, Pds2.U_y) * x * weight;
                volumeIntNonVc[5] += sv.get(i, 0) + y * sv.get(i, Pds2.U_y) * weight;
            }
        });

        Consumer<? super IntegralMixRecordEntry> bndInter = entry -> {
            double weight = entry.getWeight();
            double[] coord = entry.getGeomPoint().getCoord();
            double x = coord[0], y = coord[1];
            ShapeFunctionValue sv = entry.getShapeFunctionValue();
            MFLine seg = (MFLine) entry.getGeomPoint().getGeomUnit();
            double[] outNorm = MFLine2DUtils.chordUnitOutNormal(seg, null);
            T2Value vcValue = asymMixRecordToT2Value.apply(entry);
            for (int i = 0; i < sv.size(); i++) {
                int asmId = sv.getNodeAssemblyIndex(i);
                PartialValue perNodeVC = vcValue.getTestValue().sub(i);
                double[] bndInt = bndInts[asmId];
                double[] bndIntNonVc = bndIntNoneVCs[asmId];
                final double nx = outNorm[0];
                final double ny = outNorm[1];
                bndInt[0] += perNodeVC.get(0) * nx * weight;
                bndInt[1] += perNodeVC.get(0) * ny * weight;
                bndInt[2] += perNodeVC.get(0) * x * nx * weight;
                bndInt[3] += perNodeVC.get(0) * y * nx * weight;
                bndInt[4] += perNodeVC.get(0) * x * ny * weight;
                bndInt[5] += perNodeVC.get(0) * y * ny * weight;

                bndIntNonVc[0] += sv.get(i, 0) * nx * weight;
                bndIntNonVc[1] += sv.get(i, 0) * ny * weight;
                bndIntNonVc[2] += sv.get(i, 0) * x * nx * weight;
                bndIntNonVc[3] += sv.get(i, 0) * y * nx * weight;
                bndIntNonVc[4] += sv.get(i, 0) * x * ny * weight;
                bndIntNonVc[5] += sv.get(i, 0) * y * ny * weight;
            }
        };
        neumannRecords.forEach(bndInter);
        dirichletRecords.forEach(bndInter);
    }

    private void doAsserts() {

        for (int asmIndex = 0; asmIndex < volumeInts.length; asmIndex++) {
            double[] vol = volumeInts[asmIndex];
            double[] bnd = bndInts[asmIndex];
            double[] volNonVc = volumeIntNoneVCs[asmIndex];
            double[] bndNonVc = bndIntNoneVCs[asmIndex];

            assertArrayEquals(vol, bnd, errorLimit);
            double distance = MathArrays.distance(vol, bnd);
            double distanceNonVc = MathArrays.distance(volNonVc, bndNonVc);
            assertTrue(distance < distanceNonVc);
        }
    }

    private void doVCIntegral() {
        integrateUnitsGroup = model.getIntegrateUnitsGroup();

        processorContext.getBean(VCIntegratorBaseConfig.VC_INTEGRATORS_GROUP_PROTO);
        @SuppressWarnings("unchecked")
        ArrayList<MFConsumerGroup<GeomQuadraturePoint>> vcIntegratorGroups = (ArrayList<MFConsumerGroup<GeomQuadraturePoint>>) processorContext
                .getBean(VCIntegratorBaseConfig.VC_INTEGRATORS_GROUPS);
        MFConsumerGroup<GeomQuadraturePoint> vcIntegratorsGroup = vcIntegratorGroups.get(0);

        ThreeStageIntegralCollection threeStage = processorContext.getBean(
                IntegralBaseConfig.INTEGRAL_COLLECTION_PROTO, ThreeStageIntegralCollection.class);

        Consumer<GeomQuadraturePoint> vcVolume = vcIntegratorsGroup.getVolume();
        integrateUnitsGroup.getVolume().forEach(
                oneStreamConsumer(threeStage.getUnitToGeomQuadraturePointsGroup().getVolume(), vcVolume));
        Consumer<GeomQuadraturePoint> vcNeumann = vcIntegratorsGroup.getNeumann();
        integrateUnitsGroup.getNeumann().forEach(
                oneStreamConsumer(threeStage.getUnitToGeomQuadraturePointsGroup().getNeumann(), vcNeumann));
        Consumer<GeomQuadraturePoint> vcDirichlet = vcIntegratorsGroup.getDirichlet();
        integrateUnitsGroup.getDirichlet().forEach(
                oneStreamConsumer(threeStage.getUnitToGeomQuadraturePointsGroup().getDirichlet(), vcDirichlet));

        CommonVCAssemblyIndexMap commonVCAssemblyIndexMap = processorContext.getBean(
                VCIntegratorBaseConfig.COMMON_VC_ASSEMBLY_INDEX_MAP, CommonVCAssemblyIndexMap.class);
        commonVCAssemblyIndexMap.solveVCNodes();

        for (int asmId = 0; asmId < modelHub.getNodes().size(); asmId++) {
            VCNode vcNode = commonVCAssemblyIndexMap.getVCNode(asmId);
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
        volumeRecords = volumeRecorder.gatherRecords();
        neumannRecords = neumannRecorder.gatherRecords();
        dirichletRecords = dirichletRecorder.gatherRecords();
    }

    private void initModel() {
        logger.debug(this.toString());

        modelHub = processorContext.getBean(CommonAnalysisModelHub.class);
        modelFactory = modelFactoryContext.getBean(PoissonPatchModelFactory2D.class);
        model = modelFactory.get();
        modelHub.setAnalysisModel(model);

        SearcherBaseHub searcherBaseHub = processorContext.getBean(SearcherBaseHub.class);
        searcherBaseHub.setNodes(modelHub.getNodes());
        searcherBaseHub.setBoundaries((Collection) modelHub.getBoundaries());
        searcherBaseHub.setSpatialDimension(2);
        searcherBaseHub.init();

        @SuppressWarnings("unchecked")
        WeakBus<Double> infRadBus = (WeakBus<Double>) processorContext
                .getBean(ConstantInfluenceConfig.CONSTANT_INFLUCENCE_RADIUS_BUS);

        infRadBus.post(influenceRadius);

        processorContext.getBean(InfluenceBaseConfig.INFLUENCE_PROCESSOR, Runnable.class).run();
        @SuppressWarnings("unchecked")
        WeakBus<Double> mixerRadiusBus = (WeakBus<Double>) processorContext.getBean(MixerConfig.MIXER_MAX_RADIUS_BUS);
        mixerRadiusBus.post(influenceRadius);

        @SuppressWarnings("unchecked")
        WeakBus<Integer> quadDegreeBus = (WeakBus<Integer>) processorContext
                .getBean(IntegralBaseConfig.QUADRATURE_DEGREE_BUS);
        quadDegreeBus.post(quadratureDegree);
    }

    private void initProcessContext() {
        processorContext = new AnnotationConfigApplicationContext();
        processorContext.register(ProcessConfigs.simpConfigClasses(PoissonVolumeAssemblerConfig.class,
                ConstantInfluenceConfig.class, TwoDSimpSearcherConfig.class, ThreeStageIntegralConfig.class).toArray(
                new Class<?>[0]));
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

        @Resource(name = ShapeFunctionBaseConfig.MONOMIAL_BASES_DEGREE_BUS)
        WeakBus<Integer> monomialDegreeBus;

        @Bean
        public Boolean phonySetMonomialBasesDegree() {
            monomialDegreeBus.postToFresh(1);
            return true;
        }
    }
}
