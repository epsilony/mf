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
package net.epsilony.mf.integrate.integrator.vc.config;

import java.util.function.IntFunction;

import net.epsilony.mf.integrate.integrator.config.IntegratorsGroup;
import net.epsilony.mf.integrate.integrator.vc.HeavisideQuadricTransDomainBases2D;
import net.epsilony.mf.integrate.integrator.vc.QuadricVCNode2D;
import net.epsilony.mf.integrate.integrator.vc.VCNode;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class QuadricVCConfig extends VCIntegratorBaseConfig {
    @Bean(name = VCIntegratorBaseConfig.VC_INTEGRATORS_GROUP_PROTO)
    @Scope("prototype")
    public IntegratorsGroup vcIntegratorsGroupProto() {
        return twodVCIntegratorsGroupProto();
    }

    @Bean(name = VCIntegratorBaseConfig.VC_TRANS_DOMAIN_BASES_FUNCTION_PROTO)
    @Scope("prototype")
    public HeavisideQuadricTransDomainBases2D tranDomainBasesFunctionPrototype() {
        HeavisideQuadricTransDomainBases2D result = new HeavisideQuadricTransDomainBases2D();
        return result;
    }

    @Bean(name = VCIntegratorBaseConfig.VC_INTEGRAL_NODE_FACTORY)
    public IntFunction<VCNode> vcNodeFactory() {
        return QuadricVCNode2D::new;
    }
}
