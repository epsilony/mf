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
package net.epsilony.mf.opt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import net.epsilony.mf.implicit.assembler.config.ImplicitAssemblerConfig;
import net.epsilony.mf.implicit.config.ImplicitIntegratorConfig;
import net.epsilony.mf.implicit.sample.SimpInitialModelProcessor;
import net.epsilony.mf.integrate.integrator.config.CommonToPointsIntegratorConfig;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.config.CommonAnalysisModelHubConfig;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.search.config.TwoDLRTreeSearcherConfig;
import net.epsilony.mf.model.support_domain.config.CenterPerturbSupportDomainSearcherConfig;
import net.epsilony.mf.opt.integrate.InequalConstraintsIntegralCalculator;
import net.epsilony.mf.opt.integrate.LevelFunctionalIntegralUnitsGroup;
import net.epsilony.mf.opt.integrate.LevelFunctionalIntegrator;
import net.epsilony.mf.opt.integrate.TriangleMarchingIntegralUnitsFactory;
import net.epsilony.mf.opt.integrate.config.OptIntegralConfig;
import net.epsilony.mf.opt.integrate.config.OptIntegralHub;
import net.epsilony.mf.opt.integrate.config.OptIntegralPersistConfig;
import net.epsilony.mf.opt.integrate.config.OptIntegralPersistHub;
import net.epsilony.mf.opt.nlopt.NloptMMADriver;
import net.epsilony.mf.opt.nlopt.config.NloptConfig;
import net.epsilony.mf.opt.nlopt.config.NloptHub;
import net.epsilony.mf.opt.nlopt.config.NloptPersistConfig;
import net.epsilony.mf.opt.nlopt.config.NloptPersistHub;
import net.epsilony.mf.opt.persist.OptIndexialRecorder;
import net.epsilony.mf.opt.persist.OptRootRecorder;
import net.epsilony.mf.opt.persist.config.OptPersistBaseConfig;
import net.epsilony.mf.opt.persist.config.OptPersistBaseHub;
import net.epsilony.mf.opt.util.OptUtils;
import net.epsilony.mf.process.mix.MFMixerFunctionPack;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.shape_func.config.MLSConfig;
import net.epsilony.mf.shape_func.config.ShapeFunctionBaseConfig;
import net.epsilony.mf.util.MFBeanUtils;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.persist.RecordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableList;
import com.mongodb.DB;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptIntegralProcessor {
    public static final Logger logger = LoggerFactory.getLogger(NloptIntegralProcessor.class);

    private String name;
    private LevelOptModel levelOptModel;
    private AnalysisModel initLevelAnalysisModel;

    private double influenceRadius = 3;
    private int initQuadratureDegree = 1;
    private int optQuadratureDegree = 2;
    private double[] startParameters;
    private ApplicationContext initialContext, optPersistBaseContext, nloptContext, optIntegralContext;

    private NloptMMADriver nloptMMADriver;

    public static final List<Class<?>> DEFAULT_MIXER_CONFIGS = ImmutableList.of(
            CenterPerturbSupportDomainSearcherConfig.class, MixerConfig.class, MLSConfig.class,
            TwoDLRTreeSearcherConfig.class, ConstantInfluenceConfig.class, LinearBasesConfig.class);

    public static final List<Class<?>> DEFAULT_CONTEXT_CONFIGS = ImmutableList.of(ModelBusConfig.class,
            ImplicitAssemblerConfig.class, ImplicitIntegratorConfig.class, CommonAnalysisModelHubConfig.class);

    private List<Class<?>> initialConfigs = new ArrayList<>(DEFAULT_CONTEXT_CONFIGS);

    private List<Class<?>> mixerConfigs = new ArrayList<>(DEFAULT_MIXER_CONFIGS);

    private LevelFunctionalIntegrator objectIntegrator;

    private List<? extends LevelFunctionalIntegrator> inequalRangeIntegrators;

    private List<? extends LevelFunctionalIntegrator> inequalDomainIntegrators;

    private double[] inequalTolerents;

    public void initialProcess() {
        genInitialContext();

        initLevelAnalysisModel = OptUtils.toInitalAnalysisModel(levelOptModel);

        SimpInitialModelProcessor simpInitialModelProcessor = new SimpInitialModelProcessor();
        simpInitialModelProcessor.setInfluenceRadius(influenceRadius);
        simpInitialModelProcessor.setQuadratureDegree(initQuadratureDegree);
        simpInitialModelProcessor.setModel(initLevelAnalysisModel);
        simpInitialModelProcessor.setProcessorContext(initialContext);

        simpInitialModelProcessor.process();

        genStartParameters();
    }

    public void genInitialContext() {
        List<Class<?>> configs = new ArrayList<>(initialConfigs);
        configs.addAll(mixerConfigs);
        initialContext = new AnnotationConfigApplicationContext(configs.toArray(new Class[0]));
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

        optPersistBaseContext = new AnnotationConfigApplicationContext(OptPersistBaseConfig.class);
        OptPersistBaseHub optPersistBaseHub = optPersistBaseContext.getBean(OptPersistBaseHub.class);

        nloptContext = new AnnotationConfigApplicationContext(NloptConfig.class, NloptPersistConfig.class);

        NloptPersistHub nloptPersistHub = nloptContext.getBean(NloptPersistHub.class);
        MFBeanUtils.transmitProperties(optPersistBaseHub, nloptPersistHub, logger);

        NloptHub nloptHub = nloptContext.getBean(NloptHub.class);

        optIntegralContext = new AnnotationConfigApplicationContext(OptIntegralConfig.class,
                OptIntegralPersistConfig.class, CommonToPointsIntegratorConfig.class);
        OptIntegralPersistHub optIntegralPersistHub = optIntegralContext.getBean(OptIntegralPersistHub.class);
        MFBeanUtils.transmitProperties(optPersistBaseHub, optIntegralPersistHub, logger);

        OptIntegralHub optIntegralHub = optIntegralContext.getBean(OptIntegralHub.class);

        optIntegralHub.setQuadratureDegree(optQuadratureDegree);
        optIntegralHub.setLevelMixerPackFunctionProtoSupplier(() -> initialContext.getBean(MFMixerFunctionPack.class));
        optIntegralHub.setObjectIntegrator(objectIntegrator);
        optIntegralHub.setInequalConstraintsRangeIntegrators(inequalRangeIntegrators);
        optIntegralHub.setInequalConstraintsDomainIntegrators(inequalDomainIntegrators);

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

    private LevelFunctionalIntegralUnitsGroup rangeIntegralUnitsGroup() {
        return new RangeIntegralUnitsGroup();
    }

    class RangeIntegralUnitsGroup implements LevelFunctionalIntegralUnitsGroup {
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
    }

    public void optimize() {
        TriangleMarchingIntegralUnitsFactory factory = optIntegralContext
                .getBean(TriangleMarchingIntegralUnitsFactory.class);
        factory.setCells(levelOptModel.getCells());

        nloptMMADriver.setName(name);
        nloptMMADriver.setInequalTolerents(inequalTolerents);
        nloptMMADriver.setStart(startParameters);

        recordMixerAndNodes();

        nloptMMADriver.doOptimize();
    }

    private void recordMixerAndNodes() {

        OptPersistBaseHub optPersistBaseHub = optPersistBaseContext.getBean(OptPersistBaseHub.class);

        OptRootRecorder optRootRecorder = optPersistBaseHub.getOptRootRecorder();

        optRootRecorder.prepareRecord("mixerConfigs",
                mixerConfigs.stream().map(Class::getName).collect(Collectors.toList()));
        optRootRecorder.record();

        OptIndexialRecorder nodeRecorder = new OptIndexialRecorder();
        DB db = optPersistBaseHub.getDb();
        nodeRecorder.setDbCollection(db.getCollection("opt.model.node"));
        nodeRecorder.setUpperIdSupplier(optPersistBaseHub.getCurrentRootIdSupplier());

        CommonAnalysisModelHub analysisModelHub = initialContext.getBean(CommonAnalysisModelHub.class);
        ArrayList<MFNode> nodes = analysisModelHub.getNodes();
        for (MFNode node : nodes) {
            Map<String, Object> nodeData = RecordUtils.readRecordFields(node);
            nodeData.put("coord", node.getCoord());
            nodeRecorder.record(nodeData);
        }
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

    public LevelOptModel getLevelOptModel() {
        return levelOptModel;
    }

    public void setLevelOptModel(LevelOptModel levelOptModel) {
        this.levelOptModel = levelOptModel;
    }

    public double getInfluenceRadius() {
        return influenceRadius;
    }

    public void setInfluenceRadius(double influenceRadius) {
        this.influenceRadius = influenceRadius;
    }

    public int getInitQuadratureDegree() {
        return initQuadratureDegree;
    }

    public void setInitQuadratureDegree(int initQuadratureDegree) {
        this.initQuadratureDegree = initQuadratureDegree;
    }

    public int getOptQuadratureDegree() {
        return optQuadratureDegree;
    }

    public void setOptQuadratureDegree(int optQuadratureDegree) {
        this.optQuadratureDegree = optQuadratureDegree;
    }

    public double[] getStartParameters() {
        return startParameters;
    }

    public void setStartParameters(double[] startParameters) {
        this.startParameters = startParameters;
    }

    public List<Class<?>> getInitialConfigs() {
        return initialConfigs;
    }

    public void setInitialConfigs(List<Class<?>> initialContextConfigs) {
        this.initialConfigs = initialContextConfigs;
    }

    public ApplicationContext getInitialContext() {
        return initialContext;
    }

    public ApplicationContext getNloptContext() {
        return nloptContext;
    }

    public ApplicationContext getOptIntegralContext() {
        return optIntegralContext;
    }

    public NloptMMADriver getNloptMMADriver() {
        return nloptMMADriver;
    }

    public void setObjectIntegrator(LevelFunctionalIntegrator objectIntegrator) {
        this.objectIntegrator = objectIntegrator;
    }

    public void setInequalRangeIntegrators(List<? extends LevelFunctionalIntegrator> inequalRangeIntegrators) {
        this.inequalRangeIntegrators = inequalRangeIntegrators;
    }

    public void setInequalDomainIntegrators(List<? extends LevelFunctionalIntegrator> inequalDomainIntegrators) {
        this.inequalDomainIntegrators = inequalDomainIntegrators;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getInequalTolerents() {
        return inequalTolerents;
    }

    public void setInequalTolerents(double[] inequalTolerents) {
        this.inequalTolerents = inequalTolerents;
    }

}
