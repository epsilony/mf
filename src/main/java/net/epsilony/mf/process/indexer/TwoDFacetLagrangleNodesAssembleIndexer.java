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
package net.epsilony.mf.process.indexer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TwoDFacetLagrangleNodesAssembleIndexer extends AbstractLagrangleNodesAssemblerIndexer {

    @Override
    protected void collectBoundaryNodes() {
        Facet facet = (Facet) geomRoot;
        boundaryNodes = TwoDFacetNodesAssemblerIndexer.collectBoundaryNodes(facet);
    }

    @Override
    protected Class<? extends GeomUnit> getGeomRootType() {
        return Facet.class;
    }

    @Override
    protected void collectAndIndexLagrangleNodes() {
        int asmIndex = spaceNodes.size() + boundaryNodes.size();
        extraLagrangleNodes = new LinkedList<>();
        allLagrangleNodes = new LinkedList<>();

        for (GeomUnit bnd : dirichletBnds) {
            Line line = (Line) bnd;
            MFNode start = (MFNode) line.getStart();
            MFNode end = (MFNode) line.getEnd();
            start.setLagrangeAssemblyIndex(-1);
            end.setLagrangeAssemblyIndex(-1);
        }

        int lagIndex = asmIndex;

        // sort so that the lagrangle indes settings will be more unique
        // dirichlet bnds sort may be affected by unsorted map like HashMap
        Collections.sort(dirichletBnds, new Comparator<GeomUnit>() {

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
                    allLagrangleNodes.add(node);
                    if (node.getAssemblyIndex() < 0) {
                        extraLagrangleNodes.add(node);
                    }
                }
                node = (MFNode) line.getEnd();
            }
        }
    }

}
