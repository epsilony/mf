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
package net.epsilony.mf.process;

import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class MixerConfig extends ApplicationContextAwareImpl {

    public static final String SHAPE_FUNCTION_PROTO_NAME = "shapeFunctionPrototype";
    public static final String SUPPORT_DOMAIN_SEARCHER_PROTO_NAME = "supportDomainSearcherPrototype";

    @Bean
    @Scope("prototype")
    public Mixer mixerPrototype() {
        Mixer result = new Mixer();
        result.setShapeFunction(applicationContext.getBean(SHAPE_FUNCTION_PROTO_NAME, MFShapeFunction.class));
        result.setSupportDomainSearcher(applicationContext.getBean(SUPPORT_DOMAIN_SEARCHER_PROTO_NAME,
                SupportDomainSearcher.class));
        return result;
    }
}
