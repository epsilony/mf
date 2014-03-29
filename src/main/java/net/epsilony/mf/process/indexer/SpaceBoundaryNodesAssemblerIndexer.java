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

import net.epsilony.mf.model.MFNode;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class SpaceBoundaryNodesAssemblerIndexer extends AbstractNodesAssembleIndexer {

    @Override
    public void index() {
        indexSpaceNodes();
        indexBoundaryNodes();
        collectAllNodes();
    }

    protected void indexSpaceNodes() {
        int asmIndex = 0;
        for (MFNode nd : spaceNodes) {
            nd.setAssemblyIndex(asmIndex++);
        }
    }

    protected void indexBoundaryNodes() {
        int asmIndex = spaceNodes.get(spaceNodes.size() - 1).getAssemblyIndex() + 1;

        for (MFNode node : boundaryNodes) {
            node.setAssemblyIndex(asmIndex++);
        }
    }

    protected void collectAllNodes() {
        sortedNodes = new ArrayList<>(spaceNodes.size() + boundaryNodes.size());
        sortedNodes.addAll(spaceNodes);
        sortedNodes.addAll(boundaryNodes);
    }
}