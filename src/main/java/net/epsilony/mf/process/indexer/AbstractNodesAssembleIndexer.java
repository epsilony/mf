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
import java.util.LinkedList;
import java.util.List;

import net.epsilony.mf.model.MFNode;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public abstract class AbstractNodesAssembleIndexer implements NodesAssembleIndexer {

    protected List<? extends MFNode> spaceNodes;
    protected List<? extends MFNode> boundaryNodes;
    protected ArrayList<MFNode>      sortedNodes;

    @Override
    public void setSpaceNodes(List<? extends MFNode> spaceNodes) {
        this.spaceNodes = new LinkedList<>(spaceNodes);
    }

    @Override
    public void setBoundaryNodes(List<? extends MFNode> boundaryNodes) {
        this.boundaryNodes = boundaryNodes;
    }

    @Override
    public ArrayList<MFNode> getSortedIndexedNodes() {
        return sortedNodes;
    }

}