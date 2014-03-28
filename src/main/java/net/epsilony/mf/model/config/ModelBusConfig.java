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

import javax.swing.text.Segment;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.util.bus.ConsumerBus;
import net.epsilony.tb.solid.GeomUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class ModelBusConfig {

    public static final String SPATIAL_DIMENSION_BUS = "spatialDimensionBus";
    public static final String VALUE_DIMENSION_BUS = "valueDimensionBus";
    public static final String NODES_BUS = "nodesBus";
    public static final String SPACE_NODES_BUS = "spaceNodesBus";
    public static final String BOUNDARIES_BUS = "boundariesBus";
    public static final String LOAD_MAP_BUS = "loadMapBus";
    public static final String MODEL_INPUTED_BUS = "modelInputedBus";

    @Bean(name = SPATIAL_DIMENSION_BUS)
    public ConsumerBus<Integer> spatialDimensionBus() {
        return new ConsumerBus<>();
    }

    @Bean(name = VALUE_DIMENSION_BUS)
    public ConsumerBus<Integer> valueDimensionBus() {
        return new ConsumerBus<>();
    }

    @Bean(name = NODES_BUS)
    public ConsumerBus<List<? extends MFNode>> nodesBus() {
        return new ConsumerBus<>();
    }

    @Bean(name = SPACE_NODES_BUS)
    public ConsumerBus<List<? extends MFNode>> spaceNodesBus() {
        return new ConsumerBus<>();
    }

    @Bean(name = BOUNDARIES_BUS)
    public ConsumerBus<List<? extends Segment>> boundariesBus() {
        return new ConsumerBus<>();
    }

    @Bean(name = LOAD_MAP_BUS)
    public ConsumerBus<Map<GeomUnit, GeomPointLoad>> loadMapBus() {
        return new ConsumerBus<>();
    }

    @Bean(name = MODEL_INPUTED_BUS)
    public ConsumerBus<Object> modelInputedBus() {
        return new ConsumerBus<>();
    }

}
