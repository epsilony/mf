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

import static net.epsilony.mf.util.function.FunctionConnectors.oneStreamConsumer;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.integrate.integrator.config.IntegralBaseConfig;
import net.epsilony.mf.integrate.integrator.config.MFConsumerGroup;
import net.epsilony.mf.integrate.integrator.config.MFFunctionGroup;
import net.epsilony.mf.integrate.integrator.config.ScniIntegralCollection;
import net.epsilony.mf.integrate.integrator.config.ScniIntegralConfig;
import net.epsilony.mf.integrate.integrator.config.ThreeStageIntegralCollection;
import net.epsilony.mf.integrate.integrator.config.ThreeStageIntegralConfig;
import net.epsilony.mf.integrate.integrator.vc.CommonVCAssemblyIndexMap;
import net.epsilony.mf.integrate.integrator.vc.HeavisideQuadricTransDomainBases2D;
import net.epsilony.mf.integrate.integrator.vc.HeavisideXYTransDomainBases2D;
import net.epsilony.mf.integrate.integrator.vc.IntegralMixRecordEntry;
import net.epsilony.mf.integrate.integrator.vc.MixRecordToAssemblyInput;
import net.epsilony.mf.integrate.integrator.vc.MixRecordToLagrangleAssemblyInput;
import net.epsilony.mf.integrate.integrator.vc.SimpIntegralMixRecorder;
import net.epsilony.mf.integrate.integrator.vc.VCNode;
import net.epsilony.mf.integrate.integrator.vc.config.LinearVCConfig;
import net.epsilony.mf.integrate.integrator.vc.config.QuadricVCConfig;
import net.epsilony.mf.integrate.integrator.vc.config.VCIntegratorBaseConfig;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.influence.config.InfluenceBaseConfig;
import net.epsilony.mf.model.sample.config.MechanicalLinearSampleConfig;
import net.epsilony.mf.model.sample.config.MechanicalQuadricSampleConfig;
import net.epsilony.mf.model.search.config.TwoDSimpSearcherConfig;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.assembler.config.MechanicalVolumeAssemblerConfig;
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
import net.epsilony.mf.util.MFUtils;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.math.ArrayPartialTuple;
import net.epsilony.mf.util.math.ArrayPartialTuple.SingleArray;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.parm.MFParmContainerPool;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MechanicalPatch2DTest {
    AnnotationConfigApplicationContext    processorContext;
    private ApplicationContext            modelFactoryContext;
    private double                        influenceRadius;
    private int                           quadratureDegree;
    public static Logger                  logger = LoggerFactory.getLogger(MechanicalPatch2DTest.class);
    private String                        prefix = "";

    double                                spaceErrorLimit;
    private double                        normErrorLimit;
    private double                        diriErrorLimit;
    private CommonAnalysisModelHub        modelHub;
    private AnalysisModel                 model;
    private MechanicalPatchModelFactory2D modelFactory;
    private MFMatrix                      result;
    private IntegrateUnitsGroup           integrateUnitsGroup;
    private MatrixHub                     matrixHub;
    private MFParmContainerPool           processorParmContainerPool;

    public void initApplicationContext() {
        processorContext = new AnnotationConfigApplicationContext();
        processorContext.register(ProcessConfigs.simpConfigClasses(MechanicalVolumeAssemblerConfig.class,
                ConstantInfluenceConfig.class, TwoDSimpSearcherConfig.class, ThreeStageIntegralConfig.class).toArray(
                new Class<?>[0]));
    }

    @Test
    public void testQuadric() {
        initApplicationContext();
        processorContext.refresh();
        modelFactoryContext = new AnnotationConfigApplicationContext(MechanicalQuadricSampleConfig.class);
        processorParmContainerPool = MFParmContainerPool.fromApplicationContext(processorContext);

        influenceRadius = 1;
        quadratureDegree = 4;

        spaceErrorLimit = 4e-4;
        normErrorLimit = 4e-4;
        diriErrorLimit = 5e-3;

        prefix = "quadric patch";
        doTest();
    }

    @Test
    public void testLinear() {
        initApplicationContext();
        processorContext.refresh();

        processorParmContainerPool = MFParmContainerPool.fromApplicationContext(processorContext);
        processorParmContainerPool.setOpenParm("monomialDegree", 1);

        modelFactoryContext = new AnnotationConfigApplicationContext(MechanicalLinearSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;
        spaceErrorLimit = 2e-3;
        normErrorLimit = 7e-4;
        diriErrorLimit = 7e-5;
        prefix = "linear patch";
        doTest();
    }

    @Test
    public void testLinearScni() {
        initApplicationContext();
        processorContext.register(ScniIntegralConfig.class);
        processorContext.refresh();

        processorParmContainerPool = MFParmContainerPool.fromApplicationContext(processorContext);
        processorParmContainerPool.setOpenParm("monomialDegree", 1);

        modelFactoryContext = new AnnotationConfigApplicationContext(MechanicalLinearSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;
        spaceErrorLimit = 2e-13;
        normErrorLimit = 6e-14;
        diriErrorLimit = 2e-13;
        prefix = "scni for linear patch";
        doTest();
    }

    @Test
    public void testQuadricScni() {
        initApplicationContext();
        processorContext.register(ScniIntegralConfig.class);
        processorContext.refresh();

        processorParmContainerPool = MFParmContainerPool.fromApplicationContext(processorContext);

        modelFactoryContext = new AnnotationConfigApplicationContext(MechanicalQuadricSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 4;

        spaceErrorLimit = 8e-2;
        normErrorLimit = 8e-2;
        diriErrorLimit = 7e-2;

        prefix = "scni for quadric patch";
        doTest();
    }

    @Test
    public void testQuadricVC() {
        initApplicationContext();
        processorContext.register(QuadricVCConfig.class);
        VCIntegratorBaseConfig.addVCBasesDefinition(processorContext, HeavisideQuadricTransDomainBases2D.class);
        processorContext.refresh();

        processorParmContainerPool = MFParmContainerPool.fromApplicationContext(processorContext);

        modelFactoryContext = new AnnotationConfigApplicationContext(MechanicalQuadricSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;
        spaceErrorLimit = 7e-14;
        normErrorLimit = 2e-13;
        diriErrorLimit = 9e-14;
        prefix = "linear vc patch";
        doVCTest();
    }

    @Test
    public void testLinearVC() {
        initApplicationContext();
        processorContext.register(LinearVCConfig.class);
        VCIntegratorBaseConfig.addVCBasesDefinition(processorContext, HeavisideXYTransDomainBases2D.class);
        processorContext.refresh();
        processorParmContainerPool = MFParmContainerPool.fromApplicationContext(processorContext);
        processorParmContainerPool.setOpenParm("monomialDegree", 1);
        modelFactoryContext = new AnnotationConfigApplicationContext(MechanicalLinearSampleConfig.class);
        influenceRadius = 1;
        quadratureDegree = 2;
        spaceErrorLimit = 7e-14;
        normErrorLimit = 2e-13;
        diriErrorLimit = 9e-13;
        prefix = "quadric vc patch";
        doVCTest();
    }

    private void doTest() {
        logger.debug(this.toString());

        initModel();
        process();
        solve();
        assertErrors();
    }

    private void assertErrors() {
        ArrayList<MFNode> nodes = modelHub.getNodes();
        int valueDimension = model.getValueDimension();
        nodes.stream().forEach((nd) -> {
            int assemblyIndex = nd.getAssemblyIndex();
            double[] value = new double[valueDimension];
            for (int i = 0; i < valueDimension; i++) {
                value[i] = result.get(assemblyIndex * valueDimension + i, 0);
            }
            nd.setValue(value);
        });

        ArrayList<MFNode> lagrangleDirichletNodes = modelHub.getLagrangleDirichletNodes();
        ArrayList<MFGeomUnit> dirichletBoundaries = modelHub.getDirichletBoundaries();
        @SuppressWarnings("unchecked")
        Function<double[], PartialTuple> field = modelFactoryContext.getBean("field", Function.class);
        System.out.println("lagrangleDirichletNodes = " + lagrangleDirichletNodes);
        Mixer mixer = processorContext.getBean(Mixer.class);

        ArrayList<double[]> asmIdToValues = PostProcessors.collectArrayListArrayNodesValues(nodes);
        SimpPostProcessor simpPostProcessor = new SimpPostProcessor(asmIdToValues, 2, 2, mixer);

        logger.debug("test :{}", prefix);

        dirichletBoundaries.forEach((geomUnit) -> {
            MFLine seg = (MFLine) geomUnit;
            MFNode nd = (MFNode) seg.getStart();
            PartialTuple fieldValue = field.apply(nd.getCoord());
            double[] exp = new double[] { fieldValue.get(0, 0), fieldValue.get(1, 0) };
            simpPostProcessor.setCenter(nd.getCoord());
            simpPostProcessor.setBoundary(seg);
            PartialTuple value = simpPostProcessor.value();
            double[] actValue = new double[] { value.get(0, 0), value.get(1, 0) };
            logger.debug("dirichlet: exp = {}, act = {}, error = {}, center = {}", exp, actValue,
                    MathArrays.ebeSubtract(exp, actValue), nd.getCoord());
            assertArrayEquals(exp, actValue, diriErrorLimit);
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
                double[] act = new double[] { value.get(0, 0), value.get(1, 0) };
                PartialTuple fieldValue = field.apply(center);
                double[] exp = new double[] { fieldValue.get(0, 0), fieldValue.get(1, 0) };
                logger.debug("space: exp = {}, act = {}, error={}, center = {}", exp, act,
                        MathArrays.ebeSubtract(exp, act), center);
                assertArrayEquals(exp, act, spaceErrorLimit);
            }
        }

        L2ErrorIntegrator errorIntegrator = new L2ErrorIntegrator();
        errorIntegrator.setActFunction(gp -> {
            simpPostProcessor.setCenter(gp.getCoord());
            simpPostProcessor.setBoundary(null);
            return simpPostProcessor.value();
        });

        SingleArray actValue = new ArrayPartialTuple.SingleArray(2, 2, 0);
        errorIntegrator.setExpFunction(gp -> {
            double[] data = actValue.getData();
            PartialTuple fv = field.apply(gp.getCoord());
            data[0] = fv.get(0, 0);
            data[1] = fv.get(1, 0);
            return actValue;
        });

        PolygonConsumer polygonConsumer = errorIntegrator.new PolygonConsumer();
        polygonConsumer.setDegree(3);

        @SuppressWarnings({ "rawtypes", "unchecked" })
        Consumer<Object> polygonConsumerRaw = (Consumer) polygonConsumer;
        integrateUnitsGroup.getVolume().forEach(polygonConsumerRaw);
        PartialTuple quadrature = errorIntegrator.getQuadrature();
        logger.debug("L2 norm = {},{}", quadrature.get(0, 0), quadrature.get(1, 0));
        assertEquals(0, quadrature.get(0, 0), normErrorLimit);
        assertEquals(0, quadrature.get(1, 0), normErrorLimit);
        logger.debug("end of {}\n\n\n", prefix);
    }

    private void process() {
        integrateUnitsGroup = model.getIntegrateUnitsGroup();

        matrixHub = processorContext.getBean(MatrixHub.class);
        matrixHub.post();
        Object collectionObject = processorContext.getBean(IntegralBaseConfig.INTEGRAL_COLLECTION_PROTO);
        MFConsumerGroup<Object> integratorsGroup;
        if (collectionObject instanceof ThreeStageIntegralCollection) {
            integratorsGroup = ((ThreeStageIntegralCollection) collectionObject).asOneStageGroup();
        } else if (collectionObject instanceof ScniIntegralCollection) {
            integratorsGroup = ((ScniIntegralCollection) collectionObject).asOneGroup();
        } else {
            throw new IllegalStateException();
        }
        Consumer<Object> volume = integratorsGroup.getVolume();
        integrateUnitsGroup.getVolume().stream().forEach(volume);
        Consumer<Object> neumann = integratorsGroup.getNeumann();
        integrateUnitsGroup.getNeumann().stream().forEach(neumann);
        Consumer<Object> dirichlet = integratorsGroup.getDirichlet();
        integrateUnitsGroup.getDirichlet().stream().forEach(dirichlet);

        matrixHub.mergePosted();

        MFSolver solver = new RcmSolver();
        solver.setMainMatrix(matrixHub.getMergedMainMatrix());
        solver.setMainVector(matrixHub.getMergedMainVector());
        solver.solve();
        result = solver.getResult();
    }

    private void initModel() {
        modelHub = processorContext.getBean(CommonAnalysisModelHub.class);
        modelFactory = modelFactoryContext.getBean(MechanicalPatchModelFactory2D.class);
        ConstitutiveLaw constitutiveLaw = modelFactoryContext.getBean(ConstitutiveLaw.class);
        modelHub.setConstitutiveLaw(constitutiveLaw);
        model = modelFactory.get();
        modelHub.setAnalysisModel(model);

        // SearcherBaseHub searcherBaseHub =
        // processorContext.getBean(SearcherBaseHub.class);
        // searcherBaseHub.setNodes(modelHub.getNodes());
        // searcherBaseHub.setBoundaries((Collection) modelHub.getBoundaries());
        // searcherBaseHub.setSpatialDimension(2);
        // searcherBaseHub.init();

        @SuppressWarnings("unchecked")
        WeakBus<Double> infRadBus = (WeakBus<Double>) processorContext
                .getBean(ConstantInfluenceConfig.CONSTANT_INFLUCENCE_RADIUS_BUS);

        infRadBus.post(influenceRadius);

        processorContext.getBean(InfluenceBaseConfig.INFLUENCE_PROCESSOR, Runnable.class).run();
        @SuppressWarnings("unchecked")
        WeakBus<Double> mixerRadiusBus = (WeakBus<Double>) processorContext.getBean(MixerConfig.MIXER_MAX_RADIUS_BUS);
        mixerRadiusBus.post(influenceRadius);

        processorParmContainerPool.setOpenParm("quadratureDegree", quadratureDegree);
        // @SuppressWarnings("unchecked")
        // WeakBus<Integer> quadDegreeBus = (WeakBus<Integer>) processorContext
        // .getBean(IntegralBaseConfig.QUADRATURE_DEGREE_BUS);
        // quadDegreeBus.post(quadratureDegree);
    }

    private void doVCTest() {
        initModel();
        vcProcess();
        solve();
        assertErrors();
    }

    private void vcProcess() {
        integrateUnitsGroup = model.getIntegrateUnitsGroup();
        @SuppressWarnings("unchecked")
        MFConsumerGroup<GeomQuadraturePoint> vcIntegratorsGroup = (MFConsumerGroup<GeomQuadraturePoint>) processorContext
                .getBean(VCIntegratorBaseConfig.VC_INTEGRATORS_GROUP_PROTO);

        ThreeStageIntegralCollection integralCollection = processorContext.getBean(
                IntegralBaseConfig.INTEGRAL_COLLECTION_PROTO, ThreeStageIntegralCollection.class);

        MFFunctionGroup<Object, Stream<GeomQuadraturePoint>> toPointsGroup = integralCollection
                .getUnitToGeomQuadraturePointsGroup();

        matrixHub = processorContext.getBean(MatrixHub.class);
        matrixHub.post();

        Consumer<GeomQuadraturePoint> vcVolume = vcIntegratorsGroup.getVolume();
        integrateUnitsGroup.getVolume().forEach(oneStreamConsumer(toPointsGroup.getVolume(), vcVolume));
        Consumer<GeomQuadraturePoint> vcNeumann = vcIntegratorsGroup.getNeumann();
        integrateUnitsGroup.getNeumann().forEach(oneStreamConsumer(toPointsGroup.getNeumann(), vcNeumann));
        Consumer<GeomQuadraturePoint> vcDirichlet = vcIntegratorsGroup.getDirichlet();
        integrateUnitsGroup.getDirichlet().forEach(oneStreamConsumer(toPointsGroup.getDirichlet(), vcDirichlet));
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

        MFConsumerGroup<AssemblyInput> asmGroup = integralCollection.getAssemblyGroup();
        MixRecordToAssemblyInput mixRecordToAssemblyInput = processorContext.getBean(
                VCIntegratorBaseConfig.ASYM_MIX_RECORD_TO_ASSEMBLY_INPUT_PROTO, MixRecordToAssemblyInput.class);
        MixRecordToLagrangleAssemblyInput mixRecordToLagrangleAssemblyInput = processorContext.getBean(
                VCIntegratorBaseConfig.ASYM_MIX_RECORD_TO_LAGRANGLE_ASSEMBLY_INPUT_PROTO,
                MixRecordToLagrangleAssemblyInput.class);
        volumeRecords.stream().map(mixRecordToAssemblyInput).forEach(asmGroup.getVolume());
        neumannRecords.stream().map(mixRecordToAssemblyInput).forEach(asmGroup.getNeumann());
        dirichletRecords.stream().map(mixRecordToLagrangleAssemblyInput).forEach(asmGroup.getDirichlet());
    }

    private void solve() {
        matrixHub.mergePosted();
        System.out.println("matrixHub.getMergedMainVector() = " + matrixHub.getMergedMainVector());
        System.out.println(model.getSpaceNodes().size());
        ArrayList<MFLine> segs = Lists.newArrayList((MFFacet) model.getBoundaryRoot());
        System.out.println(segs);
        System.out.println(segs.size());

        MFSolver solver = new RcmSolver();
        solver.setMainMatrix(matrixHub.getMergedMainMatrix());
        solver.setMainVector(matrixHub.getMergedMainVector());
        solver.solve();
        result = solver.getResult();
    }

    @Override
    public String toString() {
        return "PoissonPatch2DTest [influenceRadius=" + influenceRadius + ", quadratureDegree=" + quadratureDegree
                + "]";
    }
}
