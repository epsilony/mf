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

import net.epsilony.mf.shape_func.MLS;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;
import net.epsilony.tb.common_func.BasesFunction;
import net.epsilony.tb.common_func.RadialBasis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class MLSConfig extends ApplicationContextAwareImpl {

    @Bean(name = ShapeFunctionBaseConfig.SHAPE_FUNCTION_PROTO)
    @Scope("prototype")
    public MLS shapeFunctionProto() {
        MLS result = new MLS();
        if (applicationContext.containsBean(ShapeFunctionBaseConfig.WEIGHT_FUNCTION_PROTO)) {
            RadialBasis weightFunc = applicationContext.getBean(ShapeFunctionBaseConfig.WEIGHT_FUNCTION_PROTO,
                    RadialBasis.class);
            result.setWeightFunc(weightFunc);
        }
        if (applicationContext.containsBean(ShapeFunctionBaseConfig.BASES_FUNCTION_PROTO)) {
            BasesFunction basesFunction = applicationContext.getBean(ShapeFunctionBaseConfig.BASES_FUNCTION_PROTO,
                    BasesFunction.class);
            result.setBasesFunc(basesFunction);
        }
        return result;
    }
}
