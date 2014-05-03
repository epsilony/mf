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
package net.epsilony.mf.model.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Resource;

import net.epsilony.mf.integrate.integrator.config.CommonToPointsIntegratorConfig;
import net.epsilony.mf.integrate.integrator.config.IntegratorBaseConfig;
import net.epsilony.mf.integrate.integrator.config.IntegratorsGroup;
import net.epsilony.mf.integrate.integrator.config.ScniTriggerConfig;
import net.epsilony.mf.integrate.integrator.vc.CommonVCAssemblyIndexMap;
import net.epsilony.mf.integrate.integrator.vc.HeavisideQuadricTransDomainBases2D;
import net.epsilony.mf.integrate.integrator.vc.HeavisideXYTransDomainBases2D;
import net.epsilony.mf.integrate.integrator.vc.IntegralMixRecordEntry;
import net.epsilony.mf.integrate.integrator.vc.SimpIntegralMixRecorder;
import net.epsilony.mf.integrate.integrator.vc.VCNode;
import net.epsilony.mf.integrate.integrator.vc.config.LinearVCConfig;
import net.epsilony.mf.integrate.integrator.vc.config.QuadricVCConfig;
import net.epsilony.mf.integrate.integrator.vc.config.VCIntegratorBaseConfig;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.influence.config.InfluenceBaseConfig;
import net.epsilony.mf.model.sample.config.PoissonLinearSampleConfig;
import net.epsilony.mf.model.sample.config.PoissonQuadricSampleConfig;
import net.epsilony.mf.model.search.config.TwoDLRTreeSearcherConfig;
import net.epsilony.mf.process.assembler.config.PoissonVolumeAssemblerConfig;
import net.epsilony.mf.process.assembler.matrix.MatrixHub;
import net.epsilony.mf.process.config.ProcessConfigs;
import net.epsilony.mf.process.mix.Mixer;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.process.post.L2ErrorIntegrator;
import net.epsilony.mf.process.post.L2ErrorIntegrator.PolygonConsumer;
import net.epsilony.mf.process.post.PostProcessors;
import net.epsilony.mf.process.post.SimpPostProcessor;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.config.ShapeFunctionBaseConfig;
import net.epsilony.mf.util.MFUtils;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.math.ArrayPartialTuple;
import net.epsilony.mf.util.math.ArrayPartialTuple.SingleArray;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.matrix.MFMatrix;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class PoissonPatch2DTest {

    AnnotationConfigApplicationContext processorContext;
    private ApplicationContext modelFactoryContext;
    private double influenceRadius;
    private int quadratureDegree;
    public static Logger logger = LoggerFactory.getLogger(PoissonPatch2DTest.class);
    private String prefix = "";
    private double diriErrorLimit;
    private double normErrorLimit;
    private double spaceErrorLimit;
    private AnalysisModel model;
    private PatchModelFactory2D modelFactory;
    private CommonAnalysisModelHub modelHub;
    private IntegrateUnitsGroup integrateUnitsGroup;
    private MatrixHub matrixHub;
    private MFMatrix result;

    public void initApplicationContext() {
        processorContext = new AnnotationConfigApplicationContext();
        processorContext.register(ProcessConfigs.simpConfigClasses(PoissonVolumeAssemblerConfig.class,
                ConstantInfluenceConfig.class, TwoDLRTreeSearcherConfig.class).toArray(new Class<?>[0]));
    }

    @Test
    public void testQuadric() {
        initApplicationContext();
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonQuadricSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;

        spaceErrorLimit = 6e-3;
        normErrorLimit = 3e-3;
        diriErrorLimit = 2e-3;

        prefix = "quadric";
        doTest();
    }

    @Test
    public void testLinear() {
        initApplicationContext();
        processorContext.register(LinearBasesConfig.class);
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonLinearSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;

        spaceErrorLimit = 2e-3;
        normErrorLimit = 1e-3;
        diriErrorLimit = 2e-4;

        prefix = "linear patch";
        doTest();
    }

    @Test
    public void testLinearScni() {
        initApplicationContext();
        processorContext.register(ScniTriggerConfig.class, LinearBasesConfig.class);
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonLinearSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;

        spaceErrorLimit = 5e-14;
        normErrorLimit = 3e-14;
        diriErrorLimit = 4e-15;

        prefix = "linear scni";
        doTest();
    }

    @Test
    public void testQuadricScni() {
        initApplicationContext();
        processorContext.register(ScniTriggerConfig.class);
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonQuadricSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 3;

        spaceErrorLimit = 7e-2;
        normErrorLimit = 7e-2;
        diriErrorLimit = 1e-2;

        prefix = "quadric";
        doTest();
    }

    @Test
    public void testLinearVC() {
        initApplicationContext();
        processorContext.register(LinearVCConfig.class, LinearBasesConfig.class);
        VCIntegratorBaseConfig.addVCBasesDefinition(processorContext, HeavisideXYTransDomainBases2D.class);
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonLinearSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;

        spaceErrorLimit = 5e-15;
        normErrorLimit = 5e-15;
        diriErrorLimit = 8e-15;

        prefix = "linear vc";
        doVCTest();
    }

    @Test
    public void testQuadricVC() {
        initApplicationContext();
        processorContext.register(QuadricVCConfig.class);
        VCIntegratorBaseConfig.addVCBasesDefinition(processorContext, HeavisideQuadricTransDomainBases2D.class);
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonQuadricSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;

        spaceErrorLimit = 1e-12;
        normErrorLimit = 8e-13;
        diriErrorLimit = 2e-13;

        prefix = "quadric vc";
        doVCTest();
    }

    @Test
    public void testLinearVCForQuadric() {
        initApplicationContext();
        processorContext.register(LinearVCConfig.class);
        VCIntegratorBaseConfig.addVCBasesDefinition(processorContext, HeavisideXYTransDomainBases2D.class);
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(PoissonQuadricSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;

        spaceErrorLimit = 2e-2;
        normErrorLimit = 9e-3;
        diriErrorLimit = 2e-3;

        prefix = "linear vc for quadric";
        doVCTest();
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

    private void doTest() {
        initModelAndIntegrateUnits();

        process();

        solve();

        assertErrors();
    }

    private void assertErrors() {
        ArrayList<MFNode> nodes = modelHub.getNodes();
        nodes.stream().forEach((nd) -> {
            int assemblyIndex = nd.getAssemblyIndex();
            double[] value = new double[] { result.get(assemblyIndex, 0) };
            nd.setValue(value);
        });

        ArrayList<MFNode> lagrangleDirichletNodes = modelHub.getLagrangleDirichletNodes();
        ArrayList<MFGeomUnit> dirichletBoundaries = modelHub.getDirichletBoundaries();
        @SuppressWarnings("unchecked")
        Function<double[], PartialTuple> field = modelFactoryContext.getBean("field", Function.class);
        System.out.println("lagrangleDirichletNodes = " + lagrangleDirichletNodes);
        Mixer mixer = processorContext.getBean(Mixer.class);

        ArrayList<double[]> asmIdToValues = PostProcessors.collectArrayListArrayNodesValues(nodes);
        SimpPostProcessor simpPostProcessor = new SimpPostProcessor(asmIdToValues, 1, 2, mixer);

        logger.debug("test :{}", prefix);

        dirichletBoundaries.forEach((geomUnit) -> {
            MFLine seg = (MFLine) geomUnit;
            MFNode nd = (MFNode) seg.getStart();
            double exp = field.apply(nd.getCoord()).get(0, 0);
            simpPostProcessor.setCenter(nd.getCoord());
            simpPostProcessor.setBoundary(seg);
            PartialTuple value = simpPostProcessor.value();
            double actValue = value.get(0, 0);
            logger.debug("dirichlet: exp = {}, act = {}, error = {}, center = {}", exp, actValue, exp - actValue,
                    nd.getCoord());
            assertEquals(exp, actValue, diriErrorLimit);
        });

        double margin = 0.1;
        MFRectangle rectangle = modelFactory.getRectangle();
        double[] xs = MFUtils.linSpace(rectangle.getLeft() + margin, rectangle.getRight() - margin, 3);
        double[] ys = MFUtils.linSpace(rectangle.getDown() + margin, rectangle.getUp() - margin, 3);
        for (double x : xs) {
            for (double y : ys) {
                simpPostProcessor.setBoundary(null);
                double[] center = new double[] { x, y };
                simpPostProcessor.setCenter(center);
                PartialTuple value = simpPostProcessor.value();
                double act = value.get(0, 0);
                double exp = field.apply(center).get(0, 0);
                logger.debug("space: exp = {}, act = {}, error={}, center = {}", exp, act, exp - act, center);
                assertEquals(exp, act, spaceErrorLimit);
            }
        }

        L2ErrorIntegrator errorIntegrator = new L2ErrorIntegrator();
        errorIntegrator.setActFunction(gp -> {
            simpPostProcessor.setCenter(gp.getCoord());
            simpPostProcessor.setBoundary(null);
            return simpPostProcessor.value();
        });

        SingleArray actValue = new ArrayPartialTuple.SingleArray(1, 2, 0);
        errorIntegrator.setExpFunction(gp -> {
            double[] data = actValue.getData();
            data[0] = field.apply(gp.getCoord()).get(0, 0);
            return actValue;
        });

        PolygonConsumer polygonConsumer = errorIntegrator.new PolygonConsumer();
        polygonConsumer.setDegree(3);

        @SuppressWarnings({ "rawtypes", "unchecked" })
        Consumer<Object> polygonConsumerRaw = (Consumer) polygonConsumer;
        integrateUnitsGroup.getVolume().forEach(polygonConsumerRaw);
        PartialTuple quadrature = errorIntegrator.getQuadrature();
        logger.debug("L2 norm = {}", quadrature.get(0, 0));
        assertEquals(0, quadrature.get(0, 0), normErrorLimit);
        logger.debug("end of {}", this);
        logger.debug("end of {}\n\n", prefix);
    }

    private void process() {

        processorContext.getBean(IntegratorBaseConfig.INTEGRATORS_GROUP_PROTO);
        @SuppressWarnings("unchecked")
        List<IntegratorsGroup> integratorsGroups = (List<IntegratorsGroup>) processorContext
                .getBean(IntegratorBaseConfig.INTEGRATORS_GROUPS);
        IntegratorsGroup integratorsGroup = integratorsGroups.get(0);
        @SuppressWarnings("unchecked")
        Consumer<Object> volume = (Consumer<Object>) integratorsGroup.getVolume();
        integrateUnitsGroup.getVolume().stream().forEach(volume);
        @SuppressWarnings("unchecked")
        Consumer<Object> neumann = (Consumer<Object>) integratorsGroup.getNeumann();
        integrateUnitsGroup.getNeumann().stream().forEach(neumann);
        @SuppressWarnings("unchecked")
        Consumer<Object> dirichlet = (Consumer<Object>) integratorsGroup.getDirichlet();
        integrateUnitsGroup.getDirichlet().stream().forEach(dirichlet);
    }

    private void solve() {
        matrixHub.mergePosted();

        MFSolver solver = new RcmSolver();
        solver.setMainMatrix(matrixHub.getMergedMainMatrix());
        solver.setMainVector(matrixHub.getMergedMainVector());
        solver.solve();
        result = solver.getResult();
    }

    private void doVCTest() {
        initModelAndIntegrateUnits();

        vcProcess();

        solve();

        assertErrors();
    }

    private void vcProcess() {
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
        ArrayList<IntegralMixRecordEntry> volumeRecords = volumeRecorder.gatherRecords();
        ArrayList<IntegralMixRecordEntry> neumannRecords = neumannRecorder.gatherRecords();
        ArrayList<IntegralMixRecordEntry> dirichletRecords = dirichletRecorder.gatherRecords();

        processorContext.getBean(IntegratorBaseConfig.INTEGRATORS_GROUP_PROTO);
        @SuppressWarnings("unchecked")
        List<IntegratorsGroup> integratorsGroups = (List<IntegratorsGroup>) processorContext
                .getBean(IntegratorBaseConfig.INTEGRATORS_GROUPS);
        IntegratorsGroup integratorsGroup = integratorsGroups.get(0);
        @SuppressWarnings("unchecked")
        Consumer<Object> volume = (Consumer<Object>) integratorsGroup.getVolume();
        volumeRecords.forEach(volume);
        @SuppressWarnings("unchecked")
        Consumer<Object> neumann = (Consumer<Object>) integratorsGroup.getNeumann();
        neumannRecords.forEach(neumann);
        @SuppressWarnings("unchecked")
        Consumer<Object> dirichlet = (Consumer<Object>) integratorsGroup.getDirichlet();
        dirichletRecords.forEach(dirichlet);
    }

    private void initModelAndIntegrateUnits() {
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

        @SuppressWarnings("unchecked")
        WeakBus<Integer> quadDegreeBus = (WeakBus<Integer>) processorContext
                .getBean(CommonToPointsIntegratorConfig.QUADRATURE_DEGREE_BUS);
        quadDegreeBus.post(quadratureDegree);
        integrateUnitsGroup = model.getIntegrateUnitsGroup();

        matrixHub = processorContext.getBean(MatrixHub.class);
        matrixHub.post();
    }

    @Override
    public String toString() {
        return "PoissonPatch2DTest [influenceRadius=" + influenceRadius + ", quadratureDegree=" + quadratureDegree
                + ", prefix=" + prefix + ", diriErrorLimit=" + diriErrorLimit + ", normErrorLimit=" + normErrorLimit
                + ", spaceErrorLimit=" + spaceErrorLimit + ", model=" + model + ", modelFactory=" + modelFactory
                + ", modelHub=" + modelHub + ", integrateUnitsGroup=" + integrateUnitsGroup + ", matrixHub="
                + matrixHub + "]";
    }

}
