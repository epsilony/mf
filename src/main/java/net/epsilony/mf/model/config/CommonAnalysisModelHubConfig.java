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
package net.epsilony.mf.model.config;

import net.epsilony.mf.model.CommonAnalysisModelHub;
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
@Import({ ModelBusConfig.class, LagrangleDirichletNodesBusConfig.class, ConstitutiveLawBusConfig.class })
public class CommonAnalysisModelHubConfig extends ApplicationContextAwareImpl {
    public static final String COMMON_ANALYSIS_MODEL_HUB = "commonAnalysisModelHub";

    @SuppressWarnings("unchecked")
    @Bean
    public CommonAnalysisModelHub commonAnalysisModelHub() {
        CommonAnalysisModelHub result = new CommonAnalysisModelHub();
        result.setBoundariesBus(getBus(ModelBusConfig.BOUNDARIES_BUS));
        result.setConstitutiveLawBus(getBus(ConstitutiveLawBusConfig.CONSTITUTIVE_LAW_BUS));
        result.setLagrangleDirichletNodesBus(getBus(LagrangleDirichletNodesBusConfig.LAGRANGLE_DIRICHLET_NODES_BUS));
        result.setLoadMapBus(getBus(ModelBusConfig.LOAD_MAP_BUS));
        result.setModelInputedBus(getBus(ModelBusConfig.MODEL_INPUTED_BUS));
        result.setNodesBus(getBus(ModelBusConfig.NODES_BUS));
        result.setSpaceNodesBus(getBus(ModelBusConfig.SPACE_NODES_BUS));
        result.setSpatialDimensionBus(getBus(ModelBusConfig.SPATIAL_DIMENSION_BUS));
        result.setValueDimensionBus(getBus(ModelBusConfig.VALUE_DIMENSION_BUS));
        return result;
    }

    private ConsumerBus getBus(String name) {
        return applicationContext.getBean(name, ConsumerBus.class);
    }
}
