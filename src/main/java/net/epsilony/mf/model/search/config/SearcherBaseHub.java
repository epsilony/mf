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
import net.epsilony.mf.util.parm.MFParmContainer;
import net.epsilony.mf.util.parm.ann.BusTrigger;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SearcherBaseHub implements MFParmContainer {

    private Supplier<RangeBasedMetricSearcher<MFNode>> nodesSearcherSupplier, influencedNodesSearcherSupplier;
    private Supplier<RangeBasedMetricSearcher<MFLine>> boundariesSearcherSupplier;
    private Collection<? extends MFNode>               nodes;
    private Collection<? extends MFLine>               boundaries;
    private int                                        spatialDimension;
    private boolean                                    modelInputed;

    @BusTrigger
    public void setNodes(Collection<? extends MFNode> nodes) {
        this.nodes = nodes;
    }

    @BusTrigger
    public void setBoundaries(Collection<? extends MFLine> boundaries) {
        this.boundaries = boundaries;
    }

    @BusTrigger
    public void setSpatialDimension(int spatialDimension) {
        this.spatialDimension = spatialDimension;
    }

    @BusTrigger
    public void setModelInputed(boolean modelInputed) {
        this.modelInputed = modelInputed;
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

    void setNodesSearcherSupplier(Supplier<RangeBasedMetricSearcher<MFNode>> nodesSearcherSupplier) {
        this.nodesSearcherSupplier = nodesSearcherSupplier;
    }

    void setInfluencedNodesSearcherSupplier(Supplier<RangeBasedMetricSearcher<MFNode>> influencedNodesSearcherSupplier) {
        this.influencedNodesSearcherSupplier = influencedNodesSearcherSupplier;
    }

    void setBoundariesSearcherSupplier(Supplier<RangeBasedMetricSearcher<MFLine>> boundariesSearcherSupplier) {
        this.boundariesSearcherSupplier = boundariesSearcherSupplier;
    }

    public Collection<? extends MFNode> getNodes() {
        return nodes;
    }

    public Collection<? extends MFLine> getBoundaries() {
        return boundaries;
    }

    public int getSpatialDimension() {
        return spatialDimension;
    }

    public boolean isModelInputed() {
        return modelInputed;
    }

}
