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
import java.util.LinkedList;
import java.util.List;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public abstract class AbstractNodesAssemblerIndexer implements NodesAssembleIndexer {

    protected List<MFNode> spaceNodes;
    protected GeomUnit geomRoot;
    protected List<MFNode> allNodes;
    List<MFNode> boundaryNodes;

    @Override
    public void index() {
        indexSpaceNodes();
        collectBoundaryNodes();
        indexBoundaryNodes();
        collectAllNodes();
        makeResultListsReadable();
    }

    protected void indexSpaceNodes() {
        int asmIndex = 0;
        for (MFNode nd : spaceNodes) {
            nd.setAssemblyIndex(asmIndex++);
        }
    }

    abstract protected void collectBoundaryNodes();

    abstract protected Class<? extends GeomUnit> getGeomRootType();

    @Override
    public void setGeomRoot(GeomUnit geomRoot) {
        Class<? extends GeomUnit> geomRootType = getGeomRootType();
        if (geomRootType.isInstance(geomRoot)) {
            this.geomRoot = geomRoot;
        } else {
            throw new IllegalArgumentException("Type unsupported! The geom root is not an instance of " + geomRootType
                    + " but :" + geomRoot.getClass());
        }
    }

    protected void indexBoundaryNodes() {
        int asmIndex = spaceNodes.get(spaceNodes.size() - 1).getAssemblyIndex() + 1;

        for (MFNode node : boundaryNodes) {
            node.setAssemblyIndex(asmIndex++);
        }
    }

    protected void collectAllNodes() {
        allNodes = new ArrayList<>(spaceNodes.size() + boundaryNodes.size());
        allNodes.addAll(spaceNodes);
        allNodes.addAll(boundaryNodes);
    }

    protected void makeResultListsReadable() {
        spaceNodes = Collections.unmodifiableList(spaceNodes);
        boundaryNodes = Collections.unmodifiableList(boundaryNodes);
        allNodes = Collections.unmodifiableList(allNodes);
    }

    @Override
    public void setSpaceNodes(List<? extends MFNode> spaceNodes) {
        this.spaceNodes = new LinkedList<>(spaceNodes);
    }

    @Override
    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    @Override
    public List<MFNode> getAllNodes() {
        return allNodes;
    }

    @Override
    public List<MFNode> getBoundaryNodes() {
        return boundaryNodes;
    }

}