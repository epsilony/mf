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

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.search.EnlargeRangeGenerator;
import net.epsilony.mf.model.search.InRadiusPickerFilter;
import net.epsilony.mf.model.search.InsideInfluencePickerFilter;
import net.epsilony.mf.model.search.RangeBasedMetricSearcher;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;
import net.epsilony.tb.rangesearch.RangeSearcher;
import net.epsilony.tb.solid.Node;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class SearcherBaseConfig extends ApplicationContextAwareImpl {

    // needed to be defined: ----------------------------------

    public static final String BOUNDARIES_SEARCHER_PROTO = "boundariesSearcherProto";
    public static final String BOUNDARIES_RANGE_SEARCHER_PROTO = "boundariesRangeSearcherProto";
    public static final String NODES_RANGE_SEARCHER_PROTO = "nodesRangeSearcherProto";

    @SuppressWarnings("unchecked")
    @Bean
    public SearcherBaseHub searchBaseHub() {
        SearcherBaseHub result = new SearcherBaseHub();
        result.setNodesSearcherSupplier(this::nodesSearcherProto);
        result.setInfluencedNodesSearcherSupplier(this::influencedNodesSearcherProto);
        result.setBoundariesSearcherSupplier(() -> {
            RangeBasedMetricSearcher<MFLine> bndSearcherProto = (RangeBasedMetricSearcher<MFLine>) applicationContext
                    .getBean(BOUNDARIES_SEARCHER_PROTO);
            return bndSearcherProto;
        });

        result.setNodesBus(nodesBus());
        result.setBoundariesBus(boundariesBus());
        result.setSpatialDimensionBus(spatialDimensionBus());
        result.setInitBus(initBus());

        return result;
    }

    public static final String SEARCHER_NODES_BUS = "searcherNodesBus";

    @Bean(name = SEARCHER_NODES_BUS)
    public WeakBus<Collection<? extends MFNode>> nodesBus() {
        return new WeakBus<>(SEARCHER_NODES_BUS);
    }

    public static final String SEARCHER_BOUNDARIES_BUS = "searcherBoundariesBus";

    @Bean(name = SEARCHER_BOUNDARIES_BUS)
    public WeakBus<Collection<? extends MFLine>> boundariesBus() {
        return new WeakBus<>(SEARCHER_BOUNDARIES_BUS);
    }

    public static final String SEARCHER_SPATAIL_DIMENSION_BUS = "searcherSpatialDimensionBus";

    @Bean(name = SEARCHER_SPATAIL_DIMENSION_BUS)
    public WeakBus<Integer> spatialDimensionBus() {
        return new WeakBus<>(SEARCHER_SPATAIL_DIMENSION_BUS);
    }

    public static final String SEARCHER_INIT_BUS = "searcherInitBus";

    @Bean(name = SEARCHER_INIT_BUS)
    public WeakBus<Object> initBus() {
        return new WeakBus<>(SEARCHER_INIT_BUS);
    }

    public static final String NODES_SEARCHER_PROTO = "nodesSearcherProto";

    @Bean(name = NODES_SEARCHER_PROTO)
    @Scope("prototype")
    public RangeBasedMetricSearcher<MFNode> nodesSearcherProto() {
        RangeBasedMetricSearcher<MFNode> rangeBasedMetricSearcher = new RangeBasedMetricSearcher<>();
        rangeBasedMetricSearcher.setRangeGenerator(new EnlargeRangeGenerator());
        rangeBasedMetricSearcher.setRangeSearcher(getNodesRangeSearcherProto());
        rangeBasedMetricSearcher.setMetricFilter(new InRadiusPickerFilter<Node>(Node::getCoord));
        return rangeBasedMetricSearcher;
    }

    public static final String INFLUENCED_NODES_SEARCHER_PROTO = "influencedNodesMetricSearcherProto";

    @Bean(name = INFLUENCED_NODES_SEARCHER_PROTO)
    @Scope("prototype")
    public RangeBasedMetricSearcher<MFNode> influencedNodesSearcherProto() {
        RangeBasedMetricSearcher<MFNode> rangeBasedMetricSearcher = new RangeBasedMetricSearcher<>();
        rangeBasedMetricSearcher.setRangeGenerator(new EnlargeRangeGenerator());
        rangeBasedMetricSearcher.setRangeSearcher(getNodesRangeSearcherProto());
        rangeBasedMetricSearcher.setMetricFilter(new InsideInfluencePickerFilter());
        return rangeBasedMetricSearcher;
    }

    private RangeSearcher<double[], ? extends MFNode> getNodesRangeSearcherProto() {
        @SuppressWarnings("unchecked")
        RangeSearcher<double[], ? extends MFNode> allNodesRangeSearcher = (RangeSearcher<double[], ? extends MFNode>) applicationContext
                .getBean(NODES_RANGE_SEARCHER_PROTO);
        return allNodesRangeSearcher;
    }
}
