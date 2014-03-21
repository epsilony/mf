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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import net.epsilony.mf.util.function.FunctionConnectors;
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
        Function<Stream<? extends Line>, Stream<TwoTuple<Line, Map<Line, Line>>>> streamedOneOne = FunctionConnectors
                .streamedOneOne(chainFractionizer);
        ChainsFractionResultsMerger merger = new ChainsFractionResultsMerger();
        FacetModelFractionizer facetModelFractionizer = new FacetModelFractionizer(streamedOneOne.andThen(merger));
        return facetModelFractionizer;

    }
}
