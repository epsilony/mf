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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.epsilony.mf.model.PhysicalModel;
import net.epsilony.mf.model.RawPhysicalModel;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.util.convertor.OneOneToIterableOneOne;
import net.epsilony.mf.util.tuple.TwoTuple;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class FacetModelFractionizer implements Function<PhysicalModel, PhysicalModel> {

    Function<? super Iterable<? extends Line>, ? extends TwoTuple<? extends Iterable<? extends Line>, ? extends Map<? extends Line, ? extends Line>>> chainsFractionizer;

    @Override
    public PhysicalModel apply(PhysicalModel input) {
        List<Line> chainsHeads = new LinkedList<>();
        Facet facet = (Facet) input.getGeomRoot();
        for (Chain chain : facet.getRings()) {
            Line head = (Line) chain.getHead();
            chainsHeads.add(head);
        }
        TwoTuple<? extends Iterable<? extends Line>, ? extends Map<? extends Line, ? extends Line>> fractionedTuple = chainsFractionizer
                .apply(chainsHeads);
        Facet fracedFacet = Facet.byRingsHeads(Lists.newArrayList(fractionedTuple.getFirst()));
        Map<GeomUnit, GeomPointLoad<? extends LoadValue>> fracedLoadMap = new HashMap<>();
        for (Map.Entry<? extends Line, ? extends Line> newToOriginEntry : fractionedTuple.getSecond().entrySet()) {
            fracedLoadMap.put(newToOriginEntry.getKey(), input.getLoadMap().get(newToOriginEntry.getValue()));
        }

        fracedLoadMap.put(fracedFacet, input.getLoadMap().get(input.getGeomRoot()));

        RawPhysicalModel result = new RawPhysicalModel();
        result.setGeomRoot(fracedFacet);
        result.setLoadMap(fracedLoadMap);
        result.setSpatialDimension(input.getSpatialDimension());
        result.setValueDimension(input.getValueDimension());

        return result;
    }

    public FacetModelFractionizer(
            Function<? super Iterable<? extends Line>, ? extends TwoTuple<? extends Iterable<? extends Line>, ? extends Map<? extends Line, ? extends Line>>> chainsFractionizer) {
        this.chainsFractionizer = chainsFractionizer;
    }

    public static FacetModelFractionizer newInstance(
            Function<? super Line, ? extends TwoTuple<? extends Line, ? extends Map<? extends Line, ? extends Line>>> chainFractionizer) {
        OneOneToIterableOneOne<Line, TwoTuple<? extends Line, ? extends Map<? extends Line, ? extends Line>>> iterableChainFractionizer = new OneOneToIterableOneOne<>(
                chainFractionizer);
        ChainsFractionResultsMerger merger = new ChainsFractionResultsMerger();
        FacetModelFractionizer facetModelFractionizer = new FacetModelFractionizer(
                iterableChainFractionizer.andThen(merger));
        return facetModelFractionizer;
    }

}
