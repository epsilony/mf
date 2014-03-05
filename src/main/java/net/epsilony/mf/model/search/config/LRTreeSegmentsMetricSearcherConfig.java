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

import net.epsilony.mf.model.search.MaxSegmentLengthEnlargeRangeGenerator;
import net.epsilony.mf.model.search.RangeBasedMetricSearcher;
import net.epsilony.mf.model.search.Segment2DChordCenterPicker;
import net.epsilony.mf.model.search.Segment2DMetricFilter;
import net.epsilony.mf.util.event.MethodEventBus;
import net.epsilony.tb.rangesearch.LayeredRangeTree;
import net.epsilony.tb.solid.Segment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class LRTreeSegmentsMetricSearcherConfig {
    @Resource
    MethodEventBus spatialDimensionEventBus;

    @Resource
    MethodEventBus modelInputtedEventBus;

    @Resource
    MethodEventBus allBoundariesEventBus;

    @Bean
    @Scope("prototype")
    public RangeBasedMetricSearcher<Segment> allSegmentsMetricSearcherPrototype() {
        RangeBasedMetricSearcher<Segment> result = new RangeBasedMetricSearcher<>();
        result.setRangeSearcher(allBoundariesRangeSearcher());
        result.setRangeGenerator(segmentRangeGenerator());
        result.setMetricFilter(segmentMetricFilter());
        return result;

    }

    @Bean
    public LayeredRangeTree<double[], Segment> allBoundariesRangeSearcher() {
        return allBoundariesLRTreeBuilder().getLRTree();
    }

    @Bean
    public CoordKeyLRTreeBuilder<Segment> allBoundariesLRTreeBuilder() {
        CoordKeyLRTreeBuilder<Segment> result = new CoordKeyLRTreeBuilder<>();
        result.setCoordPicker(new Segment2DChordCenterPicker());
        return result;
    }

    @Bean
    public boolean phonyRegistyAllBoundariesLRTreeBuilderToEventBuses() {
        CoordKeyLRTreeBuilder<Segment> allBoundariesLRTreeBuilder = allBoundariesLRTreeBuilder();
        spatialDimensionEventBus.registry(allBoundariesLRTreeBuilder, "setSpatialDimension", types(int.class));
        allBoundariesEventBus.registry(allBoundariesLRTreeBuilder, "setDatas", types(Collection.class));
        modelInputtedEventBus.registry(allBoundariesLRTreeBuilder, "prepareTree", types());
        return true;
    }

    @Bean
    public Segment2DChordCenterPicker segment2DChordCenterPicker() {
        return new Segment2DChordCenterPicker();
    }

    @Bean
    public MaxSegmentLengthEnlargeRangeGenerator segmentRangeGenerator() {
        return new MaxSegmentLengthEnlargeRangeGenerator();
    }

    @Bean
    public boolean phonyRegistryMaxSegmentLengthEnlargeRangeGeneratorToAllBoundariesEventBus() {
        allBoundariesEventBus.registry(segmentRangeGenerator(), "setEnlargement", types(Iterable.class));
        return true;
    }

    @Bean
    public Segment2DMetricFilter segmentMetricFilter() {
        return new Segment2DMetricFilter();
    }

}
