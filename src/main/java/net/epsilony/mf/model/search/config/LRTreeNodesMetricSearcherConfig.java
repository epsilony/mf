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

import javax.annotation.Resource;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.search.EnlargeRangeGenerator;
import net.epsilony.mf.model.search.InRadiusPickerFilter;
import net.epsilony.mf.model.search.MetricFilter;
import net.epsilony.mf.model.search.NodeCoordPicker;
import net.epsilony.mf.model.search.RangeBasedMetricSearcher;
import net.epsilony.mf.util.event.AbstractMethodEventBus;
import net.epsilony.tb.DoubleArrayComparator;
import net.epsilony.tb.rangesearch.LayeredRangeTree;
import net.epsilony.tb.solid.Node;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class LRTreeNodesMetricSearcherConfig {

    @Resource
    int spatialDimension;
    @Resource
    AbstractMethodEventBus allNodesEventBus;

    @Bean
    @Scope("prototype")
    public RangeBasedMetricSearcher<MFNode> allNodesMetricSearcherPrototype() {
        RangeBasedMetricSearcher<MFNode> rangeBasedMetricSearcher = new RangeBasedMetricSearcher<>();
        rangeBasedMetricSearcher.setRangeGenerator(nodeRangeGenerator());
        rangeBasedMetricSearcher.setRangeSearcher(allNodesRangeSearcher());
        rangeBasedMetricSearcher.setMetricFilter(nodesMetricFilter());
        return rangeBasedMetricSearcher;
    }

    @Bean
    public EnlargeRangeGenerator nodeRangeGenerator() {
        return new EnlargeRangeGenerator();
    }

    @Bean
    public LayeredRangeTree<double[], MFNode> allNodesRangeSearcher() {
        LayeredRangeTree<double[], MFNode> layeredRangeTree = new LayeredRangeTree<>();
        layeredRangeTree.setComparators(DoubleArrayComparator.comparatorsForAll(spatialDimension));
        return layeredRangeTree;
    }

    @Bean
    public boolean phonyRegistryLRTreeToAllNodesLRTreeValueEventBus() {
        allNodesLRTreeValuesEventBus().registry(allNodesRangeSearcher());
        return true;
    }

    @Bean
    public LRTreePickerBasedValuesEventBus<MFNode> allNodesLRTreeValuesEventBus() {
        LRTreePickerBasedValuesEventBus<MFNode> result = new LRTreePickerBasedValuesEventBus<>(nodeCoordPicker());
        return result;
    }

    @Bean
    public NodeCoordPicker nodeCoordPicker() {
        return new NodeCoordPicker();
    }

    @Bean
    public boolean phonyRegistryAllNodesLRTreeValueEventBusToUpperEventBus() {
        allNodesEventBus.registrySubEventBus(allNodesLRTreeValuesEventBus());
        return true;
    }

    @Bean
    public MetricFilter<Node> nodesMetricFilter() {
        return new InRadiusPickerFilter<Node>(nodeCoordPicker());
    }
}
