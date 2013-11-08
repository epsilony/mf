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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodesIndesProcessor {

    public static Logger logger = LoggerFactory.getLogger(MFNodesIndesProcessor.class);
    private List<MFNode> allGeomNodes;
    private List<MFNode> spaceNodes;
    private Collection<? extends GeomUnit> dirichletBnds;
    private List<MFNode> extraLagDirichletNodes;
    private List<MFNode> allProcessNodes;
    private boolean applyDirichletByLagrange;
    private int spatialDimension;
    GeomUnit geomRoot;
    List<MFNode> boundaryNodes;
    int lagrangleNodesNum;

    public GeomUnit getGeomRoot() {
        return geomRoot;
    }

    public void setGeomRoot(GeomUnit geomRoot) {
        this.geomRoot = geomRoot;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    public void setDirichletBnds(Collection<? extends GeomUnit> dirichletBnds) {
        this.dirichletBnds = dirichletBnds;
    }

    public void setApplyDirichletByLagrange(boolean applyDirichletByLagrange) {
        this.applyDirichletByLagrange = applyDirichletByLagrange;
    }

    public void process() {
        processSpaceNodes();
        processGeomNodes();
        processLagrangeAndExtraDirichletNodes();
        countLagrangleNodesNum();
    }

    private void processSpaceNodes() {
        int asmIndex = 0;
        for (MFNode nd : spaceNodes) {
            nd.setAssemblyIndex(asmIndex++);
        }
    }

    private void processGeomNodes() {
        int asmIndex = spaceNodes.get(spaceNodes.size() - 1).getAssemblyIndex() + 1;
        allGeomNodes = new LinkedList<>(spaceNodes);
        if (null == geomRoot) {
            return;
        }
        genBoundaryNodes();
        for (MFNode node : boundaryNodes) {
            node.setAssemblyIndex(asmIndex++);
            allGeomNodes.add(node);
        }
    }

    private void processLagrangeAndExtraDirichletNodes() {

        extraLagDirichletNodes = null;
        if (!applyDirichletByLagrange) {
            allProcessNodes = allGeomNodes;
            logger.info("nodes indes processed");
            logger.info("(SPACE/ALL_GEOM/EXTRA_LAG/ALL_PROC)=({}, {}, null, {})", spaceNodes.size(),
                    allGeomNodes.size(), allProcessNodes.size());
            return;
        }
        switch (spatialDimension) {
        case 1:
            process1DExtraLagDiri();
            break;
        case 2:
            process2DExtraLagDiri();
            break;
        default:
            throw new IllegalStateException();
        }
        allProcessNodes = new ArrayList(allGeomNodes.size() + extraLagDirichletNodes.size());
        allProcessNodes.addAll(allGeomNodes);
        allProcessNodes.addAll(extraLagDirichletNodes);

        logger.info("nodes indes processed");
        logger.info("(SPACE/ALL_GEOM/EXTRA_LAG/ALL_PROC)=({}, {}, {}, {})", spaceNodes.size(), allGeomNodes.size(),
                extraLagDirichletNodes.size(), allProcessNodes.size());
    }

    private void process1DExtraLagDiri() {
        int asmIndex = allGeomNodes.get(allGeomNodes.size() - 1).getAssemblyIndex() + 1;
        extraLagDirichletNodes = new LinkedList<>();
        for (GeomUnit bnd : dirichletBnds) {
            MFNode node = (MFNode) bnd;
            node.setLagrangeAssemblyIndex(-1);
        }

        int lagIndex = asmIndex;

        for (GeomUnit bnd : dirichletBnds) {
            MFNode node = (MFNode) bnd;
            if (node.getLagrangeAssemblyIndex() < 0) {
                node.setLagrangeAssemblyIndex(lagIndex++);
                if (node.getAssemblyIndex() < 0) {
                    extraLagDirichletNodes.add(node);
                }
            }
        }
    }

    private void process2DExtraLagDiri() {
        int asmIndex = allGeomNodes.get(allGeomNodes.size() - 1).getAssemblyIndex() + 1;
        extraLagDirichletNodes = new LinkedList<>();
        for (GeomUnit bnd : dirichletBnds) {
            Line line = (Line) bnd;
            MFNode start = (MFNode) line.getStart();
            MFNode end = (MFNode) line.getEnd();
            start.setLagrangeAssemblyIndex(-1);
            end.setLagrangeAssemblyIndex(-1);
        }

        int lagIndex = asmIndex;

        // sort so that the lagrangle indes will not affect by unsorted hash map
        Collections.sort((List) dirichletBnds, new Comparator<GeomUnit>() {

            @Override
            public int compare(GeomUnit o1, GeomUnit o2) {
                Line line1 = (Line) o1;
                Line line2 = (Line) o2;
                MFNode nd1 = (MFNode) line1.getStart();
                MFNode nd2 = (MFNode) line2.getStart();
                return nd1.getAssemblyIndex() - nd2.getAssemblyIndex();
            }
        });

        for (GeomUnit bnd : dirichletBnds) {
            Line line = (Line) bnd;
            MFNode node = (MFNode) line.getStart();
            for (int i = 0; i < 2; i++) {
                if (node.getLagrangeAssemblyIndex() < 0) {
                    node.setLagrangeAssemblyIndex(lagIndex++);
                    if (node.getAssemblyIndex() < 0) {
                        extraLagDirichletNodes.add(node);
                    }
                }

                node = (MFNode) line.getEnd();
            }
        }
    }

    public List<MFNode> getAllGeomNodes() {
        return allGeomNodes;
    }

    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public List<MFNode> getExtraLagDirichletNodes() {
        return extraLagDirichletNodes;
    }

    public List<MFNode> getAllProcessNodes() {
        return allProcessNodes;
    }

    public int getLagrangleNodesNum() {
        return lagrangleNodesNum;
    }

    public int getSpatialDimension() {
        return spatialDimension;
    }

    public void setSpatialDimension(int spatialDimension) {
        this.spatialDimension = spatialDimension;
    }

    private void genBoundaryNodes() {
        boundaryNodes = new LinkedList<>();
        switch (spatialDimension) {
        case 1:
            Chain chain = (Chain) geomRoot;
            boundaryNodes.add((MFNode) chain.getHead().getStart());
            boundaryNodes.add((MFNode) chain.getLast().getStart());
            break;
        case 2:
            Facet facet = (Facet) geomRoot;
            for (Segment seg : facet) {
                boundaryNodes.add((MFNode) seg.getStart());
            }
            break;
        case 3:
            throw new UnsupportedOperationException();
        default:
            throw new IllegalStateException();
        }
    }

    private void countLagrangleNodesNum() {
        lagrangleNodesNum = 0;
        for (MFNode node : allProcessNodes) {
            if (node.getLagrangeAssemblyIndex() >= 0) {
                lagrangleNodesNum++;
            }
        }
    }
}
