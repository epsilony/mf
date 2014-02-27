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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class NodesMetricCommonConfig extends ApplicationContextAwareImpl {

    public final String nodeRangeSearcherBeanName = "allNodesRangeSearcher";

    @Bean
    @Scope("prototype")
    public RangeBasedMetricSearcher<MFNode> allNodesMetricSearcherPrototype() {
        RangeBasedMetricSearcher<MFNode> rangeBasedMetricSearcher = new RangeBasedMetricSearcher<>();
        rangeBasedMetricSearcher.setRangeGenerator(new EnlargeRangeGenerator());
        @SuppressWarnings("unchecked")
        RangeSearcher<double[], ? extends MFNode> allNodesRangeSearcher = (RangeSearcher<double[], ? extends MFNode>) applicationContext
                .getBean(nodeRangeSearcherBeanName);
        rangeBasedMetricSearcher.setRangeSearcher(allNodesRangeSearcher);
        rangeBasedMetricSearcher.setMetricFilter(nodesMetricFilter());
        return rangeBasedMetricSearcher;
    }

    @Bean
    @Scope("prototype")
    public RangeBasedMetricSearcher<MFNode> allNodesInsideInfluenceMetricSearcherPrototype() {
        RangeBasedMetricSearcher<MFNode> rangeBasedMetricSearcher = new RangeBasedMetricSearcher<>();
        rangeBasedMetricSearcher.setRangeGenerator(new EnlargeRangeGenerator());
        @SuppressWarnings("unchecked")
        RangeSearcher<double[], ? extends MFNode> allNodesRangeSearcher = (RangeSearcher<double[], ? extends MFNode>) applicationContext
                .getBean(nodeRangeSearcherBeanName);
        rangeBasedMetricSearcher.setRangeSearcher(allNodesRangeSearcher);
        rangeBasedMetricSearcher.setMetricFilter(insideInfluenceNodesMetricFilter());
        return rangeBasedMetricSearcher;
    }

    @Bean
    public InRadiusPickerFilter<Node> nodesMetricFilter() {
        return new InRadiusPickerFilter<Node>(new NodeCoordPicker());
    }

    @Bean
    InsideInfluencePickerFilter insideInfluenceNodesMetricFilter() {
        return new InsideInfluencePickerFilter();
    }
}
