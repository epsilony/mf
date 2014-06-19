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
package net.epsilony.mf.opt.integrate.config;

import java.util.Collection;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import net.epsilony.mf.adapt.Fissionizer;
import net.epsilony.mf.adapt.TriangleCellFissionizer;
import net.epsilony.mf.implicit.contour.BisectionEdgeZeroPointSolver;
import net.epsilony.mf.implicit.contour.TriangleMarching;
import net.epsilony.mf.integrate.integrator.config.CommonToPointsIntegratorConfig;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.mf.model.geom.SimpMFCell;
import net.epsilony.mf.model.geom.SimpMFEdge;
import net.epsilony.mf.opt.PowerRangePenaltyFunction;
import net.epsilony.mf.opt.integrate.InequalConstraintsIntegralCalculator;
import net.epsilony.mf.opt.integrate.InequalConstraintsIntegralCalculator.FunctionsGroup;
import net.epsilony.mf.opt.integrate.LevelFunctionalIntegralUnitsGroup;
import net.epsilony.mf.opt.integrate.LevelPenaltyIntegrator;
import net.epsilony.mf.opt.integrate.NoRepetitatePreparationLvUnitsGroup;
import net.epsilony.mf.opt.integrate.ObjectIntegralCalculator;
import net.epsilony.mf.opt.integrate.TriangleMarchingIntegralUnitsFactory;
import net.epsilony.mf.process.mix.MFMixerFunctionPack;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.function.TypeMapFunction;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class OptIntegralConfig extends ApplicationContextAwareImpl {

    @Bean
    WeakBus<Map<String, Object>> prepareTriggerBus() {
        return new WeakBus<>("optIntegralPrepareTrigerBus");
    }

    @Bean
    WeakBus<MFMixerFunctionPack> levelMixerFunctionPackBus() {
        return new WeakBus<>("optIntegralLevelMixerFunctionPackBus");
    }

    @Bean
    WeakBus<double[]> objectParameterBus() {
        return new WeakBus<>("optIntegralParameterConsumer");
    }

    @Bean
    WeakBus<double[]> inequalConstraintsParameterBus() {
        return new WeakBus<>("optIntegralInequalConstraintsParameterBus");
    }

    @Bean
    public WeakBus<Integer> quadratureDegreeBus() {
        return new WeakBus<>("optIntegralQuadratureDegreeBus");
    }

    @Bean
    public OptIntegralHub optIntegralHub() {
        OptIntegralHub result = new OptIntegralHub();
        final ObjectIntegralCalculator objectIntegralCalculator = objectIntegralCalculator();
        result.setObjectCalculateTrigger((obj) -> {
            objectIntegralCalculator.calculatePrepare();
            objectIntegralCalculator.calculate();
        });
        result.setObjectValueSupplier(objectIntegralCalculator::value);
        result.setObjectGradientSupplier(objectIntegralCalculator::gradient);

        final InequalConstraintsIntegralCalculator inequalConstraintsCalculator = inequalConstraintsIntegralCalculator();
        FunctionsGroup functionsGroup = inequalConstraintsCalculator.new FunctionsGroup();
        result.setInequalConstraintsCalculateTrigger((obj) -> {
            inequalConstraintsCalculator.calculatePrepare();
            inequalConstraintsCalculator.calculate();
        });
        result.setInequalConstraintsValueSuppliersSupplier(functionsGroup::getInequalConstraintsValueSuppliers);
        result.setInequalConstraintsGradientSuppliersSupplier(functionsGroup::getInequalConstraintsGradientSuppliers);

        result.setQuadratureDegreeBus(quadratureDegreeBus());

        result.setObjectIntegratorConsumer(objectIntegralCalculator::setIntegrator);
        result.setInequalConstraintsRangeIntegratorsConsumer(inequalConstraintsCalculator::setRangeIntegrators);
        result.setInequalConstraintsDomainIntegratorsConsumer(inequalConstraintsCalculator::setDomainIntegrators);

        result.setObjectParameterConsumer(objectParameterBus()::post);
        result.setPrepareTriggerBus(prepareTriggerBus());
        result.setInequalConstraintsParameterConsumer(inequalConstraintsParameterBus()::post);
        result.setLevelMixerFunctionPackBus(levelMixerFunctionPackBus());
        result.setQuadratureDegreeBus(quadratureDegreeBus());

        return result;
    }

    public static final String OBJECT_INTEGRAL_CALCULATOR = "objectIntegralCalculator";

    @Bean(name = OBJECT_INTEGRAL_CALCULATOR)
    public ObjectIntegralCalculator objectIntegralCalculator() {
        ObjectIntegralCalculator result = new ObjectIntegralCalculator();
        result.setCommonUnitToPoints(getCommonToPoints());
        result.setIntegralUnitsGroup(domainIntegralUnitsGroup());
        levelMixerFunctionPackBus().register((obj, func) -> {
            objectParameterBus().register(MFMixerFunctionPack::setParameters, func);
            func.setDiffOrder(1);
            obj.setLevelPackFunction(func::valuePack);
        }, result);
        prepareTriggerBus().register((obj, map) -> {
            double[] start = (double[]) map.get("start");
            obj.setGradientSize(start.length);
        }, result);
        return result;
    }

    public static final String INEQUAL_CONSTRAINTS_INTEGRAL_CALCULATOR = "inequalConstraintsIntegralCalculator";

    @Bean(name = INEQUAL_CONSTRAINTS_INTEGRAL_CALCULATOR)
    public InequalConstraintsIntegralCalculator inequalConstraintsIntegralCalculator() {
        InequalConstraintsIntegralCalculator result = new InequalConstraintsIntegralCalculator();
        result.setCommonUnitToPoints(getCommonToPoints());
        result.setDomainIntegralUnitsGroup(domainIntegralUnitsGroup());
        levelMixerFunctionPackBus().register((obj, func) -> {
            inequalConstraintsParameterBus().register(MFMixerFunctionPack::setParameters, func);
            func.setDiffOrder(1);
            obj.setLevelPackFunction(func::valuePack);
        }, result);
        prepareTriggerBus().register((obj, map) -> {
            double[] start = (double[]) map.get("start");
            obj.setGradientSize(start.length);
        }, result);
        return result;
    }

    @Bean
    public LevelFunctionalIntegralUnitsGroup domainIntegralUnitsGroup() {
        NoRepetitatePreparationLvUnitsGroup result = new NoRepetitatePreparationLvUnitsGroup();
        result.setFactory(triangleMarchingIntegralUnitsFactory());
        prepareTriggerBus().register((obj, value) -> obj.resetLast(), result);
        objectParameterBus().register(NoRepetitatePreparationLvUnitsGroup::setParameters, result);
        inequalConstraintsParameterBus().register(NoRepetitatePreparationLvUnitsGroup::setParameters, result);
        return result;
    }

    @Bean
    public LevelPenaltyIntegrator levelPenalty() {
        LevelPenaltyIntegrator result = new LevelPenaltyIntegrator();
        result.setPenalty(penaltyFunction());
        return result;
    }

    @Bean
    public DoubleFunction<PartialValue> penaltyFunction() {
        return new PowerRangePenaltyFunction(1000, 3);
    }

    public static final String TRIANGLE_MARCHING_INTEGRAL_UNITS_FACTORY = "triangleMarchingIntegralUnitsFactory";

    @Bean(name = TRIANGLE_MARCHING_INTEGRAL_UNITS_FACTORY)
    public TriangleMarchingIntegralUnitsFactory triangleMarchingIntegralUnitsFactory() {
        TriangleMarchingIntegralUnitsFactory result = new TriangleMarchingIntegralUnitsFactory();
        result.setTriangleMarching(triangleMarching());
        levelMixerFunctionPackBus().register((obj, func) -> {
            objectParameterBus().register(MFMixerFunctionPack::setParameters, func);
            inequalConstraintsParameterBus().register(MFMixerFunctionPack::setParameters, func);
            obj.setLevelFunction(func::value);
        }, result);
        return result;
    }

    @Bean
    public TriangleMarching triangleMarching() {
        TriangleMarching result = new TriangleMarching();
        result.setEdgeFactory(SimpMFEdge::new);
        result.setNodeFactory(MFNode::new);
        result.setZeroPointSolver(zeroPointSolver());
        result.setFissionizer(fissionizer());
        levelMixerFunctionPackBus().register((obj, func) -> {
            objectParameterBus().register(MFMixerFunctionPack::setParameters, func);
            inequalConstraintsParameterBus().register(MFMixerFunctionPack::setParameters, func);
            obj.setLevelFunction(func::value);
        }, result);
        return result;
    }

    @Bean
    public Function<MFEdge, double[]> zeroPointSolver() {
        BisectionEdgeZeroPointSolver solver = new BisectionEdgeZeroPointSolver();
        levelMixerFunctionPackBus().register((obj, func) -> {
            objectParameterBus().register(MFMixerFunctionPack::setParameters, func);
            inequalConstraintsParameterBus().register(MFMixerFunctionPack::setParameters, func);
            obj.setLevelFunction(func::value);
        }, solver);

        return solver;
    }

    @Bean
    public Fissionizer fissionizer() {
        Fissionizer fissionizer = new Fissionizer();
        TriangleCellFissionizer triangleCellFissionizer = new TriangleCellFissionizer();
        triangleCellFissionizer.setCellFactory(() -> new SimpMFCell(3));
        triangleCellFissionizer.setEdgeFactory(SimpMFEdge::new);
        triangleCellFissionizer.setNodeFactory(MFNode::new);
        fissionizer.setCellFissionizer(triangleCellFissionizer);
        return fissionizer;
    }

    private Function<Object, Stream<GeomQuadraturePoint>> getCommonToPoints() {
        @SuppressWarnings("unchecked")
        TypeMapFunction<Object, Collection<GeomQuadraturePoint>> commonToPoints = (TypeMapFunction<Object, Collection<GeomQuadraturePoint>>) applicationContext
                .getBean(CommonToPointsIntegratorConfig.COMMON_UNIT_TO_POINTS_PROTO);
        Function<Object, Stream<GeomQuadraturePoint>> commonUnitToPointStream = commonToPoints
                .andThen(Collection::stream);
        return commonUnitToPointStream;
    }
}
