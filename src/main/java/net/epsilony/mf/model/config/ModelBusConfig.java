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

import java.util.List;
import java.util.Map;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.util.bus.WeakBus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class ModelBusConfig {

    public static final String SPATIAL_DIMENSION_BUS = "spatialDimensionBus";
    public static final String VALUE_DIMENSION_BUS   = "valueDimensionBus";
    public static final String NODES_BUS             = "nodesBus";
    public static final String SPACE_NODES_BUS       = "spaceNodesBus";
    public static final String BOUNDARIES_BUS        = "boundariesBus";
    public static final String LOAD_MAP_BUS          = "loadMapBus";
    public static final String MODEL_INPUTED_BUS     = "modelInputedBus";

    @Bean(name = SPATIAL_DIMENSION_BUS)
    public WeakBus<Integer> spatialDimensionBus() {
        return new WeakBus<>(SPATIAL_DIMENSION_BUS);
    }

    @Bean(name = VALUE_DIMENSION_BUS)
    public WeakBus<Integer> valueDimensionBus() {
        return new WeakBus<>(VALUE_DIMENSION_BUS);
    }

    @Bean(name = NODES_BUS)
    public WeakBus<List<? extends MFNode>> nodesBus() {
        return new WeakBus<>(NODES_BUS);
    }

    @Bean(name = SPACE_NODES_BUS)
    public WeakBus<List<? extends MFNode>> spaceNodesBus() {
        return new WeakBus<>(SPACE_NODES_BUS);
    }

    @Bean(name = BOUNDARIES_BUS)
    public WeakBus<List<? extends MFLine>> boundariesBus() {
        return new WeakBus<>(BOUNDARIES_BUS);
    }

    @Bean(name = LOAD_MAP_BUS)
    public WeakBus<Map<MFGeomUnit, GeomPointLoad>> loadMapBus() {
        return new WeakBus<>(LOAD_MAP_BUS);
    }

    @Bean(name = MODEL_INPUTED_BUS)
    public WeakBus<Object> modelInputedBus() {
        return new WeakBus<>(MODEL_INPUTED_BUS);
    }

}
