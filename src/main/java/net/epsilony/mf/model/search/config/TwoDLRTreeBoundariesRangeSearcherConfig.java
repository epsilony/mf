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

import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.search.Segment2DChordCenterPicker;
import net.epsilony.mf.util.bus.ConsumerRegistry;
import net.epsilony.tb.rangesearch.LayeredRangeTree;
import net.epsilony.tb.solid.Segment;

import org.springframework.context.annotation.Bean;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TwoDLRTreeBoundariesRangeSearcherConfig {

    @Resource(name = ModelBusConfig.SPATIAL_DIMENSION_BUS)
    ConsumerRegistry<Integer> spatialDimensionEventBus;
    @Resource(name = ModelBusConfig.BOUNDARIES_BUS)
    ConsumerRegistry<List<? extends Segment>> allBoundariesEventBus;
    @Resource(name = ModelBusConfig.MODEL_INPUTED_BUS)
    ConsumerRegistry<Object> modelInputtedEventBus;

    @Bean(name = SearcherBaseConfig.BOUNDARIES_RANGE_SEARCHER_PROTO)
    public LayeredRangeTree<double[], Segment> boundariesRangeSearcherProto() {
        return boundariesLRTreeBuilder().getLRTree();
    }

    @Bean
    CoordKeyLRTreeBuilder<Segment> boundariesLRTreeBuilder() {
        CoordKeyLRTreeBuilder<Segment> result = new CoordKeyLRTreeBuilder<>();
        result.setCoordPicker(new Segment2DChordCenterPicker());

        spatialDimensionEventBus.register(result::setSpatialDimension);
        allBoundariesEventBus.register(result::setDatas);
        modelInputtedEventBus.register(result::prepareTree);
        return result;
    }
}
