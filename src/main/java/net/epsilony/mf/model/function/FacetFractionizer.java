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
package net.epsilony.mf.model.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.epsilony.mf.model.function.ChainFractionizer.ChainFractionResult;
import net.epsilony.mf.model.function.FacetFractionizer.FacetFractionResult;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class FacetFractionizer implements Function<Facet, FacetFractionResult> {

    Function<Line, ? extends ChainFractionResult> chainFractionier;

    @Override
    public FacetFractionResult apply(Facet facet) {
        List<Chain> rings = facet.getRings();
        List<Line> heads = new ArrayList<>(rings.size());
        Map<Line, Line> newToOri = new HashMap<>();
        for (Chain chain : rings) {
            ChainFractionResult chainRes = chainFractionier.apply((Line) chain.getHead());
            heads.add(chainRes.getHead());
            newToOri.putAll(chainRes.getNewToOri());
        }
        Facet newFacet = Facet.byRingsHeads(heads);
        return new FacetFractionResult(newFacet, newToOri);
    }

    public Function<Line, ? extends ChainFractionResult> getChainFractionier() {
        return chainFractionier;
    }

    public void setChainFractionier(Function<Line, ? extends ChainFractionResult> chainFractionier) {
        this.chainFractionier = chainFractionier;
    }

    public FacetFractionizer() {
    }

    public FacetFractionizer(Function<Line, ? extends ChainFractionResult> chainFractionier) {
        this.chainFractionier = chainFractionier;
    }

    public static class FacetFractionResult {
        private final Facet facet;
        private final Map<Line, Line> newToOri;

        public Facet getFacet() {
            return facet;
        }

        public Map<Line, Line> getNewToOri() {
            return newToOri;
        }

        public FacetFractionResult(Facet facet, Map<Line, Line> newToOri) {
            this.facet = facet;
            this.newToOri = newToOri;
        }

    }

}
