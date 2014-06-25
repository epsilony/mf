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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.process.indexer.LagrangleNodesAssembleIndexer;
import net.epsilony.mf.process.indexer.SBLNodesAssembleIndexer;
import net.epsilony.mf.util.parm.MFParmContainer;
import net.epsilony.mf.util.parm.ann.BusTrigger;
import net.epsilony.mf.util.parm.ann.GlobalBus;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CommonAnalysisModelHub implements MFParmContainer {

    AnalysisModel              analysisModel;
    ArrayList<MFNode>          nodes, spaceNodes, boundaryNodes, lagrangleDirichletNodes, extraLagragleNodes;
    ArrayList<MFGeomUnit>      boundaries, dirichletBoundaries;
    ConstitutiveLaw            constitutiveLaw;
    Map<Object, GeomPointLoad> loadMap;

    public AnalysisModel getAnalysisModel() {
        return analysisModel;
    }

    public static class T {
        public static final String v = "v";
    }

    @BusTrigger(aims = {
            "spatialDimension",
            "valueDimension",
            "spaceNodes",
            "boundaryNodes",
            "lagrangleDirichletNodes",
            "extraLagrangleNodes",
            "nodes",
            "loadMap",
            "boundaries",
            "dirichletBoundaries",
            "modelInputed", })
    public void setAnalysisModel(AnalysisModel analysisModel) {
        this.analysisModel = analysisModel;

        spaceNodes = new ArrayList<>(analysisModel.getSpaceNodes());

        loadMap = Collections.synchronizedMap(analysisModel.getLoadMap());

        extractBoundaries();

        genNodesAndIndexing();
    }

    @BusTrigger
    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    private void genNodesAndIndexing() {
        LagrangleNodesAssembleIndexer indexer = new SBLNodesAssembleIndexer();

        indexer.setSpaceNodes(spaceNodes);

        extractDirichletBoundaries();
        extractLagrangleDirichletNodes();
        indexer.setBoundaryNodes(boundaryNodes);
        indexer.setDirichletNodes(lagrangleDirichletNodes);
        indexer.index();

        nodes = indexer.getSortedIndexedNodes();
        lagrangleDirichletNodes = indexer.getSortedLagrangleNodes();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void extractBoundaries() {
        if (analysisModel.getBoundaryRoot() == null) {
            boundaries = new ArrayList<MFGeomUnit>();
            boundaryNodes = new ArrayList<MFNode>();
            return;
        }
        switch (analysisModel.getSpatialDimension()) {
            case 1:
                MFLine chain = (MFLine) analysisModel.getBoundaryRoot();
                MFLine last = null;
                for (MFLine l : chain) {
                    last = l;
                }
                boundaries = new ArrayList<>(Arrays.asList((MFNode) chain.getStart(), (MFNode) last.getStart()));
                boundaryNodes = new ArrayList<>((List) boundaries);
                break;
            case 2:
                MFFacet facet = (MFFacet) analysisModel.getBoundaryRoot();
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
    }

    public void extractLagrangleDirichletNodes() {
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

        Object t = analysisModel.getExtraData().get(AnalysisModel.SPACE_DIRICHLET_NODE_PRIDICATE);
        if (t != null) {
            @SuppressWarnings({ "unchecked" })
            Predicate<MFNode> spaceDirichletNodePrediate = (Predicate<MFNode>) t;
            List<MFNode> freeDirichletNodes = analysisModel.getSpaceNodes().stream().filter(spaceDirichletNodePrediate)
                    .collect(Collectors.toList());
            lagrangleDirichletNodes.addAll(freeDirichletNodes);
        }
    }

    @GlobalBus
    public int getSpatialDimension() {
        return analysisModel.getSpatialDimension();
    }

    @GlobalBus
    public int getValueDimension() {
        return analysisModel.getValueDimension();
    }

    @GlobalBus
    public ArrayList<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    @GlobalBus
    public ArrayList<MFNode> getBoundaryNodes() {
        return boundaryNodes;
    }

    @GlobalBus
    public ArrayList<MFNode> getLagrangleDirichletNodes() {
        return lagrangleDirichletNodes;
    }

    @GlobalBus
    public ArrayList<MFNode> getExtraLagrangleNodes() {
        return extraLagragleNodes;
    }

    @GlobalBus
    public ArrayList<MFNode> getNodes() {
        return nodes;
    }

    @GlobalBus
    public Map<Object, GeomPointLoad> getLoadMap() {
        return loadMap;
    }

    @GlobalBus
    public ArrayList<MFGeomUnit> getBoundaries() {
        return boundaries;
    }

    @GlobalBus
    public ArrayList<MFGeomUnit> getDirichletBoundaries() {
        return dirichletBoundaries;
    }

    @GlobalBus
    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    @GlobalBus
    public boolean getModelInputed() {
        return true;
    }

}
