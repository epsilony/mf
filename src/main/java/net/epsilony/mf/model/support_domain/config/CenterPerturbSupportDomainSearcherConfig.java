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
package net.epsilony.mf.model.support_domain.config;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.search.MetricSearcher;
import net.epsilony.mf.model.support_domain.CenterPerturbSupportDomainSearcher2D;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;
import net.epsilony.tb.solid.Segment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class CenterPerturbSupportDomainSearcherConfig extends ApplicationContextAwareImpl {
    public final String nodesSearcherBeanName = "allNodesMetricSearcherPrototype";
    public final String insideInfluenceNodesSearcherBeanName = "allNodesInsideInfluenceMetricSearcherPrototype";
    public final String segmentsSearcherBeanName = "allSegmentsMetricSearcherPrototype";

    @Bean
    @Scope("prototype")
    public CenterPerturbSupportDomainSearcher2D supportDomainSearcherPrototype() {
        @SuppressWarnings("unchecked")
        MetricSearcher<? extends Segment> segmentSearcher = (MetricSearcher<? extends Segment>) applicationContext
                .getBean(segmentsSearcherBeanName);
        @SuppressWarnings("unchecked")
        MetricSearcher<? extends MFNode> nodesSearcher = (MetricSearcher<? extends MFNode>) applicationContext
                .getBean(nodesSearcherBeanName);
        CenterPerturbSupportDomainSearcher2D result = new CenterPerturbSupportDomainSearcher2D(nodesSearcher,
                segmentSearcher);
        return result;
    }

    @Bean
    @Scope("prototype")
    public CenterPerturbSupportDomainSearcher2D insideInfluenceSupportDomainSearcherPrototype() {
        @SuppressWarnings("unchecked")
        MetricSearcher<? extends Segment> segmentSearcher = (MetricSearcher<? extends Segment>) applicationContext
                .getBean(segmentsSearcherBeanName);
        @SuppressWarnings("unchecked")
        MetricSearcher<? extends MFNode> nodesSearcher = (MetricSearcher<? extends MFNode>) applicationContext
                .getBean(insideInfluenceNodesSearcherBeanName);
        CenterPerturbSupportDomainSearcher2D result = new CenterPerturbSupportDomainSearcher2D(nodesSearcher,
                segmentSearcher);
        return result;
    }

}
