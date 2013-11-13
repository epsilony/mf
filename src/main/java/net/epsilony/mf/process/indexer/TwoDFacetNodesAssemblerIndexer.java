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
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Segment;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TwoDFacetNodesAssemblerIndexer extends AbstractNodesAssemblerIndexer {

    @Override
    protected void collectBoundaryNodes() {
        Facet facet = (Facet) geomRoot;
        collectBoundaryNodes(facet);
    }

    public static List<MFNode> collectBoundaryNodes(Facet facet) {
        List<MFNode> result = new LinkedList<>();

        for (Segment seg : facet) {
            result.add((MFNode) seg.getStart());
        }
        return result;
    }

    @Override
    protected Class<? extends GeomUnit> getGeomRootType() {
        return Facet.class;
    }

}
