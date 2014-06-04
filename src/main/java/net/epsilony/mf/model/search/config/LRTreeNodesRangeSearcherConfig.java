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

import java.util.List;

import javax.annotation.Resource;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.util.bus.BiConsumerRegistry;
import net.epsilony.tb.rangesearch.LayeredRangeTree;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class LRTreeNodesRangeSearcherConfig {

    @Resource(name = SearcherBaseConfig.SEARCHER_SPATAIL_DIMENSION_BUS)
    BiConsumerRegistry<Integer>                spatialDimensionBus;
    @Resource(name = SearcherBaseConfig.SEARCHER_NODES_BUS)
    BiConsumerRegistry<List<? extends MFNode>> nodesBus;
    @Resource(name = SearcherBaseConfig.SEARCHER_INIT_BUS)
    BiConsumerRegistry<Object>                 initBus;

    @Bean(name = SearcherBaseConfig.NODES_RANGE_SEARCHER_PROTO)
    public LayeredRangeTree<double[], MFNode> nodesRangeSearcher() {
        return nodesLRTreeBuilder().getLRTree();
    }

    @Bean
    CoordKeyLRTreeBuilder<MFNode> nodesLRTreeBuilder() {
        CoordKeyLRTreeBuilder<MFNode> allNodesLRTreeBuilder = new CoordKeyLRTreeBuilder<>();
        allNodesLRTreeBuilder.setCoordPicker(MFNode::getCoord);
        spatialDimensionBus.register(CoordKeyLRTreeBuilder::setSpatialDimension, allNodesLRTreeBuilder);
        nodesBus.register(CoordKeyLRTreeBuilder::setDatas, allNodesLRTreeBuilder);
        initBus.register(CoordKeyLRTreeBuilder::prepareTree, allNodesLRTreeBuilder);
        return allNodesLRTreeBuilder;
    }
}
