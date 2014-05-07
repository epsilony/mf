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
package net.epsilony.mf.implicit.sample;

import static org.apache.commons.math3.util.FastMath.abs;
import static org.apache.commons.math3.util.MathArrays.ebeAdd;
import static org.apache.commons.math3.util.MathArrays.scale;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import net.epsilony.mf.implicit.assembler.config.ImplicitAssemblerConfig;
import net.epsilony.mf.implicit.config.ImplicitIntegratorConfig;
import net.epsilony.mf.implicit.level.CircleLvFunction;
import net.epsilony.mf.implicit.sample.RectangleRangeInitalModelFactory.ByNumRowsCols;
import net.epsilony.mf.integrate.integrator.config.CommonToPointsIntegratorConfig;
import net.epsilony.mf.integrate.integrator.config.IntegralBaseConfig;
import net.epsilony.mf.integrate.integrator.config.MFConsumerGroup;
import net.epsilony.mf.integrate.integrator.config.ThreeStageIntegralCollection;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.integrate.unit.MFLineUnit;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.config.CommonAnalysisModelHubConfig;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.function.SingleLineFractionizer;
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.geom.util.MFFacetFactory;
import net.epsilony.mf.model.geom.util.MFLineChainFactory;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.influence.config.InfluenceBaseConfig;
import net.epsilony.mf.model.search.config.TwoDSimpSearcherConfig;
import net.epsilony.mf.model.support_domain.config.CenterPerturbSupportDomainSearcherConfig;
import net.epsilony.mf.process.assembler.matrix.MatrixHub;
import net.epsilony.mf.process.mix.Mixer;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.process.post.PostProcessors;
import net.epsilony.mf.process.post.SimpPostProcessor;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.config.MLSConfig;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.matrix.MFMatrix;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ApproximationSampleTest {

    private AnnotationConfigApplicationContext processorContext;
    private AnalysisModel model;
    private double influenceRadius;
    private int quadratureDegree;

    public static Logger logger = LoggerFactory.getLogger(ApproximationSampleTest.class);
    private ToDoubleFunction<double[]> levelFunction;

    @Test
    public void testCircleLevel() {
        int numNodeRowsCols = 11;
        int numQuadRowsCols = 10;
        double cx = 1, cy = -3;
        double[] center = { cx, cy };
        double radius = 10;
        double enlarge = 1.5;

        MFRectangle rectangle = new MFRectangle();
        double t = radius * enlarge;
        double[] drul = { cy - t, cx + t, cy + t, cx - t };
        rectangle.setDrul(drul);

        levelFunction = new CircleLvFunction(center, radius);

        influenceRadius = 5.5;

        quadratureDegree = 2;

        logger.info("[testCircleLevel]levelFunction: {}, quadratureDegree {}", levelFunction, quadratureDegree);

        final double relativeError = 1e-14;

        RectangleRangeInitalModelFactory.ByNumRowsCols factory = new ByNumRowsCols(rectangle, numNodeRowsCols,
                numNodeRowsCols, numQuadRowsCols, numQuadRowsCols);
        factory.setLevelFunction(levelFunction);
        model = factory.get();

        initProcessContext();

        process();

        assertVolumeError(relativeError);

        processorContext.close();
    }

    @Test
    public void testEmphaziseRectangleLeft() {
        MFRectangle range = new MFRectangle(1, 4, 3, 1);
        int times = 5;
        int numNodesRows = 3 * times;
        int numNodesCols = 4 * times;

        MFRectangle levelRectangle = new MFRectangle(1.5, 3.5, 2.5, 1.5);
        levelFunction = genLevelFunction(levelRectangle);

        RectangleRangeInitalModelFactory modelFactory = genModel(range, numNodesRows, numNodesCols);
        int emphLineNum = 3 * times;
        modelFactory.setEmphasizeLines(genLeftSideEmphasizeLines(levelRectangle, emphLineNum));
        modelFactory.setLevelFunction(levelFunction);

        influenceRadius = range.getWidth() / (numNodesCols - 1) * 2.2;

        quadratureDegree = 1;

        model = modelFactory.get();

        int[] quadratureDegrees = { 1, 2 };
        double[] midEmphPointErrors = { 3e-14, 2e-3 };
        double[] relativeVolumeErrors = { 3e-14, 4e-3 };
        double[] emphIntegralErrors = { 2e-16, 1e-16 };
        double[] emphAbsIntegralErrors = { 1e-14, 2e-3 };
        for (int i = 0; i < quadratureDegrees.length; i++) {
            quadratureDegree = quadratureDegrees[i];
            double midEmphPointError = midEmphPointErrors[i];
            double relativeVolumeError = relativeVolumeErrors[i];
            double emphIntegralError = emphIntegralErrors[i];
            double emphAbsIntegralError = emphAbsIntegralErrors[i];
            doTest(levelFunction, midEmphPointError, relativeVolumeError, emphIntegralError, emphAbsIntegralError);
        }
        processorContext.close();
    }

    public Function<Object, Collection<? extends GeomQuadraturePoint>> genCommonToPoints() {
        @SuppressWarnings("unchecked")
        Function<Object, Collection<? extends GeomQuadraturePoint>> commonToPoints = (Function<Object, Collection<? extends GeomQuadraturePoint>>) processorContext
                .getBean(CommonToPointsIntegratorConfig.COMMON_UNIT_TO_POINTS_PROTO);
        return commonToPoints;
    }

    public SimpPostProcessor genSimpPostProcessor() {
        Mixer mixer = processorContext.getBean(Mixer.class);

        CommonAnalysisModelHub modelHub = processorContext.getBean(CommonAnalysisModelHub.class);
        ArrayList<MFNode> nodes = modelHub.getNodes();
        ArrayList<double[]> asmIdToValues = PostProcessors.collectArrayListArrayNodesValues(nodes);
        SimpPostProcessor simpPostProcessor = new SimpPostProcessor(asmIdToValues, 1, 2, mixer);
        return simpPostProcessor;
    }

    private void process() {

        CommonAnalysisModelHub modelHub = processorContext.getBean(CommonAnalysisModelHub.class);

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
                .getBean(IntegralBaseConfig.QUADRATURE_DEGREE_BUS);
        quadDegreeBus.post(quadratureDegree);

        IntegrateUnitsGroup integrateUnitsGroup = model.getIntegrateUnitsGroup();

        MatrixHub matrixHub = processorContext.getBean(MatrixHub.class);
        matrixHub.post();
        ThreeStageIntegralCollection intCollection = processorContext.getBean(
                IntegralBaseConfig.INTEGRAL_COLLECTION_PROTO, ThreeStageIntegralCollection.class);
        MFConsumerGroup<Object> integratorsGroup = intCollection.asOneStageGroup();

        Consumer<Object> volume = integratorsGroup.getVolume();
        integrateUnitsGroup.getVolume().stream().forEach(volume);
        Consumer<Object> dirichlet = integratorsGroup.getDirichlet();
        if (null != dirichlet && integrateUnitsGroup.getDirichlet() != null) {
            integrateUnitsGroup.getDirichlet().forEach(dirichlet);
        }

        matrixHub.mergePosted();

        MFSolver solver = new RcmSolver();
        solver.setMainMatrix(matrixHub.getMergedMainMatrix());
        solver.setMainVector(matrixHub.getMergedMainVector());
        solver.solve();
        MFMatrix result = solver.getResult();

        ArrayList<MFNode> nodes = modelHub.getNodes();
        double minLagValue = Double.POSITIVE_INFINITY;
        double minAbsLagValue = Double.POSITIVE_INFINITY;
        for (MFNode nd : nodes) {
            int assemblyIndex = nd.getAssemblyIndex();
            double[] value = new double[] { result.get(assemblyIndex, 0) };
            nd.setValue(value);
            int lagIndex = nd.getLagrangeAssemblyIndex();
            if (lagIndex >= 0) {
                final double lagValue = result.get(lagIndex, 0);
                nd.setLagrangeValue(new double[] { lagValue });
                if (lagValue < minLagValue) {
                    minLagValue = lagValue;
                }
                double absLagValue = abs(lagValue);
                if (absLagValue < minAbsLagValue) {
                    minAbsLagValue = absLagValue;
                }
            }
        }
        logger.info("min node lag/abs_lag value:{} {}", minLagValue, minAbsLagValue);
    }

    public void initProcessContext() {
        processorContext = new AnnotationConfigApplicationContext();
        processorContext.register(ModelBusConfig.class, ImplicitAssemblerConfig.class, ImplicitIntegratorConfig.class,
                CenterPerturbSupportDomainSearcherConfig.class, CommonAnalysisModelHubConfig.class, MixerConfig.class,
                MLSConfig.class, TwoDSimpSearcherConfig.class, ConstantInfluenceConfig.class);
        processorContext.refresh();
    }

    public void doTest(ToDoubleFunction<double[]> levelFunction, double emphMidPointError, double relativeVolumeError,
            double emphIntegralError, double emphAbsIntegralError) {

        logger.info("[left side emph] quadDegree: {}", quadratureDegree);

        initProcessContext();

        process();

        assertEmphMidPoints(emphMidPointError);

        assertVolumeError(relativeVolumeError);

        assertEmphError(emphIntegralError, emphAbsIntegralError);
    }

    public void assertEmphError(double emphIntegralError, double emphAbsIntegralError) {
        SimpPostProcessor simpPostProcessor = genSimpPostProcessor();
        Function<Object, Collection<? extends GeomQuadraturePoint>> commonToPoints = genCommonToPoints();
        double emphIntegral = 0;
        double emphAbsIntegral = 0;
        boolean tested = false;
        List<Object> dirichletUnits = model.getIntegrateUnitsGroup().getDirichlet();
        for (Object qu : dirichletUnits) {
            for (GeomQuadraturePoint qp : commonToPoints.apply(qu)) {
                final double[] coord = qp.getGeomPoint().getCoord();
                double lv = levelFunction.applyAsDouble(coord);
                assertEquals(0, lv, 1e-14);
                simpPostProcessor.setCenter(coord);
                double av = simpPostProcessor.value().get(0, 0);
                emphIntegral += av * qp.getWeight();
                emphAbsIntegral += abs(av) * qp.getWeight();
                tested = true;
            }
        }

        logger.info("emphIntegral = {}", emphIntegral);
        logger.info("emphAbsIntegral = {}", emphAbsIntegral);
        assertTrue(tested);
        assertEquals(0, emphIntegral, emphIntegralError);
        assertEquals(0, emphAbsIntegral, emphAbsIntegralError);
    }

    public void assertVolumeError(double relativeVolumeError) {
        SimpPostProcessor simpPostProcessor = genSimpPostProcessor();
        Function<Object, Collection<? extends GeomQuadraturePoint>> commonToPoints = genCommonToPoints();
        double levelIntegral = 0;
        double approximateIntegral = 0;
        boolean tested = false;
        for (Object qu : model.getIntegrateUnitsGroup().getVolume()) {
            for (GeomQuadraturePoint qp : commonToPoints.apply(qu)) {
                final double[] coord = qp.getGeomPoint().getCoord();
                double lv = levelFunction.applyAsDouble(coord);
                simpPostProcessor.setCenter(coord);
                double av = simpPostProcessor.value().get(0, 0);
                double weight = qp.getWeight();
                levelIntegral += weight * lv;
                approximateIntegral += weight * av;
                tested = true;
            }
        }
        assertTrue(tested);
        logger.info("level function intergral: {}, approximate function intergral: {}, error {}", levelIntegral,
                approximateIntegral, abs(levelIntegral - approximateIntegral));
        assertEquals(levelIntegral, approximateIntegral, abs(levelIntegral * relativeVolumeError));
    }

    public void assertEmphMidPoints(double midPointError) {
        SimpPostProcessor simpPostProcessor = genSimpPostProcessor();
        List<Object> dirichletUnits = model.getIntegrateUnitsGroup().getDirichlet();
        double maxAbsActvalue = 0;
        for (Object obj : dirichletUnits) {
            MFLineUnit lineUnit = (MFLineUnit) obj;
            MFLine line = lineUnit.getLine();
            double[] mid = ebeAdd(line.getStartCoord(), line.getEndCoord());
            mid = scale(0.5, mid);
            simpPostProcessor.setCenter(mid);
            PartialTuple value = simpPostProcessor.value();
            final double actual = value.get(0, 0);
            if (abs(actual) > abs(maxAbsActvalue)) {
                maxAbsActvalue = actual;
            }
            assertEquals(0, actual, midPointError);
        }
        logger.info("max emphasize lines mid point error value: {}", maxAbsActvalue);
    }

    private List<MFLine> genLeftSideEmphasizeLines(MFRectangle levelRectangle, int emphLineNum) {
        double[] start = { levelRectangle.getLeft(), levelRectangle.getUp() };
        double[] end = { levelRectangle.getLeft(), levelRectangle.getDown() };

        MFLineChainFactory factory = new MFLineChainFactory(SimpMFLine::new, MFNode::new);
        List<double[]> newCoords = new SingleLineFractionizer.ByNumberOfNewCoords(emphLineNum - 1)
                .fraction(new double[][] { start, end });
        ArrayList<double[]> coords = new ArrayList<>(emphLineNum + 1);
        coords.add(start);
        coords.addAll(newCoords);
        coords.add(end);
        factory.setClosed(false);
        MFLine chainHead = factory.produce(coords);
        return chainHead.stream().filter(line -> line.getSucc() != null).collect(Collectors.toList());

    }

    public RectangleRangeInitalModelFactory genModel(MFRectangle range, int numNodesRows, int numNodesCols) {
        RectangleRangeInitalModelFactory modelFactory = new ByNumRowsCols(range, numNodesRows, numNodesCols,
                numNodesRows - 1, numNodesCols - 1);

        return modelFactory;
    }

    public ToDoubleFunction<double[]> genLevelFunction(MFRectangle levelRectangle) {
        final MFFacet facet = new MFFacetFactory(SimpMFLine::new, MFNode::new).produceBySingleChain(levelRectangle
                .vertesCoords());
        ToDoubleFunction<double[]> levelFunction = facet::distanceFunction;
        return levelFunction;
    }
}
