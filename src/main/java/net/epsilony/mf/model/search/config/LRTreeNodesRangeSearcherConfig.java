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
import net.epsilony.mf.model.config.ModelBusConfig;
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

    @Resource(name = ModelBusConfig.SPATIAL_DIMENSION_BUS)
    BiConsumerRegistry<Integer> spatialDimensionEventBus;
    @Resource(name = ModelBusConfig.NODES_BUS)
    BiConsumerRegistry<List<? extends MFNode>> allNodesEventBus;
    @Resource(name = ModelBusConfig.MODEL_INPUTED_BUS)
    BiConsumerRegistry<Object> modelInputtedEventBus;

    @Bean(name = SearcherBaseConfig.NODES_RANGE_SEARCHER_PROTO)
    public LayeredRangeTree<double[], MFNode> nodesRangeSearcher() {
        return nodesLRTreeBuilder().getLRTree();
    }

    @Bean
    CoordKeyLRTreeBuilder<MFNode> nodesLRTreeBuilder() {
        CoordKeyLRTreeBuilder<MFNode> allNodesLRTreeBuilder = new CoordKeyLRTreeBuilder<>();
        allNodesLRTreeBuilder.setCoordPicker(MFNode::getCoord);
        spatialDimensionEventBus.register(CoordKeyLRTreeBuilder::setSpatialDimension, allNodesLRTreeBuilder);
        allNodesEventBus.register(CoordKeyLRTreeBuilder::setDatas, allNodesLRTreeBuilder);
        modelInputtedEventBus.register(CoordKeyLRTreeBuilder::prepareTree, allNodesLRTreeBuilder);
        return allNodesLRTreeBuilder;
    }
}
