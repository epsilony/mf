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

import java.util.LinkedList;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDChainLagrangleNodesAssembleIndexer extends AbstractLagrangleNodesAssemblerIndexer {

    @Override
    protected void collectBoundaryNodes() {
        Chain chain = (Chain) geomRoot;
        boundaryNodes = OneDChainNodesAssemblerIndexer.collectBoundaryNodes(chain);

    }

    @Override
    protected Class<? extends GeomUnit> getGeomRootType() {
        return Chain.class;
    }

    @Override
    protected void collectAndIndexLagrangleNodes() {
        int lagIndex = spaceNodes.size() + boundaryNodes.size();
        extraLagrangleNodes = new LinkedList<>();
        allLagrangleNodes = new LinkedList<>();
        for (GeomUnit bnd : dirichletBnds) {
            MFNode node = (MFNode) bnd;
            node.setLagrangeAssemblyIndex(-1);
        }

        for (GeomUnit bnd : dirichletBnds) {
            MFNode node = (MFNode) bnd;
            if (node.getLagrangeAssemblyIndex() < 0) {
                node.setLagrangeAssemblyIndex(lagIndex++);
                allLagrangleNodes.add(node);
                if (node.getAssemblyIndex() < 0) {
                    extraLagrangleNodes.add(node);
                }
            }
        }
    }
}
