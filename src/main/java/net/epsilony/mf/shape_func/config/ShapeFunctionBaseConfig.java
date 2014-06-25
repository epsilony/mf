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
package net.epsilony.mf.shape_func.config;

import net.epsilony.mf.shape_func.bases.MFMonomialBasesFactory;
import net.epsilony.mf.util.parm.MFParmContainer;
import net.epsilony.mf.util.parm.RelayParmContainerBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class ShapeFunctionBaseConfig {

    // required beans
    public static final String SHAPE_FUNCTION_PROTO  = "shapeFunctionProto";
    // optional beans
    public static final String BASES_FUNCTION_PROTO  = "shapeFunctionBasesFunctionPrototype";
    public static final String WEIGHT_FUNCTION_PROTO = "shapeFunctionWeightFunctionPrototype"; ;

    //

    @Bean
    public MFParmContainer shapeFunctionBaseParmContainer() {
        return new RelayParmContainerBuilder().addParms("spatialDimension", "monomialDegree").get();
    }

    public static final String MONOMIAL_BASES_FACTORY = "monomialBasesFactory";

    @Bean(name = MONOMIAL_BASES_FACTORY)
    public MFMonomialBasesFactory monomialBasesFactory() {
        MFMonomialBasesFactory factory = new MFMonomialBasesFactory();
        factory.setMonomialDegree(2);

        shapeFunctionBaseParmContainer().autoRegister(factory);
        return factory;
    }
}
