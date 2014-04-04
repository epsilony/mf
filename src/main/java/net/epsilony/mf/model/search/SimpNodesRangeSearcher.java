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
package net.epsilony.mf.model.search;

import java.util.Collection;
import java.util.List;

import net.epsilony.tb.rangesearch.RangeSearcher;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SimpNodesRangeSearcher<N extends Node> implements RangeSearcher<double[], N> {
    List<? extends N> nodes;

    public List<? extends N> getNodes() {
        return nodes;
    }

    public void setNodes(List<? extends N> nodes) {
        this.nodes = nodes;
    }

    @Override
    public void rangeSearch(double[] from, double[] to, Collection<? super N> output) {
        output.clear();
        nodes.stream().filter((nd) -> {
            double[] coord = nd.getCoord();
            for (int i = 0; i < coord.length; i++) {
                double f = from[i];
                double t = to[i];
                double c = coord[i];
                if (c < f || c > t) {
                    return false;
                }
            }
            return true;
        }).forEach(output::add);
    }

}
