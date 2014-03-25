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

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.search.EnlargeRangeGenerator;
import net.epsilony.mf.model.search.InRadiusPickerFilter;
import net.epsilony.mf.model.search.InsideInfluencePickerFilter;
import net.epsilony.mf.model.search.NodeCoordPicker;
import net.epsilony.mf.model.search.RangeBasedMetricSearcher;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;
import net.epsilony.tb.rangesearch.RangeSearcher;
import net.epsilony.tb.solid.Node;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SearcherBaseConfig extends ApplicationContextAwareImpl {
    public static final String NODES_SEARCHER_PROTO = "nodesSearcherProto";
    public static final String INFLUENCED_NODES_SEARCHER_PROTO = "influencedNodesMetricSearcherProto";

    // needed to be defined: ----------------------------------
    /**
     * @see LRTreeNodesRangeSearcherConfig
     */
    public static final String NODES_RANGES_SEARCHER_PROTO = "nodesRangeSearcherProto";
    /**
     * @see TwoDBoundariesSearcherConfig
     */
    public static final String BOUNDARIES_SEARCHER_PROTO = "boundariesSearcherProto";
    /**
     * @see TwoDLRTreeBoundariesRangeSearcherConfig
     */
    public static final String BOUNDARIES_RANGE_SEARCHER_PROTO = "boundariesRangeSearcherProto";

    @Bean(name = NODES_SEARCHER_PROTO)
    @Scope("prototype")
    public RangeBasedMetricSearcher<MFNode> nodesSearcherProto() {
        RangeBasedMetricSearcher<MFNode> rangeBasedMetricSearcher = new RangeBasedMetricSearcher<>();
        rangeBasedMetricSearcher.setRangeGenerator(new EnlargeRangeGenerator());
        rangeBasedMetricSearcher.setRangeSearcher(getNodesRangeSearcherProto());
        rangeBasedMetricSearcher.setMetricFilter(new InRadiusPickerFilter<Node>(new NodeCoordPicker()));
        return rangeBasedMetricSearcher;
    }

    @Bean(name = INFLUENCED_NODES_SEARCHER_PROTO)
    @Scope("prototype")
    public RangeBasedMetricSearcher<MFNode> influencedNodesMetricSearcherProto() {
        RangeBasedMetricSearcher<MFNode> rangeBasedMetricSearcher = new RangeBasedMetricSearcher<>();
        rangeBasedMetricSearcher.setRangeGenerator(new EnlargeRangeGenerator());
        rangeBasedMetricSearcher.setRangeSearcher(getNodesRangeSearcherProto());
        rangeBasedMetricSearcher.setMetricFilter(new InsideInfluencePickerFilter());
        return rangeBasedMetricSearcher;
    }

    private RangeSearcher<double[], ? extends MFNode> getNodesRangeSearcherProto() {
        @SuppressWarnings("unchecked")
        RangeSearcher<double[], ? extends MFNode> allNodesRangeSearcher = (RangeSearcher<double[], ? extends MFNode>) applicationContext
                .getBean(NODES_RANGES_SEARCHER_PROTO);
        return allNodesRangeSearcher;
    }
}
