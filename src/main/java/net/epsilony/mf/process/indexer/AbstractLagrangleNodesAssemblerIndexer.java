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
import java.util.List;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.GeomUnit;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractLagrangleNodesAssemblerIndexer extends AbstractNodesAssemblerIndexer implements
        LagrangleNodesAssembleIndexer {

    protected List<? extends GeomUnit> dirichletBnds;
    protected List<MFNode> extraLagrangleNodes;
    protected List<MFNode> allLagrangleNodes;

    @Override
    public void index() {
        indexSpaceNodes();

        collectBoundaryNodes();
        indexBoundaryNodes();

        collectAndIndexLagrangleNodes();

        collectAllNodes();
        makeResultListsReadable();
    }

    @Override
    public void setDirichletBnds(List<? extends GeomUnit> dirichletBnds) {
        this.dirichletBnds = dirichletBnds;
    }

    abstract protected void collectAndIndexLagrangleNodes();

    @Override
    public List<MFNode> getExtraLagrangleNodes() {
        return extraLagrangleNodes;
    }

    @Override
    public List<MFNode> getAllLagrangleNodes() {
        return allLagrangleNodes;
    }

    @Override
    protected void collectAllNodes() {
        int allNodesSize = spaceNodes.size() + boundaryNodes.size() + extraLagrangleNodes.size();
        allNodes = new ArrayList<>(allNodesSize);
        allNodes.addAll(spaceNodes);
        allNodes.addAll(boundaryNodes);
        allNodes.addAll(extraLagrangleNodes);
    }

    @Override
    protected void makeResultListsReadable() {
        super.makeResultListsReadable();
        allLagrangleNodes = Collections.unmodifiableList(allLagrangleNodes);
        extraLagrangleNodes = Collections.unmodifiableList(extraLagrangleNodes);
    }

}
