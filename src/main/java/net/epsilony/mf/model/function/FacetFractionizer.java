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
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.MFLine;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class FacetFractionizer implements Function<MFFacet, FacetFractionResult> {

    Function<MFLine, ? extends ChainFractionResult> chainFractionier;

    @Override
    public FacetFractionResult apply(MFFacet facet) {
        List<MFLine> rings = facet.getRingsHeads();
        List<MFLine> heads = new ArrayList<>(rings.size());
        Map<MFLine, MFLine> newToOri = new HashMap<>();
        for (MFLine chain : rings) {
            ChainFractionResult chainRes = chainFractionier.apply(chain);
            heads.add(chainRes.getHead());
            newToOri.putAll(chainRes.getNewToOri());
        }
        MFFacet newFacet = new MFFacet();
        newFacet.getRingsHeads().addAll(heads);
        newFacet.requireWell();

        return new FacetFractionResult(newFacet, newToOri);
    }

    public Function<MFLine, ? extends ChainFractionResult> getChainFractionier() {
        return chainFractionier;
    }

    public void setChainFractionier(Function<MFLine, ? extends ChainFractionResult> chainFractionier) {
        this.chainFractionier = chainFractionier;
    }

    public FacetFractionizer() {
    }

    public FacetFractionizer(Function<MFLine, ? extends ChainFractionResult> chainFractionier) {
        this.chainFractionier = chainFractionier;
    }

    public static class FacetFractionResult {
        private final MFFacet             facet;
        private final Map<MFLine, MFLine> newToOri;

        public MFFacet getFacet() {
            return facet;
        }

        public Map<MFLine, MFLine> getNewToOri() {
            return newToOri;
        }

        public FacetFractionResult(MFFacet facet, Map<MFLine, MFLine> newToOri) {
            this.facet = facet;
            this.newToOri = newToOri;
        }

    }

}
