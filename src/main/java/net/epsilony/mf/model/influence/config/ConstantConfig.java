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

import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.mf.model.support_domain.config.SupportDomainBaseConfig;
import net.epsilony.mf.util.bus.ConsumerBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
@Import(InfluenceBaseConfig.class)
public class ConstantConfig extends ApplicationContextAwareImpl {
    public static final String CONSTANT_INFLUCENCE_RADIUS_BUS = "constantInfluenceRadiusBus";

    @Bean(name = CONSTANT_INFLUCENCE_RADIUS_BUS)
    public ConsumerBus<Double> constantInfluenceRadiusBus() {
        return new ConsumerBus<>();
    }

    @Bean(name = InfluenceBaseConfig.INFLUENCE_RADIUS_CALCULATOR_PROTO)
    public ConstantInfluenceRadiusCalculator influenceRadiusCalculatorProto() {
        ConstantInfluenceRadiusCalculator result = new ConstantInfluenceRadiusCalculator();
        result.setSupportDomainSearcher(applicationContext.getBean(
                SupportDomainBaseConfig.SUPPORT_DOMAIN_SEARCHER_PROTO, SupportDomainSearcher.class));
        constantInfluenceRadiusBus().register(result::setRad);
        return result;
    }
}
