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

import java.util.ArrayList;
import java.util.Collections;

import net.epsilony.mf.model.MFNode;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SBLNodesAssembleIndexer extends AbstractLagrangleNodesAssemblerIndexer {
    boolean moveLagrangleToTail = true;

    @Override
    public void index() {
        SpaceBoundaryNodesAssemblerIndexer sb = new SpaceBoundaryNodesAssemblerIndexer();
        sb.setSpaceNodes(spaceNodes);
        sb.setBoundaryNodes(boundaryNodes);
        sb.index();
        sortedNodes = sb.getSortedIndexedNodes();

        indexLagrangleNodes();
        if (moveLagrangleToTail) {
            sortToMakeLagrangleNodesAtTail();
        }
    }

    private void indexLagrangleNodes() {
        for (MFNode node : sortedNodes) {
            node.setLagrangeAssemblyIndex(-1);
        }

        sortedLagrangleNodes = new ArrayList<>(dirichletNodes);
        Collections.sort(sortedLagrangleNodes, (o1, o2) -> o1.getAssemblyIndex() - o2.getAssemblyIndex());

        int lagId = sortedNodes.size();
        for (MFNode node : dirichletNodes) {
            node.setLagrangeAssemblyIndex(lagId++);
        }
    }

    private void sortToMakeLagrangleNodesAtTail() {
        Collections.sort(sortedNodes, (o1, o2) -> {
            int t = o1.getLagrangeAssemblyIndex() - o2.getLagrangeAssemblyIndex();
            return t == 0 ? o1.getAssemblyIndex() - o2.getAssemblyIndex() : t;
        });
    }

    @Override
    public ArrayList<MFNode> getSortedLagrangleNodes() {
        return sortedLagrangleNodes;
    }

    public boolean isMoveLagrangleToTail() {
        return moveLagrangleToTail;
    }

    public void setMoveLagrangleToTail(boolean moveLagrangleToTail) {
        this.moveLagrangleToTail = moveLagrangleToTail;
    }

}
