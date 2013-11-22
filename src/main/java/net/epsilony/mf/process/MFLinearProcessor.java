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

package net.epsilony.mf.process;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.GeomModel2DUtils;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.LagrangleAssembler;
import net.epsilony.mf.process.indexer.LagrangleNodesAssembleIndexer;
import net.epsilony.mf.process.indexer.NodesAssembleIndexer;
import net.epsilony.mf.process.integrate.MFIntegralProcessor;
import net.epsilony.mf.process.integrate.MFIntegrateResult;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.synchron.SynchronizedIterator;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFLinearProcessor {

    public static Logger logger = LoggerFactory.getLogger(MFLinearProcessor.class);
    protected NodesAssembleIndexer nodesAssembleIndexer;
    protected MFNodesInfluenceRadiusProcessor nodesInfluenceRadiusProcessor = new MFNodesInfluenceRadiusProcessor();
    protected MFMixerFactory mixerFactory = new MFMixerFactory();
    protected MFIntegralProcessor integralProcessor;
    protected MFSolver mainMatrixSolver;
    protected AnalysisModel analysisModel;
    protected MFShapeFunction shapeFunction;
    protected Factory<Map<MFProcessType, Assembler>> assemblersGroupFactory;
    protected InfluenceRadiusCalculator influenceRadiusCalculator;

    public void preprocess() {
        logger.info("start preprocessing");
        prepare();
        integrate();
    }

    public MFIntegrateResult getIntegrateResult() {
        return integralProcessor.getIntegrateResult();
    }

    public void solve() {
        MFIntegrateResult integrateResult = getIntegrateResult();
        mainMatrixSolver.setMainMatrix(integrateResult.getMainMatrix());
        mainMatrixSolver.setMainVector(integrateResult.getMainVector());
        mainMatrixSolver.solve();

        fillNodeValues(mainMatrixSolver.getResult());
    }

    private void fillNodeValues(MFMatrix result) {
        int valueDimension = analysisModel.getValueDimension();
        for (MFNode node : nodesAssembleIndexer.getAllNodes()) {
            int nodeValueIndex = node.getAssemblyIndex() * valueDimension;
            if (nodeValueIndex >= 0) {
                double[] nodeValue = new double[valueDimension];
                for (int i = 0; i < valueDimension; i++) {
                    nodeValue[i] = result.get(i + nodeValueIndex, 0);
                    node.setValue(nodeValue);
                }
            }
            int lagrangeValueIndex = node.getLagrangeAssemblyIndex();
            MFMatrix mainMatrix = getIntegrateResult().getMainMatrix();
            if (lagrangeValueIndex >= 0) {
                double[] lagrangeValue = new double[valueDimension];
                boolean[] lagrangeValueValidity = new boolean[valueDimension];
                for (int i = 0; i < valueDimension; i++) {
                    int index = lagrangeValueIndex * valueDimension + i;
                    lagrangeValue[i] = result.get(index, 0);
                    lagrangeValueValidity[i] = mainMatrix.get(index, index) == 0; // a
                                                                                  // prototyle
                                                                                  // of
                                                                                  // validity
                }
                node.setLagrangeValue(lagrangeValue);
                node.setLagrangeValueValidity(lagrangeValueValidity);
            }
        }
        logger.info("filled nodes values");
    }

    public PostProcessor genPostProcessor() {
        PostProcessor result = new PostProcessor();
        result.setShapeFunction(SerializationUtils.clone(shapeFunction));
        result.setNodeValueDimension(analysisModel.getValueDimension());
        result.setSupportDomainSearcher(nodesInfluenceRadiusProcessor.getSupportDomainSearcherFactory().produce());
        result.setMaxInfluenceRad(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        return result;
    }

    private void prepare() {
        logger.info("start preparing");
        prepareProcessNodesDatas();
        prepareMixerFactory();
        wrapAssemblersGroupFactory();
        logger.info("prepared!");
    }

    private void integrate() {
        logger.info("start integrating");
        logger.info("integrate processor: {}", integralProcessor);

        integralProcessor.setIntegrateUnitsGroup(genIntegrateUnitsGroup());
        integralProcessor.setMainMatrixSize(getMainMatrixSize());
        integralProcessor.setAssemblersGroupList(new AssemblerFactoryWrapper());
        integralProcessor.setMixerFactory(mixerFactory);
        integralProcessor.integrate();
    }

    private Map<MFProcessType, SynchronizedIterator<MFIntegrateUnit>> genIntegrateUnitsGroup() {

        EnumMap<MFProcessType, SynchronizedIterator<MFIntegrateUnit>> result = new EnumMap<>(MFProcessType.class);
        for (MFProcessType type : MFProcessType.values()) {
            result.put(type, SynchronizedIterator.produce(analysisModel.getIntegrateUnitsGroup().get(type)));
        }
        return result;
    }

    private int getMainMatrixSize() {
        int valueDimension = analysisModel.getValueDimension();
        if (nodesAssembleIndexer instanceof LagrangleNodesAssembleIndexer) {
            LagrangleNodesAssembleIndexer lagIndexer = (LagrangleNodesAssembleIndexer) nodesAssembleIndexer;
            return valueDimension * (lagIndexer.getAllNodes().size() + lagIndexer.getAllLagrangleNodes().size());
        } else {
            return valueDimension * (nodesAssembleIndexer.getAllNodes().size());
        }
    }

    private void prepareProcessNodesDatas() {
        nodesAssembleIndexer.setSpaceNodes(analysisModel.getSpaceNodes());
        nodesAssembleIndexer.setGeomRoot(analysisModel.getGeomRoot());

        if (nodesAssembleIndexer instanceof LagrangleNodesAssembleIndexer) {
            LagrangleNodesAssembleIndexer lagrangleIndexer = (LagrangleNodesAssembleIndexer) nodesAssembleIndexer;
            lagrangleIndexer.setDirichletBnds(searchDirichletBnds(analysisModel));
        }

        nodesAssembleIndexer.index();

        int spatialDimension = analysisModel.getSpatialDimension();
        nodesInfluenceRadiusProcessor.setAllNodes(nodesAssembleIndexer.getAllNodes());
        nodesInfluenceRadiusProcessor.setSpaceNodes(nodesAssembleIndexer.getSpaceNodes());
        nodesInfluenceRadiusProcessor.setDimension(spatialDimension);
        switch (spatialDimension) {
        case 1:
            nodesInfluenceRadiusProcessor.setBoundaries(null);
            break;
        case 2:
            nodesInfluenceRadiusProcessor.setBoundaries(GeomModel2DUtils.getAllSegments(analysisModel.getGeomRoot()));
            break;
        default:
            throw new IllegalStateException();
        }
        nodesInfluenceRadiusProcessor.setInfluenceRadiusCalculator(influenceRadiusCalculator);
        nodesInfluenceRadiusProcessor.process();

        logger.info("nodes datas prepared");
    }

    private void prepareMixerFactory() {
        logger.info("start preparing mixer factory");
        logger.info("shape function: {}", shapeFunction);
        mixerFactory.setMaxNodesInfluenceRadius(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        shapeFunction.setDimension(analysisModel.getSpatialDimension());
        mixerFactory.setShapeFunction(shapeFunction);
        mixerFactory.setSupportDomainSearcherFactory(nodesInfluenceRadiusProcessor.getSupportDomainSearcherFactory());

    }

    protected void wrapAssemblersGroupFactory() {
        logger.info("start preparing assembler");

    }

    protected Factory<Map<MFProcessType, Assembler>> genPreparedAssemblersGroupFactory() {
        return new AssemblerFactoryWrapper();
    }

    private class AssemblerFactoryWrapper implements Factory<Map<MFProcessType, Assembler>> {

        @Override
        public Map<MFProcessType, Assembler> produce() {
            Map<MFProcessType, Assembler> assemblersGroup = assemblersGroupFactory.produce();
            prepareAssemblersGroup(assemblersGroup);
            return assemblersGroup;
        }
    }

    protected void prepareAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroup) {
        int allGeomNodesNum = nodesAssembleIndexer.getSpaceNodes().size()
                + nodesAssembleIndexer.getBoundaryNodes().size();
        for (Entry<MFProcessType, Assembler> entry : assemblersGroup.entrySet()) {
            Assembler assembler = entry.getValue();
            assembler.setNodesNum(allGeomNodesNum);
            assembler.setSpatialDimension(analysisModel.getSpatialDimension());
            assembler.setValueDimension(analysisModel.getValueDimension());
            if (assembler instanceof LagrangleAssembler) {
                LagrangleAssembler sL = (LagrangleAssembler) assembler;
                LagrangleNodesAssembleIndexer lagrangleIndexer = (LagrangleNodesAssembleIndexer) nodesAssembleIndexer;
                sL.setAllLagrangleNodesNum(lagrangleIndexer.getAllLagrangleNodes().size());
            }
        }
        logger.info("prepared assemblers group: {}", assemblersGroup);
    }

    public static List<GeomUnit> searchDirichletBnds(AnalysisModel model) {
        LinkedList<GeomUnit> dirichletBnd = new LinkedList<>();

        for (Map.Entry<GeomUnit, MFLoad> entry : model.getLoadMap().entrySet()) {
            MFLoad load = entry.getValue();
            if (load instanceof SegmentLoad) {
                SegmentLoad segLoad = (SegmentLoad) load;
                if (!segLoad.isDirichlet()) {
                    continue;
                }
            } else if (load instanceof NodeLoad) {
                NodeLoad nodeLoad = (NodeLoad) load;
                if (!nodeLoad.isDirichlet()) {
                    continue;
                }
            } else {
                continue;
            }

            dirichletBnd.add(entry.getKey());
        }
        return dirichletBnd;
    }

    public void setNodesAssembleIndexer(NodesAssembleIndexer nodesAssembleIndexer) {
        this.nodesAssembleIndexer = nodesAssembleIndexer;
    }

    public void setIntegralProcessor(MFIntegralProcessor integralProcessor) {
        this.integralProcessor = integralProcessor;
    }

    public void setMainMatrixSolver(MFSolver mainMatrixSolver) {
        this.mainMatrixSolver = mainMatrixSolver;
    }

    public void setAnalysisModel(AnalysisModel analysisModel) {
        this.analysisModel = analysisModel;
    }

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public void setAssemblersGroupFactory(Factory<Map<MFProcessType, Assembler>> assemblersGroupFactory) {
        this.assemblersGroupFactory = assemblersGroupFactory;
    }
}
