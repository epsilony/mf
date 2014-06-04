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
package net.epsilony.mf.model.search.config;

import java.util.Collection;
import java.util.function.Supplier;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.search.RangeBasedMetricSearcher;
import net.epsilony.mf.util.bus.WeakBus;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SearcherBaseHub {

    private WeakBus<Collection<? extends MFNode>> nodesBus;
    private WeakBus<Collection<? extends MFLine>> boundariesBus;
    private WeakBus<Integer>                      spatialDimensionBus;
    private WeakBus<Object>                       initBus;

    private Supplier<RangeBasedMetricSearcher<MFNode>> nodesSearcherSupplier, influencedNodesSearcherSupplier;
    private Supplier<RangeBasedMetricSearcher<MFLine>> boundariesSearcherSupplier;

    public void setNodes(Collection<? extends MFNode> nodes) {
        nodesBus.post(nodes);
    }

    public void setBoundaries(Collection<? extends MFLine> bnds) {
        boundariesBus.post(bnds);
    }

    public void setSpatialDimension(int spatialDimension) {
        spatialDimensionBus.post(spatialDimension);
    }

    public void init() {
        initBus.post(true);
    }

    public Supplier<RangeBasedMetricSearcher<MFNode>> getNodesSearcherSupplier() {
        return nodesSearcherSupplier;
    }

    public Supplier<RangeBasedMetricSearcher<MFNode>> getInfluencedNodesSearcherSupplier() {
        return influencedNodesSearcherSupplier;
    }

    public Supplier<RangeBasedMetricSearcher<MFLine>> getBoundariesSearcherSupplier() {
        return boundariesSearcherSupplier;
    }

    void setNodesBus(WeakBus<Collection<? extends MFNode>> nodesBus) {
        this.nodesBus = nodesBus;
    }

    void setBoundariesBus(WeakBus<Collection<? extends MFLine>> bndsBus) {
        this.boundariesBus = bndsBus;
    }

    void setSpatialDimensionBus(WeakBus<Integer> spatialDimensionBus) {
        this.spatialDimensionBus = spatialDimensionBus;
    }

    void setInitBus(WeakBus<Object> initBus) {
        this.initBus = initBus;
    }

    void setNodesSearcherSupplier(Supplier<RangeBasedMetricSearcher<MFNode>> nodesSearcherSupplier) {
        this.nodesSearcherSupplier = nodesSearcherSupplier;
    }

    void setInfluencedNodesSearcherSupplier(Supplier<RangeBasedMetricSearcher<MFNode>> influencedNodesSearcherSupplier) {
        this.influencedNodesSearcherSupplier = influencedNodesSearcherSupplier;
    }

    void setBoundariesSearcherSupplier(Supplier<RangeBasedMetricSearcher<MFLine>> boundariesSearcherSupplier) {
        this.boundariesSearcherSupplier = boundariesSearcherSupplier;
    }

}
