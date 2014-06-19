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
package net.epsilony.mf.process.mix.bm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.influence.config.InfluenceBaseConfig;
import net.epsilony.mf.process.mix.MFMixer;
import net.epsilony.mf.process.mix.bm.config.BenchMarkBaseConfig;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.function.RectangleToGridCoords.ByNumRowsCols;
import net.epsilony.mf.util.math.ArrayPartialValue;
import net.epsilony.mf.util.math.PartialValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MixerBenchMarker {
    public static final int                  DEFAULT_TEST_COUNT = 100_000;
    public static final int                  DEFAULT_WARM_UP    = 1_000;
    public static final TimeUnit             DEFAULT_TIME_UNIT  = TimeUnit.MILLISECONDS;
    private ApplicationContext               applicationContext;
    private Consumer<ApplicationContext>     influenceProcessorTrigger;
    private Supplier<? extends MFMixerInput> inputSampleFactory;
    private int                              testCount          = DEFAULT_TEST_COUNT;
    private int                              warmUpCount        = DEFAULT_WARM_UP;
    private TimeUnit                         timeUnit           = DEFAULT_TIME_UNIT;
    private MFMixer                          mixer;
    private List<MFNode>                     allNodes;
    private List<MFNode>                     spaceNodes;
    private List<MFLine>                     boundaries;

    public static final Logger               logger             = LoggerFactory.getLogger(MixerBenchMarker.class);

    public MixerBenchMarker(ApplicationContext applicationContext,
            Consumer<ApplicationContext> influenceProcessorTrigger,
            Supplier<? extends MFMixerInput> inputSampleFactory, List<MFNode> allNodes, List<MFNode> spaceNodes,
            List<MFLine> boundaries) {
        this.applicationContext = applicationContext;
        this.influenceProcessorTrigger = influenceProcessorTrigger;
        this.inputSampleFactory = inputSampleFactory;
        this.allNodes = allNodes;
        this.spaceNodes = spaceNodes;
        this.boundaries = boundaries;
    }

    public MixerBenchMarker() {
    }

    /**
     * for generating influenceProcessorTrigger
     */
    public static void triggerConstantInfluenceProcessing(ApplicationContext applicationContext, double influenceRadius) {
        @SuppressWarnings("unchecked")
        WeakBus<Double> influenceRadiusBus = applicationContext.getBean(
                ConstantInfluenceConfig.CONSTANT_INFLUCENCE_RADIUS_BUS, WeakBus.class);
        influenceRadiusBus.post(influenceRadius);

        applicationContext.getBean(InfluenceBaseConfig.INFLUENCE_PROCESSOR, Runnable.class).run();

        @SuppressWarnings("unchecked")
        WeakBus<Double> mixerRadiusBus = applicationContext.getBean(MixerConfig.MIXER_MAX_RADIUS_BUS, WeakBus.class);
        mixerRadiusBus.post(influenceRadius);
    }

    public static MFMixerInput randomInput(double[] xRange, double[] yRange, Random random) {
        double tx = random.nextDouble();
        double ty = random.nextDouble();
        double[] center = { xRange[0] * (1 - tx) + xRange[1] * tx, yRange[0] * (1 - ty) + yRange[1] * ty };
        return new MFMixerInput(null, null, center);
    }

    public void run() {
        initMixer();
        PartialValue warmSum = run(warmUpCount, "warm up");
        PartialValue testSum = run(testCount, "main");
        logger.info("warm sum", warmSum);
        logger.info("test sum", testSum);
    }

    private void initMixer() {
        @SuppressWarnings("unchecked")
        WeakBus<Collection<MFNode>> allNodesBus = applicationContext.getBean(ModelBusConfig.NODES_BUS, WeakBus.class);
        allNodesBus.post(getAllNodes());

        @SuppressWarnings("unchecked")
        WeakBus<Collection<MFNode>> spaceNodesBus = applicationContext.getBean(ModelBusConfig.SPACE_NODES_BUS,
                WeakBus.class);
        spaceNodesBus.post(getSpaceNodes());

        @SuppressWarnings("unchecked")
        WeakBus<Collection<MFLine>> boundariesBus = applicationContext.getBean(ModelBusConfig.BOUNDARIES_BUS,
                WeakBus.class);
        boundariesBus.post(Collections.<MFLine> emptyList());

        @SuppressWarnings("unchecked")
        WeakBus<Integer> spatialDimensionBus = applicationContext.getBean(ModelBusConfig.SPATIAL_DIMENSION_BUS,
                WeakBus.class);
        spatialDimensionBus.post(2);

        @SuppressWarnings("unchecked")
        WeakBus<Object> modelInputedBus = applicationContext.getBean(ModelBusConfig.MODEL_INPUTED_BUS, WeakBus.class);
        modelInputedBus.post(true);

        influenceProcessorTrigger.accept(applicationContext);

        mixer = applicationContext.getBean(MixerConfig.MIXER_PROTO, MFMixer.class);
        mixer.setDiffOrder(1);
        mixer.setCenter(new double[] { 0.5, 0.5 });
        System.out.println("mixer.mix() = " + mixer.mix());

    }

    private ArrayPartialValue run(int count, String prefix) {
        long prepareInputsTime = System.nanoTime();
        ArrayList<MFMixerInput> inputs = new ArrayList<>(testCount);
        for (int i = 0; i < count; i++) {
            inputs.add(inputSampleFactory.get());
        }
        prepareInputsTime = System.nanoTime() - prepareInputsTime;
        logger.info("Prepare {} {} inputs for {} {}", count, prefix,
                timeUnit.convert(prepareInputsTime, TimeUnit.NANOSECONDS), timeUnit);

        long runTime = System.nanoTime();
        ArrayPartialValue sum = new ArrayPartialValue(2, mixer.getDiffOrder());
        for (MFMixerInput input : inputs) {
            sumMixResult(sum, input);
        }
        runTime = System.nanoTime() - runTime;
        logger.info("Run {} {} times for {} {}", count, prefix, timeUnit.convert(runTime, TimeUnit.NANOSECONDS),
                timeUnit);
        return sum;

    }

    private void sumMixResult(ArrayPartialValue sum, MFMixerInput input) {
        mixer.setBoundary(input.getBoundary());
        mixer.setCenter(input.getCenter());
        mixer.setUnitOutNormal(input.getUnitOutNormal());
        ShapeFunctionValue mix = mixer.mix();
        for (int i = 0; i < mix.size(); i++) {
            for (int j = 0; j < mix.partialSize(); j++) {
                sum.add(j, mix.get(i, j));
            }
        }
    }

    public void setInputSampleFactory(Supplier<? extends MFMixerInput> inputSampleFactory) {
        this.inputSampleFactory = inputSampleFactory;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public int getWarmUpCount() {
        return warmUpCount;
    }

    public void setWarmUpCount(int warmUpCount) {
        this.warmUpCount = warmUpCount;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public List<MFNode> getAllNodes() {
        return allNodes;
    }

    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public List<MFLine> getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(List<MFLine> boundaries) {
        this.boundaries = boundaries;
    }

    public void setAllNodes(List<MFNode> allNodes) {
        this.allNodes = allNodes;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    public void setInfluenceProcessorTrigger(Consumer<ApplicationContext> influenceProcessorTrigger) {
        this.influenceProcessorTrigger = influenceProcessorTrigger;
    }

    public static void main(String[] args) {
        int nodesRowColNum = 300;
        ApplicationContext ac = new AnnotationConfigApplicationContext(BenchMarkBaseConfig.class,
                ConstantInfluenceConfig.class);
        List<MFNode> allNodes = new ByNumRowsCols(nodesRowColNum, nodesRowColNum).apply(new MFRectangle(0, 1, 1, 0))
                .stream().flatMap(Collection::stream).map(MFNode::new).collect(Collectors.toList());
        List<MFNode> spaceNodes = allNodes;
        List<MFLine> boundaries = Collections.emptyList();
        final Random random = new Random();
        Supplier<MFMixerInput> inputFactory = () -> randomInput(new double[] { 0.3, 0.7 }, new double[] { 0.3, 0.7 },
                random);
        Consumer<ApplicationContext> influenceProcessTrigger = obj -> triggerConstantInfluenceProcessing(obj,
                1.0 / nodesRowColNum * 2);

        MixerBenchMarker mbm = new MixerBenchMarker(ac, influenceProcessTrigger, inputFactory, allNodes, spaceNodes,
                boundaries);
        mbm.setTestCount(10_000);
        mbm.run();
    }
}
