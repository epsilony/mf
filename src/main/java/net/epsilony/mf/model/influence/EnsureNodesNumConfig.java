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
package net.epsilony.mf.model.influence;

import static net.epsilony.mf.util.event.EventBuses.types;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.mf.util.event.MethodEventBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */

@Configuration
public class EnsureNodesNumConfig extends ApplicationContextAwareImpl {
    public final String supportDomainSearcherBeanName = "supportDomainSearcherPrototype";

    @Bean
    public MethodEventBus ensureNodesNumInitRadiusEventBus() {
        return new MethodEventBus();
    }

    @Bean
    public MethodEventBus ensureNodesNumLowerBoundEventBus() {
        return new MethodEventBus();
    }

    @Bean
    @Scope("prototype")
    public EnsureNodesNum ensureNodesNumPrototype() {
        EnsureNodesNum ensureNodesNum = new EnsureNodesNum();
        SupportDomainSearcher supportDomainSearcher = applicationContext.getBean(supportDomainSearcherBeanName,
                SupportDomainSearcher.class);
        ensureNodesNum.setSupportDomainSearcher(supportDomainSearcher);
        ensureNodesNumInitRadiusEventBus().registry(ensureNodesNum, "setInitSearchRad", types(double.class));
        ensureNodesNumLowerBoundEventBus().registry(ensureNodesNum, "setNodesNumLowerBound", types(int.class));
        return ensureNodesNum;
    }

    @Bean
    @Scope("prototype")
    public EnsureNodesNum influenceRadiusCalculatorPrototype() {
        return ensureNodesNumPrototype();
    }

}
