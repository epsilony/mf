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
package net.epsilony.mf.model.influence.config;

import net.epsilony.mf.model.influence.EnsureNodesNum;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.mf.model.support_domain.config.SupportDomainBaseConfig;
import net.epsilony.mf.util.bus.ConsumerBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */

@Configuration
@Import(InfluenceBaseConfig.class)
public class EnsureNodesNumConfig extends ApplicationContextAwareImpl {

    public static final String ENSURE_NODES_NUM_INIT_RADIUS_BUS = "ensureNodesNumInitRadiusBus";
    public static final String ENSURE_NODES_NUM_LOWER_BOUND_BUS = "ensureNodesLowerBoundBus";

    @Bean(name = InfluenceBaseConfig.INFLUENCE_RADIUS_CALCULATOR_PROTO)
    @Scope("prototype")
    public EnsureNodesNum influenceRadiusCalculatorPrototype() {
        return ensureNodesNumPrototype();
    }

    @Bean(name = ENSURE_NODES_NUM_INIT_RADIUS_BUS)
    public ConsumerBus<Double> ensureNodesNumInitRadiusBus() {
        return new ConsumerBus<>();
    }

    @Bean(name = ENSURE_NODES_NUM_LOWER_BOUND_BUS)
    public ConsumerBus<Integer> ensureNodesNumLowerBoundBus() {
        return new ConsumerBus<>();
    }

    @Bean
    @Scope("prototype")
    public EnsureNodesNum ensureNodesNumPrototype() {
        EnsureNodesNum ensureNodesNum = new EnsureNodesNum();
        SupportDomainSearcher supportDomainSearcher = applicationContext.getBean(
                SupportDomainBaseConfig.SUPPORT_DOMAIN_SEARCHER_PROTO, SupportDomainSearcher.class);
        ensureNodesNum.setSupportDomainSearcher(supportDomainSearcher);
        ensureNodesNumInitRadiusBus().register(ensureNodesNum::setInitSearchRad);
        ensureNodesNumLowerBoundBus().register(ensureNodesNum::setNodesNumLowerBound);
        return ensureNodesNum;
    }

}
