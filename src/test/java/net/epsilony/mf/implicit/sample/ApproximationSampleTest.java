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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.implicit.assembler.config.ImplicitAssemblerConfig;
import net.epsilony.mf.implicit.config.ImplicitIntegratorConfig;
import net.epsilony.mf.implicit.level.CircleLvFunction;
import net.epsilony.mf.implicit.sample.RectangleApproximationModelFactory.ByNumRowsCols;
import net.epsilony.mf.integrate.integrator.config.CommonToPointsIntegratorConfig;
import net.epsilony.mf.integrate.integrator.config.MFConsumerGroup;
import net.epsilony.mf.integrate.integrator.config.IntegralBaseConfig;
import net.epsilony.mf.integrate.integrator.config.ThreeStageIntegralCollection;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.config.CommonAnalysisModelHubConfig;
import net.epsilony.mf.model.config.ModelBusConfig;
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
import net.epsilony.mf.util.matrix.MFMatrix;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ApproximationSampleTest {

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

        ToDoubleFunction<double[]> levelFunction = new CircleLvFunction(center, radius);

        double influenceRadius = 5.5;

        int quadratureDegree = 2;

        final double relativeError = 1e-14;

        RectangleApproximationModelFactory.ByNumRowsCols factory = new ByNumRowsCols(numNodeRowsCols, numNodeRowsCols,
                numQuadRowsCols, numQuadRowsCols);
        factory.setRectangle(rectangle);
        factory.setLevelFunction(levelFunction);
        AnalysisModel model = factory.get();

        AnnotationConfigApplicationContext processorContext = new AnnotationConfigApplicationContext();
        processorContext.register(ModelBusConfig.class, ImplicitAssemblerConfig.class, ImplicitIntegratorConfig.class,
                CenterPerturbSupportDomainSearcherConfig.class, CommonAnalysisModelHubConfig.class, MixerConfig.class,
                MLSConfig.class, TwoDSimpSearcherConfig.class, ConstantInfluenceConfig.class);
        processorContext.refresh();

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

        matrixHub.mergePosted();

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

        Mixer mixer = processorContext.getBean(Mixer.class);

        ArrayList<double[]> asmIdToValues = PostProcessors.collectArrayListArrayNodesValues(nodes);
        SimpPostProcessor simpPostProcessor = new SimpPostProcessor(asmIdToValues, 1, 2, mixer);

        @SuppressWarnings("unchecked")
        Function<Object, Collection<? extends GeomQuadraturePoint>> commonToPoints = (Function<Object, Collection<? extends GeomQuadraturePoint>>) processorContext
                .getBean(CommonToPointsIntegratorConfig.COMMON_UNIT_TO_POINTS_PROTO);

        final double[] levelIntegral = new double[1];
        final double[] approximateIntegral = new double[1];
        final boolean[] tested = new boolean[1];
        integrateUnitsGroup.getVolume().forEach(qu -> {
            commonToPoints.apply(qu).stream().forEach(qp -> {
                final double[] coord = qp.getGeomPoint().getCoord();
                double lv = levelFunction.applyAsDouble(coord);
                simpPostProcessor.setCenter(coord);
                double av = simpPostProcessor.value().get(0, 0);

                double weight = qp.getWeight();
                levelIntegral[0] += weight * lv;
                approximateIntegral[0] += weight * av;

                tested[0] = true;
            });
        });

        assertTrue(tested[0]);
        assertEquals(levelIntegral[0], approximateIntegral[0], abs(levelIntegral[0] * relativeError));

        processorContext.close();
    }
}
