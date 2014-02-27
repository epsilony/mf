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
package net.epsilony.mf.model.convertor;

import java.util.List;
import java.util.Map;

import net.epsilony.mf.util.convertor.OneOneLink;
import net.epsilony.mf.util.convertor.OneOneToIterableOneOne;
import net.epsilony.mf.util.tuple.TwoTuple;
import net.epsilony.tb.RudeFactory;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class FractionDemo {
    public static <N extends Node> FacetModelFractionizer genFacetModelFractionizer(Class<N> nodeType,
            double coordDistanceSup) {
        SingleLineFractionizer singleLineFractionizer = new SingleLineFractionizer.ByUndisturbedNeighbourCoordsDistanceSup(
                coordDistanceSup);
        ChainFractionizer<N> chainFractionizer = new ChainFractionizer<>(singleLineFractionizer, new RudeFactory<>(
                nodeType));
        OneOneToIterableOneOne<Line, TwoTuple<Line, Map<Line, Line>>> iterableChainFractionizer = new OneOneToIterableOneOne<>(
                chainFractionizer);
        ChainsFractionResultsMerger merger = new ChainsFractionResultsMerger();
        OneOneLink<Iterable<? extends Line>, Iterable<TwoTuple<Line, Map<Line, Line>>>, TwoTuple<List<Line>, Map<Line, Line>>> oneOneLink = new OneOneLink<>(
                iterableChainFractionizer, merger);
        FacetModelFractionizer facetModelFractionizer = new FacetModelFractionizer(oneOneLink);
        return facetModelFractionizer;

    }
}
