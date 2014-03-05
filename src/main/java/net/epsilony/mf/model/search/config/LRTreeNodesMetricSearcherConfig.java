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

import static net.epsilony.mf.util.event.EventBuses.types;

import java.util.Collection;

import javax.annotation.Resource;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.search.NodeCoordPicker;
import net.epsilony.mf.util.event.MethodEventBus;
import net.epsilony.tb.rangesearch.LayeredRangeTree;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
@Import(NodesMetricCommonConfig.class)
public class LRTreeNodesMetricSearcherConfig {

    @Resource
    MethodEventBus spatialDimensionEventBus;
    @Resource
    MethodEventBus allNodesEventBus;
    @Resource
    MethodEventBus modelInputtedEventBus;

    @Bean
    public LayeredRangeTree<double[], MFNode> allNodesRangeSearcher() {
        return allNodesLRTreeBuilder().getLRTree();
    }

    @Bean
    public CoordKeyLRTreeBuilder<MFNode> allNodesLRTreeBuilder() {
        CoordKeyLRTreeBuilder<MFNode> result = new CoordKeyLRTreeBuilder<>();
        result.setCoordPicker(new NodeCoordPicker());
        return result;
    }

    @Bean
    public boolean phonyRegistryAllNodesLRTreeBuilderToEventBuses() {
        CoordKeyLRTreeBuilder<MFNode> allNodesLRTreeBuilder = allNodesLRTreeBuilder();
        spatialDimensionEventBus.registry(allNodesLRTreeBuilder, "setSpatialDimension", types(int.class));
        allNodesEventBus.registry(allNodesLRTreeBuilder, "setDatas", types(Collection.class));
        modelInputtedEventBus.registry(allNodesLRTreeBuilder, "prepareTree", types());
        return true;
    }

}
