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
package net.epsilony.mf.opt.config;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.epsilony.mf.adapt.Fissionizer;
import net.epsilony.mf.adapt.TriangleCellFissionizer;
import net.epsilony.mf.implicit.contour.BisectionEdgeZeroPointSolver;
import net.epsilony.mf.implicit.contour.TriangleMarching;
import net.epsilony.mf.integrate.integrator.config.CommonToPointsIntegratorConfig;
import net.epsilony.mf.integrate.integrator.config.IntegralBaseConfig;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFCell;
import net.epsilony.mf.model.geom.SimpMFEdge;
import net.epsilony.mf.opt.InequalConstraintsCalculator;
import net.epsilony.mf.opt.ObjectCalculator;
import net.epsilony.mf.opt.PowerRangePenaltyFunction;
import net.epsilony.mf.opt.RepetitionBlockParametersConsumer;
import net.epsilony.mf.opt.integrate.LevelFunctionalIntegralUnitsGroup;
import net.epsilony.mf.opt.integrate.LevelPenaltyIntegrator;
import net.epsilony.mf.opt.integrate.TriangleMarchingIntegralUnitsFactory;
import net.epsilony.mf.opt.nlopt.NloptFuncWrapper;
import net.epsilony.mf.opt.nlopt.NloptMFuncWrapper;
import net.epsilony.mf.opt.nlopt.NloptMMADriver;
import net.epsilony.mf.process.mix.MFMixerFunctionPack;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.function.TypeMapFunction;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
@Import(CommonToPointsIntegratorConfig.class)
public class OptBaseConfig extends ApplicationContextAwareImpl {

    public static final String OPT_CONFIG_HUB = "optConfigHub";

    @Bean(name = OPT_CONFIG_HUB)
    public OptConfigHub optConfigHub() {
        return new OptConfigHub();
    }

    // only this bean is designed for inputing level function mixer packs from
    // outside
    public static final String LEVEL_MIXER_PACK_FACTORY_BUS = "levelMixerPackFactoryBus";

    @Bean(name = LEVEL_MIXER_PACK_FACTORY_BUS)
    public WeakBus<Supplier<? extends MFMixerFunctionPack>> levelMixerPackFactoryBus() {
        WeakBus<Supplier<? extends MFMixerFunctionPack>> result = new WeakBus<>(LEVEL_MIXER_PACK_FACTORY_BUS);
        result.register((obj, sup) -> {
            Supplier<MFMixerFunctionPack> parameterLinkSupplier = () -> {
                MFMixerFunctionPack pack = sup.get();
                levelParametersBus().register(MFMixerFunctionPack::setParameters, pack);
                return pack;
            };
            obj.postToEach(parameterLinkSupplier);
        }, levelMixerPackBus());
        return result;
    }

    public static final String LEVEL_MIXER_PACK_BUS = "levelMixerPackBus";

    @Bean(name = LEVEL_MIXER_PACK_BUS)
    public WeakBus<MFMixerFunctionPack> levelMixerPackBus() {
        return new WeakBus<>(LEVEL_MIXER_PACK_BUS);
    }

    public static final String LEVEL_PARAMETERS_BUS = "levelParametersBus";

    @Bean(name = LEVEL_PARAMETERS_BUS)
    public WeakBus<double[]> levelParametersBus() {
        return new WeakBus<>(LEVEL_PARAMETERS_BUS);
    }

    // must be posted
    public static final String INEQUAL_CONSTRAINTS_SIZE_BUS = "inequalConstraintsSizeBus";

    @Bean(name = INEQUAL_CONSTRAINTS_SIZE_BUS)
    public WeakBus<Integer> inequalConstraintsSizeBus() {
        return new WeakBus<>(INEQUAL_CONSTRAINTS_SIZE_BUS);
    }

    public static final String NLOPT_MMA_DRIVER = "nloptMMADriver";

    @Bean(name = NLOPT_MMA_DRIVER)
    public NloptMMADriver nloptMMADriver() {
        NloptMMADriver driver = new NloptMMADriver();
        driver.setObject(objectFunction());
        driver.setInequalConstraints(inequalConstraints());
        inequalConstraintsSizeBus().register(NloptMMADriver::setInequalConstraintsSize, driver);
        return driver;
    }

    @Bean
    public NloptFuncWrapper objectFunction() {
        NloptFuncWrapper result = new NloptFuncWrapper();

        ObjectCalculator objectCalculator = objectCalculator();
        result.setGradientSupplier(objectCalculator::gradient);
        result.setValueSupplier(objectCalculator::value);

        result.setParametersConsumer(pars -> {
            levelParametersBus().post(pars);
            objectCalculator.calculate();
        });

        return result;
    }

    @Bean
    public NloptMFuncWrapper inequalConstraints() {
        NloptMFuncWrapper result = new NloptMFuncWrapper();

        InequalConstraintsCalculator inequalConstraintsCalculator = inequalConstraintsCalculator();
        Consumer<double[]> consumer = pars -> {
            levelParametersBus().post(pars);
            inequalConstraintsCalculator.calculate();
        };
        RepetitionBlockParametersConsumer inequalConstraintsParametersConsumer = inequalConstraintsParametersConsumer();
        inequalConstraintsParametersConsumer.setInnerConsumer(consumer);
        result.setParametersConsumer(inequalConstraintsParametersConsumer);
        result.setGradientsSupplier(inequalConstraintsCalculator::gradients);
        result.setResultsSupplier(inequalConstraintsCalculator::values);
        return result;
    }

    @Bean
    public RepetitionBlockParametersConsumer inequalConstraintsParametersConsumer() {
        return new RepetitionBlockParametersConsumer();
    }

    public static final String OBJECT_CALCULATOR = "objectCalculator";

    @Bean(name = OBJECT_CALCULATOR)
    public ObjectCalculator objectCalculator() {
        ObjectCalculator result = new ObjectCalculator();
        result.setCommonUnitToPoints(getCommonToPoints());
        result.setIntegralUnitsGroup(domainIntegralUnitsGroup());
        levelMixerPackBus().register((obj, func) -> {
            func.setDiffOrder(1);
            obj.setLevelPackFunction(func::valuePack);
        }, result);
        return result;
    }

    public static final String INEQUAL_CONSTRAINTS_CALCULATOR = "inequalConstraintsCalculator";

    @Bean(name = INEQUAL_CONSTRAINTS_CALCULATOR)
    public InequalConstraintsCalculator inequalConstraintsCalculator() {
        InequalConstraintsCalculator result = new InequalConstraintsCalculator();
        result.setCommonUnitToPoints(getCommonToPoints());
        result.setDomainIntegralUnitsGroup(domainIntegralUnitsGroup());
        levelMixerPackBus().register((obj, func) -> {
            func.setDiffOrder(1);
            obj.setLevelPackFunction(func::valuePack);
        }, result);
        return result;
    }

    @Bean
    public LevelFunctionalIntegralUnitsGroup domainIntegralUnitsGroup() {
        TriangleMarchingIntegralUnitsFactory result = triangleMarchingIntegralUnitsFactory();
        return new LevelFunctionalIntegralUnitsGroup() {

            @Override
            public Stream<PolygonIntegrateUnit> volume() {
                return result.volumeUnits().stream();
            }

            @Override
            public Stream<MFLine> boundary() {
                return result.boundaryUnits().stream();
            }

            @Override
            public void prepare() {
                result.generateUnits();
            }
        };
    }

    @Bean
    public LevelPenaltyIntegrator levelPenalty() {
        LevelPenaltyIntegrator result = new LevelPenaltyIntegrator();
        result.setPenalty(penaltyFunction());
        levelParametersBus().register((obj, pars) -> {
            obj.setGradientSize(pars.length);
        }, result);
        return result;
    }

    @Bean
    public DoubleFunction<PartialValue> penaltyFunction() {
        return new PowerRangePenaltyFunction(1000, 3);
    }

    @Bean
    public TriangleMarchingIntegralUnitsFactory triangleMarchingIntegralUnitsFactory() {
        TriangleMarchingIntegralUnitsFactory result = new TriangleMarchingIntegralUnitsFactory();
        result.setTriangleMarching(triangleMarching());
        levelMixerPackBus().register((obj, func) -> {
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
        levelMixerPackBus().register((obj, func) -> {
            obj.setLevelFunction(func::value);
        }, result);
        return result;
    }

    @Bean
    public Function<MFEdge, double[]> zeroPointSolver() {
        BisectionEdgeZeroPointSolver solver = new BisectionEdgeZeroPointSolver();
        levelMixerPackBus().register((obj, func) -> {
            solver.setLevelFunction(func::value);
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

    @Bean(name = IntegralBaseConfig.QUADRATURE_DEGREE_BUS)
    public WeakBus<Integer> quadratureDegreeBus() {
        return new WeakBus<>(IntegralBaseConfig.QUADRATURE_DEGREE_BUS);
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
