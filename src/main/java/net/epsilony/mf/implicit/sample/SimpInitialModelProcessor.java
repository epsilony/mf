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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import net.epsilony.mf.integrate.integrator.config.IntegralBaseConfig;
import net.epsilony.mf.integrate.integrator.config.MFConsumerGroup;
import net.epsilony.mf.integrate.integrator.config.ThreeStageIntegralCollection;
import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.CommonAnalysisModelHub;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.influence.config.ConstantInfluenceConfig;
import net.epsilony.mf.model.influence.config.InfluenceBaseConfig;
import net.epsilony.mf.model.search.config.SearcherBaseHub;
import net.epsilony.mf.process.assembler.matrix.MatrixHub;
import net.epsilony.mf.process.mix.config.MixerConfig;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.matrix.MFMatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SimpInitialModelProcessor {

    public static final Logger     logger = LoggerFactory.getLogger(SimpInitialModelProcessor.class);
    // requirement
    private AnalysisModel          model;
    private double                 influenceRadius;
    private int                    quadratureDegree;
    private ApplicationContext     processorContext;
    //
    private CommonAnalysisModelHub modelHub;
    private ArrayList<MFNode>      nodes;
    private MatrixHub              matrixHub;

    public void process() {
        modelHub = processorContext.getBean(CommonAnalysisModelHub.class);
        modelHub.setAnalysisModel(model);

        SearcherBaseHub searcherBaseHub = processorContext.getBean(SearcherBaseHub.class);
        searcherBaseHub.setNodes(modelHub.getNodes());
        searcherBaseHub.setBoundaries((Collection) modelHub.getBoundaries());
        searcherBaseHub.setSpatialDimension(2);
        searcherBaseHub.init();

        prepositionInfluenceProcess();

        suggestSearchRadiusToMixerBus();

        suggestQuadratureDegree();

        integrate();

        solve();
    }

    public void solve() {
        MFSolver solver = new RcmSolver();
        solver.setMainMatrix(matrixHub.getMergedMainMatrix());
        solver.setMainVector(matrixHub.getMergedMainVector());
        solver.solve();
        MFMatrix result = solver.getResult();

        nodes = modelHub.getNodes();

        for (MFNode nd : nodes) {
            int assemblyIndex = nd.getAssemblyIndex();
            double[] value = new double[] { result.get(assemblyIndex, 0) };
            nd.setValue(value);
            int lagIndex = nd.getLagrangeAssemblyIndex();
            if (lagIndex >= 0) {
                final double lagValue = result.get(lagIndex, 0);
                nd.setLagrangeValue(new double[] { lagValue });
            }
        }
    }

    public MatrixHub integrate() {
        matrixHub = processorContext.getBean(MatrixHub.class);
        matrixHub.post();

        IntegrateUnitsGroup integrateUnitsGroup = model.getIntegrateUnitsGroup();
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
        return matrixHub;
    }

    public void suggestQuadratureDegree() {
        @SuppressWarnings("unchecked")
        WeakBus<Integer> quadDegreeBus = (WeakBus<Integer>) processorContext
                .getBean(IntegralBaseConfig.QUADRATURE_DEGREE_BUS);
        quadDegreeBus.post(quadratureDegree);
    }

    public void suggestSearchRadiusToMixerBus() {
        processorContext.getBean(InfluenceBaseConfig.INFLUENCE_PROCESSOR, Runnable.class).run();
        @SuppressWarnings("unchecked")
        WeakBus<Double> mixerRadiusBus = (WeakBus<Double>) processorContext.getBean(MixerConfig.MIXER_MAX_RADIUS_BUS);
        mixerRadiusBus.post(influenceRadius);
    }

    public void prepositionInfluenceProcess() {
        @SuppressWarnings("unchecked")
        WeakBus<Double> infRadBus = (WeakBus<Double>) processorContext
                .getBean(ConstantInfluenceConfig.CONSTANT_INFLUCENCE_RADIUS_BUS);

        infRadBus.post(influenceRadius);
    }

    public void setModel(AnalysisModel model) {
        this.model = model;
    }

    public void setInfluenceRadius(double influenceRadius) {
        this.influenceRadius = influenceRadius;
    }

    public void setQuadratureDegree(int quadratureDegree) {
        this.quadratureDegree = quadratureDegree;
    }

    public void setProcessorContext(ApplicationContext processorContext) {
        this.processorContext = processorContext;
    }

}
