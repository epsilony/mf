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
package net.epsilony.mf.integrate.integrator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class ThreeStageIntegralConfig extends IntegralBaseConfig {

    // end of optional requirements
    @Bean(name = INTEGRAL_COLLECTION_PROTO)
    @Scope("prototype")
    public ThreeStageIntegralCollection integralCollectionProto() {
        ThreeStageIntegralCollection result = new ThreeStageIntegralCollection(unitToGeomQuadraturePointsGroupProto(),
                pointToAssemblyInputGroupProto(), assemblerIntegratorsGroupProto());
        integralGroupCollections().add(result);
        return result;
    }

}
