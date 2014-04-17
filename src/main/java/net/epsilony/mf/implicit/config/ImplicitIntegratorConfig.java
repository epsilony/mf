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
package net.epsilony.mf.implicit.config;

import net.epsilony.mf.integrate.integrator.config.IntegratorBaseConfig;
import net.epsilony.mf.integrate.integrator.config.IntegratorsGroup;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
@Import(IntegratorBaseConfig.class)
public class ImplicitIntegratorConfig extends ApplicationContextAwareImpl {

    @Bean
    @Scope("prototype")
    public IntegratorsGroup integratorsGroupProto() {
        return applicationContext.getBean(IntegratorBaseConfig.NON_LAGRANGLE_INTEGRATORS_GROUP_PROTO,
                IntegratorsGroup.class);
    }
}
