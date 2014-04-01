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
package net.epsilony.mf.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.process.indexer.LagrangleNodesAssembleIndexer;
import net.epsilony.mf.process.indexer.SBLNodesAssembleIndexer;
import net.epsilony.mf.util.bus.ConsumerBus;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Segment;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CommonAnalysisModelHub {

    AnalysisModel analysisModel;
    ArrayList<MFNode> nodes, spaceNodes, boundaryNodes, lagrangleDirichletNodes, extraLagragleNodes;
    ArrayList<GeomUnit> boundaries, dirichletBoundaries;
    ConstitutiveLaw constitutiveLaw;
    Map<GeomUnit, GeomPointLoad> loadMap;
    ConsumerBus<Collection<? extends MFNode>> nodesBus, spaceNodesBus;
    ConsumerBus<Collection<? extends GeomUnit>> boundariesBus;
    ConsumerBus<Map<GeomUnit, GeomPointLoad>> loadMapBus;
    ConsumerBus<Integer> spatialDimensionBus;
    ConsumerBus<Integer> valueDimensionBus;
    ConsumerBus<Object> modelInputedBus;
    ConsumerBus<Collection<? extends GeomUnit>> lagrangleDirichletNodesBus;
    ConsumerBus<ConstitutiveLaw> constitutiveLawBus;

    public AnalysisModel getAnalysisModel() {
        return analysisModel;
    }

    public void setAnalysisModel(AnalysisModel analysisModel) {
        this.analysisModel = analysisModel;

        spatialDimensionBus.postToFresh(analysisModel.getSpatialDimension());
        valueDimensionBus.postToFresh(analysisModel.getValueDimension());

        spaceNodes = new ArrayList<>(analysisModel.getSpaceNodes());
        spaceNodesBus.postToFresh(spaceNodes);
        loadMap = Collections.synchronizedMap(analysisModel.getLoadMap());
        loadMapBus.postToFresh(loadMap);

        extractBoundaries();
        boundariesBus.postToFresh(boundaries);

        genNodesAndIndexing();
        nodesBus.postToFresh(nodes);
        if (null != lagrangleDirichletNodesBus) {
            lagrangleDirichletNodesBus.postToFresh(lagrangleDirichletNodes);
        }

        if (constitutiveLaw != null) {
            constitutiveLawBus.postToFresh(constitutiveLaw);
        }

        modelInputedBus.postToFresh(true);
    }

    private void genNodesAndIndexing() {
        LagrangleNodesAssembleIndexer indexer = new SBLNodesAssembleIndexer();

        indexer.setSpaceNodes(spaceNodes);

        extractDirichletBoundaries();
        indexer.setBoundaryNodes(boundaryNodes);
        indexer.setDirichletNodes(lagrangleDirichletNodes);
        indexer.index();

        nodes = indexer.getSortedIndexedNodes();
        nodesBus.postToFresh(nodes);

        lagrangleDirichletNodes = indexer.getSortedLagrangleNodes();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void extractBoundaries() {
        switch (analysisModel.getSpatialDimension()) {
        case 1:
            Chain chain = (Chain) analysisModel.getGeomRoot();
            boundaries = new ArrayList<>(Arrays.asList(chain.getHead().getStart(), chain.getLast().getStart()));
            boundaryNodes = new ArrayList<>((List) boundaries);
            break;
        case 2:
            Facet facet = (Facet) analysisModel.getGeomRoot();
            boundaryNodes = new ArrayList<>();
            boundaries = new ArrayList<>();
            for (Segment seg : facet) {
                boundaryNodes.add((MFNode) seg.getStart());
                boundaries.add(seg);
            }
            break;
        default:
            throw new IllegalStateException();
        }
    }

    private void extractDirichletBoundaries() {
        dirichletBoundaries = new ArrayList<>();

        for (GeomUnit bnd : boundaries) {
            GeomPointLoad load = loadMap.get(bnd);
            if (load != null && load.isDirichlet()) {
                dirichletBoundaries.add(bnd);
            }
        }

        lagrangleDirichletNodes = new ArrayList<>(dirichletBoundaries.size());
        switch (analysisModel.getSpatialDimension()) {
        case 1:
            for (GeomUnit bnd : dirichletBoundaries) {
                MFNode node = (MFNode) bnd;
                lagrangleDirichletNodes.add(node);
            }
            break;
        case 2:
            for (GeomUnit bnd : dirichletBoundaries) {
                Segment seg = (Segment) bnd;
                lagrangleDirichletNodes.add((MFNode) seg.getStart());
            }
            break;
        default:
            throw new IllegalStateException();
        }

    }

    public ArrayList<MFNode> getNodes() {
        return nodes;
    }

    public ArrayList<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public ArrayList<MFNode> getBoundaryNodes() {
        return boundaryNodes;
    }

    public ArrayList<MFNode> getLagrangleDirichletNodes() {
        return lagrangleDirichletNodes;
    }

    public ArrayList<MFNode> getExtraLagragleNodes() {
        return extraLagragleNodes;
    }

    public ArrayList<GeomUnit> getBoundaries() {
        return boundaries;
    }

    public ArrayList<GeomUnit> getDirichletBoundaries() {
        return dirichletBoundaries;
    }

    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    public Map<GeomUnit, GeomPointLoad> getLoadMap() {
        return loadMap;
    }

    public ConsumerBus<Collection<? extends MFNode>> getNodesBus() {
        return nodesBus;
    }

    public ConsumerBus<Collection<? extends MFNode>> getSpaceNodesBus() {
        return spaceNodesBus;
    }

    public ConsumerBus<Collection<? extends GeomUnit>> getBoundariesBus() {
        return boundariesBus;
    }

    public ConsumerBus<Map<GeomUnit, GeomPointLoad>> getLoadMapBus() {
        return loadMapBus;
    }

    public ConsumerBus<Integer> getSpatialDimensionBus() {
        return spatialDimensionBus;
    }

    public ConsumerBus<Integer> getValueDimensionBus() {
        return valueDimensionBus;
    }

    public ConsumerBus<Object> getModelInputedBus() {
        return modelInputedBus;
    }

    public ConsumerBus<Collection<? extends GeomUnit>> getLagrangleDirichletNodesBus() {
        return lagrangleDirichletNodesBus;
    }

    public ConsumerBus<ConstitutiveLaw> getConstitutiveLawBus() {
        return constitutiveLawBus;
    }

    public void setNodesBus(ConsumerBus<Collection<? extends MFNode>> nodesBus) {
        this.nodesBus = nodesBus;
    }

    public void setSpaceNodesBus(ConsumerBus<Collection<? extends MFNode>> spaceNodesBus) {
        this.spaceNodesBus = spaceNodesBus;
    }

    public void setBoundariesBus(ConsumerBus<Collection<? extends GeomUnit>> boundariesBus) {
        this.boundariesBus = boundariesBus;
    }

    public void setLoadMapBus(ConsumerBus<Map<GeomUnit, GeomPointLoad>> loadMapBus) {
        this.loadMapBus = loadMapBus;
    }

    public void setSpatialDimensionBus(ConsumerBus<Integer> spatialDimensionBus) {
        this.spatialDimensionBus = spatialDimensionBus;
    }

    public void setValueDimensionBus(ConsumerBus<Integer> valueDimensionBus) {
        this.valueDimensionBus = valueDimensionBus;
    }

    public void setModelInputedBus(ConsumerBus<Object> modelInputedBus) {
        this.modelInputedBus = modelInputedBus;
    }

    public void setLagrangleDirichletNodesBus(ConsumerBus<Collection<? extends GeomUnit>> lagrangleDirichletNodesBus) {
        this.lagrangleDirichletNodesBus = lagrangleDirichletNodesBus;
    }

    public void setConstitutiveLawBus(ConsumerBus<ConstitutiveLaw> constitutiveLawBus) {
        this.constitutiveLawBus = constitutiveLawBus;
    }
}
