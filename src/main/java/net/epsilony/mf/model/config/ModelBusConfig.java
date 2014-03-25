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

import javax.swing.text.Segment;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.util.event.HolderOneOffBus;
import net.epsilony.mf.util.event.OneOffConsumerBus;

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
    public static final String BOUNDARIES_BUS = "boundariesBus";
    public static final String MODEL_INPUTED_BUS = "modelInputedBus";

    @Bean(name = SPATIAL_DIMENSION_BUS)
    public HolderOneOffBus<Integer> spatialDimensionBus() {
        return new HolderOneOffBus<>();
    }

    @Bean(name = VALUE_DIMENSION_BUS)
    public HolderOneOffBus<Integer> valueDimensionBus() {
        return new HolderOneOffBus<>();
    }

    @Bean(name = NODES_BUS)
    public HolderOneOffBus<List<? extends MFNode>> allNodesBus() {
        return new HolderOneOffBus<>();
    }

    @Bean(name = BOUNDARIES_BUS)
    public HolderOneOffBus<List<? extends Segment>> allBoundariesBus() {
        return new HolderOneOffBus<>();
    }

    @Bean(name = MODEL_INPUTED_BUS)
    public OneOffConsumerBus<Object> modelInputedBus() {
        return new OneOffConsumerBus<>();
    }

}
