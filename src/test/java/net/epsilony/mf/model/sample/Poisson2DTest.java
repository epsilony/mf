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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import net.epsilony.mf.integrate.integrator.config.IntegratorBaseConfig;
import net.epsilony.mf.integrate.integrator.config.IntegratorsGroup;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.influence.config.InfluenceBaseConfig;
import net.epsilony.mf.model.sample.config.PoissonLinearSampleConfig;
import net.epsilony.mf.model.sample.config.PoissonQuadricSampleConfig;
import net.epsilony.mf.model.search.config.TwoDLRTreeSearcherConfig;
import net.epsilony.mf.process.Mixer;
import net.epsilony.mf.process.assembler.config.PoissonVolumeAssemblerConfig;
import net.epsilony.mf.process.assembler.matrix.MatrixHub;
import net.epsilony.mf.process.config.MixerConfig;
import net.epsilony.mf.process.config.ProcessConfigs;
import net.epsilony.mf.process.post.L2ErrorIntegrator;
import net.epsilony.mf.process.post.L2ErrorIntegrator.PolygonConsumer;
import net.epsilony.mf.process.post.PostProcessors;
import net.epsilony.mf.process.post.SimpPostProcessor;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.config.ShapeFunctionBaseConfig;
import net.epsilony.mf.util.MFUtils;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.math.ArrayPartialValueTuple;
import net.epsilony.mf.util.math.ArrayPartialValueTuple.SingleArray;
import net.epsilony.mf.util.math.PartialValueTuple;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.common_func.BasesFunction;
import net.epsilony.tb.common_func.MonomialBases2D;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Segment;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class Poisson2DTest {

    AnnotationConfigApplicationContext processorContext;
    private ApplicationContext modelFactoryContext;
    private double influenceRadius;
    private int quadratureDegree;
    public static Logger logger = LoggerFactory.getLogger(Poisson2DTest.class);
    private String prefix = "";
    private double diriErrorLimit;
    private double normErrorLimit;
    private double spaceErrorLimit;

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
        normErrorLimit = 8e-4;
        diriErrorLimit = 9e-5;

        prefix = "linear patch";
        doTest();
    }

    @Configuration
    public static class LinearBasesConfig {

        @Bean(name = ShapeFunctionBaseConfig.BASES_FUNCTION_PROTO)
        @Scope("prototype")
        public BasesFunction basesFunction() {
            return new MonomialBases2D(1);
        }
    }

    private void doTest() {
        logger.debug(this.toString());

        CommonAnalysisModelHub modelHub = processorContext.getBean(CommonAnalysisModelHub.class);
        PatchModelFactory2D modelFactory = modelFactoryContext.getBean(PoissonPatchModelFactory2D.class);
        AnalysisModel model = modelFactory.get();
        modelHub.setAnalysisModel(model);

        @SuppressWarnings("unchecked")
        WeakBus<Double> infRadBus = (WeakBus<Double>) processorContext
                .getBean(ConstantInfluenceConfig.CONSTANT_INFLUCENCE_RADIUS_BUS);

        infRadBus.post(influenceRadius);

        processorContext.getBean(InfluenceBaseConfig.INFLUENCE_PROCESSOR, Runnable.class).run();
        @SuppressWarnings("unchecked")
        WeakBus<Double> mixerRadiusBus = (WeakBus<Double>) processorContext.getBean(MixerConfig.MIXER_MAX_RADIUS_BUS);
        mixerRadiusBus.post(influenceRadius);

        MatrixHub matrixHub = processorContext.getBean(MatrixHub.class);
        matrixHub.post();

        @SuppressWarnings("unchecked")
        WeakBus<Integer> quadDegreeBus = (WeakBus<Integer>) processorContext
                .getBean(IntegratorBaseConfig.QUADRATURE_DEGREE_BUS);
        quadDegreeBus.post(quadratureDegree);
        IntegrateUnitsGroup integrateUnitsGroup = model.getIntegrateUnitsGroup();
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

        matrixHub.mergePosted();
        System.out.println("matrixHub.getMergedMainVector() = " + matrixHub.getMergedMainVector());
        System.out.println(model.getSpaceNodes().size());
        ArrayList<Segment> segs = Lists.newArrayList((Facet) model.getGeomRoot());
        System.out.println(segs);
        System.out.println(segs.size());

        MFSolver solver = new RcmSolver();
        solver.setMainMatrix(matrixHub.getMergedMainMatrix());
        solver.setMainVector(matrixHub.getMergedMainVector());
        solver.solve();
        MFMatrix result = solver.getResult();

        ArrayList<MFNode> nodes = modelHub.getNodes();
        nodes.stream().forEach((nd) -> {
            int assemblyIndex = nd.getAssemblyIndex();
            double[] value = new double[] { result.get(assemblyIndex, 0) };
            nd.setValue(value);
        });

        ArrayList<MFNode> lagrangleDirichletNodes = modelHub.getLagrangleDirichletNodes();
        ArrayList<GeomUnit> dirichletBoundaries = modelHub.getDirichletBoundaries();
        @SuppressWarnings("unchecked")
        Function<double[], PartialValueTuple> field = modelFactoryContext.getBean("field", Function.class);
        System.out.println("lagrangleDirichletNodes = " + lagrangleDirichletNodes);
        Mixer mixer = processorContext.getBean(Mixer.class);

        ArrayList<double[]> asmIdToValues = PostProcessors.collectArrayListArrayNodesValues(nodes);
        SimpPostProcessor simpPostProcessor = new SimpPostProcessor(asmIdToValues, 1, 2, mixer);

        logger.debug("test :{}", prefix);

        dirichletBoundaries.forEach((geomUnit) -> {
            Segment seg = (Segment) geomUnit;
            MFNode nd = (MFNode) seg.getStart();
            double exp = field.apply(nd.getCoord()).get(0, 0);
            simpPostProcessor.setCenter(nd.getCoord());
            simpPostProcessor.setBoundary(seg);
            PartialValueTuple value = simpPostProcessor.value();
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
                PartialValueTuple value = simpPostProcessor.value();
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

        SingleArray actValue = new ArrayPartialValueTuple.SingleArray(1, 2, 0);
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
        PartialValueTuple quadrature = errorIntegrator.getQuadrature();
        logger.debug("L2 norm = {}", quadrature.get(0, 0));
        assertEquals(0, quadrature.get(0, 0), normErrorLimit);
    }

    @Override
    public String toString() {
        return "Poisson2DTest [influenceRadius=" + influenceRadius + ", quadratureDegree=" + quadratureDegree + "]";
    }

}
