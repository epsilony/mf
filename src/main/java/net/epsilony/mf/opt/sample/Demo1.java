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
package net.epsilony.mf.opt.sample;

import static org.apache.commons.math3.util.FastMath.PI;
import static org.apache.commons.math3.util.MathArrays.distance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import javax.annotation.Resource;

import net.epsilony.mf.implicit.assembler.config.ImplicitAssemblerConfig;
import net.epsilony.mf.implicit.config.ImplicitIntegratorConfig;
import net.epsilony.mf.implicit.sample.SimpInitialModelProcessor;
import net.epsilony.mf.integrate.integrator.config.CommonToPointsIntegratorConfig;
import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.config.CommonAnalysisModelHubConfig;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.search.config.TwoDSimpSearcherConfig;
import net.epsilony.mf.model.support_domain.config.CenterPerturbSupportDomainSearcherConfig;
import net.epsilony.mf.opt.LevelOptModel;
import net.epsilony.mf.opt.config.OptPersistBaseConfig;
import net.epsilony.mf.opt.integrate.CoreShiftRangeFunctionalIntegrator;
import net.epsilony.mf.opt.integrate.InequalConstraintsIntegralCalculator;
import net.epsilony.mf.opt.integrate.LevelFunctionalIntegralUnitsGroup;
import net.epsilony.mf.opt.integrate.LevelFunctionalIntegrator;
import net.epsilony.mf.opt.integrate.LevelPenaltyIntegrator;
import net.epsilony.mf.opt.integrate.TriangleMarchingIntegralUnitsFactory;
import net.epsilony.mf.opt.integrate.config.OptIntegralConfig;
import net.epsilony.mf.opt.integrate.config.OptIntegralHub;
import net.epsilony.mf.opt.nlopt.NloptMMADriver;
import net.epsilony.mf.opt.nlopt.config.NloptConfig;
import net.epsilony.mf.opt.nlopt.config.NloptHub;
import net.epsilony.mf.opt.nlopt.config.NloptPersistConfig;
import net.epsilony.mf.opt.util.OptUtils;
import net.epsilony.mf.process.mix.MFMixerFunctionPack;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.shape_func.config.MLSConfig;
import net.epsilony.mf.shape_func.config.ShapeFunctionBaseConfig;
import net.epsilony.mf.util.bus.WeakBus;

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
public class Demo1 {
    public static Logger logger = LoggerFactory.getLogger(Demo1.class);

    public double left = 1;
    public double up = 15;
    public int width = 20;
    public int height = 20;
    public int margin = 2;
    public double triangleScale = 1;
    public double[] distanceCenter = { 11, 9 };
    public ToDoubleFunction<GeomPoint> objectCoreFunction = gp -> {
        return distance(gp.getCoord(), distanceCenter);
    };
    public ToDoubleFunction<GeomPoint> inequalIntegratorCore = gp -> -1;
    public double inequalShift = 4 * 4 * PI;

    public LevelOptModel levelOptModel;
    public AnalysisModel initLevelAnalysisModel;

    public ApplicationContext initialContext;
    public double influenceRadiusRatio = 3;
    public int initQuadratureDegree = 1;
    public double[] startParameters;
    public ApplicationContext levelMixerContext, nloptContext, optIntegralContext;

    public NloptMMADriver nloptMMADriver;

    private int optQuadratureDegree = 2;

    public void initialProcess() {
        genLevelOptModel();
        genInitialContext();
        SimpInitialModelProcessor simpInitialModelProcessor = new SimpInitialModelProcessor();
        simpInitialModelProcessor.setInfluenceRadius(influenceRadiusRatio * triangleScale);
        simpInitialModelProcessor.setQuadratureDegree(initQuadratureDegree);
        simpInitialModelProcessor.setModel(initLevelAnalysisModel);
        simpInitialModelProcessor.setProcessorContext(initialContext);

        simpInitialModelProcessor.process();

        genStartParameters();
    }

    public void genLevelOptModel() {
        RangeMarginLevelOptModelFactory factory = new RangeMarginLevelOptModelFactory(left, up, width, height, margin,
                margin, triangleScale, triangleScale, (line) -> false);
        levelOptModel = factory.produce();
        initLevelAnalysisModel = OptUtils.toInitalAnalysisModel(levelOptModel);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(levelOptModel);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        System.out.println("baos.size=" + baos.size());

        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            levelOptModel = (LevelOptModel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

    }

    public void genInitialContext() {
        AnnotationConfigApplicationContext result = new AnnotationConfigApplicationContext();
        result = new AnnotationConfigApplicationContext();
        Class<?>[] configCls = { ModelBusConfig.class, ImplicitAssemblerConfig.class, ImplicitIntegratorConfig.class,
                CenterPerturbSupportDomainSearcherConfig.class, CommonAnalysisModelHubConfig.class, MixerConfig.class,
                MLSConfig.class, TwoDSimpSearcherConfig.class, ConstantInfluenceConfig.class, LinearBasesConfig.class };
        result.register(configCls);
        result.refresh();
        initialContext = result;
    }

    public void genStartParameters() {
        CommonAnalysisModelHub modelHub = initialContext.getBean(
                CommonAnalysisModelHubConfig.COMMON_ANALYSIS_MODEL_HUB, CommonAnalysisModelHub.class);
        ArrayList<MFNode> nodes = modelHub.getNodes();
        ArrayList<MFNode> lagrangleDirichletNodes = modelHub.getLagrangleDirichletNodes();
        startParameters = new double[nodes.size() + lagrangleDirichletNodes.size()];
        for (MFNode node : nodes) {
            startParameters[node.getAssemblyIndex()] = node.getValue()[0];
            if (node.getLagrangeAssemblyIndex() > -1) {
                startParameters[node.getLagrangeAssemblyIndex()] = node.getLagrangeValue()[0];
            }
        }
    }

    public void prepareOpt() {

        nloptContext = new AnnotationConfigApplicationContext(NloptConfig.class, OptPersistBaseConfig.class,
                NloptPersistConfig.class);
        NloptHub nloptHub = nloptContext.getBean(NloptHub.class);

        optIntegralContext = new AnnotationConfigApplicationContext(OptIntegralConfig.class,
                CommonToPointsIntegratorConfig.class);
        OptIntegralHub optIntegralHub = optIntegralContext.getBean(OptIntegralHub.class);

        optIntegralHub.setQuadratureDegree(optQuadratureDegree);
        optIntegralHub.setLevelMixerPackFunctionProtoSupplier(() -> initialContext.getBean(MFMixerFunctionPack.class));
        optIntegralHub.setObjectIntegrator(objectIntegrator());
        optIntegralHub.setInequalConstraintsRangeIntegrators(inequalRangeIntegrators());
        optIntegralHub.setInequalConstraintsDomainIntegrators(inequalDomainIntegrators());

        InequalConstraintsIntegralCalculator inequalConstraintsIntegralCalculator = optIntegralContext
                .getBean(InequalConstraintsIntegralCalculator.class);
        inequalConstraintsIntegralCalculator.setRangeIntegralUnitsGroup(rangeIntegralUnitsGroup());

        nloptHub.setObjectParameterConsumer(optIntegralHub.getObjectParameterConsumer());
        nloptHub.setObjectCalculateTrigger(optIntegralHub.getObjectCalculateTrigger());
        nloptHub.setObjectValueSupplier(optIntegralHub.getObjectValueSupplier());
        nloptHub.setObjectGradientSupplier(optIntegralHub.getObjectGradientSupplier());

        nloptHub.setInequalConstraintsParameterConsumer(optIntegralHub.getInequalConstraintsParameterConsumer());
        nloptHub.setInequalConstraintsCalculateTrigger(optIntegralHub.getInequalConstraintsCalculateTrigger());
        nloptHub.setInequalConstraintsValueSuppliers(optIntegralHub.getInequalConstraintsValueSuppliers());
        nloptHub.setInequalConstraintsGradientSuppliers(optIntegralHub.getInequalConstraintsGradientSuppliers());

        nloptHub.getPrepareBus().register((obj, data) -> {
            obj.accept(data);
        }, optIntegralHub.getPrepareTrigger());

        nloptMMADriver = nloptContext.getBean(NloptMMADriver.class);
    }

    private LevelFunctionalIntegrator objectIntegrator() {
        CoreShiftRangeFunctionalIntegrator objectIntegrator = new CoreShiftRangeFunctionalIntegrator();
        objectIntegrator.setCoreFunction(objectCoreFunction);
        return objectIntegrator;
    }

    private List<LevelFunctionalIntegrator> inequalRangeIntegrators() {
        LevelPenaltyIntegrator integrator = optIntegralContext.getBean(LevelPenaltyIntegrator.class);

        return Arrays.asList(integrator);
    }

    private List<LevelFunctionalIntegrator> inequalDomainIntegrators() {
        CoreShiftRangeFunctionalIntegrator inequalIntegrator = new CoreShiftRangeFunctionalIntegrator();
        inequalIntegrator.setCoreFunction(inequalIntegratorCore);
        inequalIntegrator.setShift(inequalShift);
        return Arrays.asList(inequalIntegrator);
    }

    private LevelFunctionalIntegralUnitsGroup rangeIntegralUnitsGroup() {
        return new LevelFunctionalIntegralUnitsGroup() {

            @Override
            public Stream<PolygonIntegrateUnit> volume() {
                return null;
            }

            @Override
            public Stream<MFLine> boundary() {
                return levelOptModel.getRangeBarrier().getAll().stream();
            }

            @Override
            public void prepare() {
            }
        };

    }

    public void optimize() {
        TriangleMarchingIntegralUnitsFactory factory = optIntegralContext
                .getBean(TriangleMarchingIntegralUnitsFactory.class);
        factory.setCells(levelOptModel.getCells());

        nloptMMADriver.setName(Demo1.class.getSimpleName());
        nloptMMADriver.setInequalTolerents(new double[] { 1, 0 });
        nloptMMADriver.setStart(startParameters);
        nloptMMADriver.doOptimize();

    }

    @Configuration
    public static class LinearBasesConfig {

        @Resource(name = ShapeFunctionBaseConfig.MONOMIAL_BASES_DEGREE_BUS)
        WeakBus<Integer> monomialDegreeBus;

        @Bean
        public Boolean phonySetMonomialBasesDegree() {
            monomialDegreeBus.post(1);
            return true;
        }
    }

    public static void main(String[] args) {
        Demo1 demo = new Demo1();
        demo.initialProcess();
        demo.prepareOpt();

        demo.optimize();
    }
}
