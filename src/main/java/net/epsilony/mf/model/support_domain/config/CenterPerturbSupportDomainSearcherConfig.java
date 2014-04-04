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
import net.epsilony.mf.model.search.config.SearcherBaseConfig;
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

    @Bean(name = SupportDomainBaseConfig.SUPPORT_DOMAIN_SEARCHER_PROTO)
    @Scope("prototype")
    public CenterPerturbSupportDomainSearcher2D supportDomainSearcherProto() {
        @SuppressWarnings("unchecked")
        MetricSearcher<? extends Segment> segmentSearcher = (MetricSearcher<? extends Segment>) applicationContext
                .getBean(SearcherBaseConfig.BOUNDARIES_SEARCHER_PROTO);
        @SuppressWarnings("unchecked")
        MetricSearcher<? extends MFNode> nodesSearcher = (MetricSearcher<? extends MFNode>) applicationContext
                .getBean(SearcherBaseConfig.NODES_SEARCHER_PROTO);
        CenterPerturbSupportDomainSearcher2D result = new CenterPerturbSupportDomainSearcher2D(nodesSearcher,
                segmentSearcher);
        return result;
    }

    @Bean(name = SupportDomainBaseConfig.INFLUENCED_SUPPORT_DOMAIN_SEARCHER_PROTO)
    @Scope("prototype")
    public CenterPerturbSupportDomainSearcher2D influenceSupportDomainSearcherProto() {
        @SuppressWarnings("unchecked")
        MetricSearcher<? extends Segment> segmentSearcher = (MetricSearcher<? extends Segment>) applicationContext
                .getBean(SearcherBaseConfig.BOUNDARIES_SEARCHER_PROTO);
        @SuppressWarnings("unchecked")
        MetricSearcher<? extends MFNode> nodesSearcher = (MetricSearcher<? extends MFNode>) applicationContext
                .getBean(SearcherBaseConfig.INFLUENCED_NODES_SEARCHER_PROTO);
        CenterPerturbSupportDomainSearcher2D result = new CenterPerturbSupportDomainSearcher2D(nodesSearcher,
                segmentSearcher);
        return result;
    }

}
