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
import java.util.List;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDChainNodesAssemblerIndexer extends AbstractNodesAssemblerIndexer {
    @Override
    protected void collectBoundaryNodes() {
        Chain chain = (Chain) geomRoot;
        boundaryNodes = collectBoundaryNodes(chain);
    }

    public static List<MFNode> collectBoundaryNodes(Chain chain) {
        List<MFNode> result = new LinkedList<>();

        result.add((MFNode) chain.getHead().getStart());
        result.add((MFNode) chain.getLast().getStart());
        return result;
    }

    @Override
    protected Class<? extends GeomUnit> getGeomRootType() {
        return Chain.class;
    }

}