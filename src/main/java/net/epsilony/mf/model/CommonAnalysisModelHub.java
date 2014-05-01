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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.process.indexer.LagrangleNodesAssembleIndexer;
import net.epsilony.mf.process.indexer.SBLNodesAssembleIndexer;
import net.epsilony.mf.util.bus.WeakBus;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CommonAnalysisModelHub {

    AnalysisModel analysisModel;
    ArrayList<MFNode> nodes, spaceNodes, boundaryNodes, lagrangleDirichletNodes, extraLagragleNodes;
    ArrayList<MFGeomUnit> boundaries, dirichletBoundaries;
    ConstitutiveLaw constitutiveLaw;
    Map<Object, GeomPointLoad> loadMap;
    WeakBus<Collection<? extends MFNode>> nodesBus, spaceNodesBus;
    WeakBus<Collection<? extends MFGeomUnit>> boundariesBus;
    WeakBus<Map<Object, GeomPointLoad>> loadMapBus;
    WeakBus<Integer> spatialDimensionBus;
    WeakBus<Integer> valueDimensionBus;
    WeakBus<Object> modelInputedBus;
    WeakBus<Collection<? extends MFNode>> lagrangleDirichletNodesBus;
    WeakBus<ConstitutiveLaw> constitutiveLawBus;

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
        lagrangleDirichletNodes = indexer.getSortedLagrangleNodes();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void extractBoundaries() {
        if (analysisModel.getGeomRoot() == null) {
            boundaries = new ArrayList<MFGeomUnit>();
            boundaryNodes = new ArrayList<MFNode>();
            return;
        }
        switch (analysisModel.getSpatialDimension()) {
        case 1:
            MFLine chain = (MFLine) analysisModel.getGeomRoot();
            MFLine last = null;
            for (MFLine l : chain) {
                last = l;
            }
            boundaries = new ArrayList<>(Arrays.asList((MFNode) chain.getStart(), (MFNode) last.getStart()));
            boundaryNodes = new ArrayList<>((List) boundaries);
            break;
        case 2:
            MFFacet facet = (MFFacet) analysisModel.getGeomRoot();
            boundaryNodes = new ArrayList<>();
            boundaries = new ArrayList<>();
            for (MFLine seg : facet) {
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

        for (MFGeomUnit bnd : boundaries) {
            GeomPointLoad load = loadMap.get(bnd);
            if (load != null && load.isDirichlet()) {
                dirichletBoundaries.add(bnd);
            }
        }

        lagrangleDirichletNodes = new ArrayList<>(dirichletBoundaries.size());
        switch (analysisModel.getSpatialDimension()) {
        case 1:
            for (MFGeomUnit bnd : dirichletBoundaries) {
                MFNode node = (MFNode) bnd;
                lagrangleDirichletNodes.add(node);
            }
            break;
        case 2:
            LinkedHashSet<MFNode> dirichletNodesSet = new LinkedHashSet<>();
            for (MFGeomUnit bnd : dirichletBoundaries) {
                MFLine seg = (MFLine) bnd;
                dirichletNodesSet.add((MFNode) seg.getStart());
                dirichletNodesSet.add((MFNode) seg.getEnd());
            }
            lagrangleDirichletNodes.addAll(dirichletNodesSet);
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

    public ArrayList<MFGeomUnit> getBoundaries() {
        return boundaries;
    }

    public ArrayList<MFGeomUnit> getDirichletBoundaries() {
        return dirichletBoundaries;
    }

    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    public Map<Object, GeomPointLoad> getLoadMap() {
        return loadMap;
    }

    public WeakBus<Collection<? extends MFNode>> getNodesBus() {
        return nodesBus;
    }

    public WeakBus<Collection<? extends MFNode>> getSpaceNodesBus() {
        return spaceNodesBus;
    }

    public WeakBus<Collection<? extends MFGeomUnit>> getBoundariesBus() {
        return boundariesBus;
    }

    public WeakBus<Map<Object, GeomPointLoad>> getLoadMapBus() {
        return loadMapBus;
    }

    public WeakBus<Integer> getSpatialDimensionBus() {
        return spatialDimensionBus;
    }

    public WeakBus<Integer> getValueDimensionBus() {
        return valueDimensionBus;
    }

    public WeakBus<Object> getModelInputedBus() {
        return modelInputedBus;
    }

    public WeakBus<Collection<? extends MFNode>> getLagrangleDirichletNodesBus() {
        return lagrangleDirichletNodesBus;
    }

    public WeakBus<ConstitutiveLaw> getConstitutiveLawBus() {
        return constitutiveLawBus;
    }

    public void setNodesBus(WeakBus<Collection<? extends MFNode>> nodesBus) {
        this.nodesBus = nodesBus;
    }

    public void setSpaceNodesBus(WeakBus<Collection<? extends MFNode>> spaceNodesBus) {
        this.spaceNodesBus = spaceNodesBus;
    }

    public void setBoundariesBus(WeakBus<Collection<? extends MFGeomUnit>> boundariesBus) {
        this.boundariesBus = boundariesBus;
    }

    public void setLoadMapBus(WeakBus<Map<Object, GeomPointLoad>> loadMapBus) {
        this.loadMapBus = loadMapBus;
    }

    public void setSpatialDimensionBus(WeakBus<Integer> spatialDimensionBus) {
        this.spatialDimensionBus = spatialDimensionBus;
    }

    public void setValueDimensionBus(WeakBus<Integer> valueDimensionBus) {
        this.valueDimensionBus = valueDimensionBus;
    }

    public void setModelInputedBus(WeakBus<Object> modelInputedBus) {
        this.modelInputedBus = modelInputedBus;
    }

    public void setLagrangleDirichletNodesBus(WeakBus<Collection<? extends MFNode>> lagrangleDirichletNodesBus) {
        this.lagrangleDirichletNodesBus = lagrangleDirichletNodesBus;
    }

    public void setConstitutiveLawBus(WeakBus<ConstitutiveLaw> constitutiveLawBus) {
        this.constitutiveLawBus = constitutiveLawBus;
    }
}
