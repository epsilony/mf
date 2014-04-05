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
import net.epsilony.mf.model.search.MaxSegmentLengthEnlargeRangeGenerator;
import net.epsilony.mf.model.search.RangeBasedMetricSearcher;
import net.epsilony.mf.model.search.Segment2DMetricFilter;
import net.epsilony.mf.util.bus.BiConsumerRegistry;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;
import net.epsilony.tb.rangesearch.RangeSearcher;
import net.epsilony.tb.solid.Segment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class TwoDBoundariesSearcherConfig extends ApplicationContextAwareImpl {

    @Resource(name = ModelBusConfig.BOUNDARIES_BUS)
    BiConsumerRegistry<List<? extends Segment>> allBoundariesEventBus;

    @Bean(name = SearcherBaseConfig.BOUNDARIES_SEARCHER_PROTO)
    @Scope("prototype")
    public RangeBasedMetricSearcher<Segment> boundariesSearcherProto() {
        RangeBasedMetricSearcher<Segment> result = new RangeBasedMetricSearcher<>();
        result.setRangeSearcher(getBoundariesRangeSearcherProto());

        MaxSegmentLengthEnlargeRangeGenerator maxSegmentLengthEnlargeRangeGenerator = new MaxSegmentLengthEnlargeRangeGenerator();
        allBoundariesEventBus.register(MaxSegmentLengthEnlargeRangeGenerator::setEnlargement,
                maxSegmentLengthEnlargeRangeGenerator);
        result.setRangeGenerator(maxSegmentLengthEnlargeRangeGenerator);

        result.setMetricFilter(new Segment2DMetricFilter());
        return result;

    }

    @SuppressWarnings("unchecked")
    private RangeSearcher<double[], ? extends Segment> getBoundariesRangeSearcherProto() {
        return (RangeSearcher<double[], ? extends Segment>) applicationContext
                .getBean(SearcherBaseConfig.BOUNDARIES_RANGE_SEARCHER_PROTO);
    }

}
