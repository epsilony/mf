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
package net.epsilony.mf.model.convertor;

import java.util.ArrayList;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import java.util.function.Function;
import net.epsilony.mf.util.convertor.GridInnerPicker;
import net.epsilony.mf.util.convertor.IterableOutputConcator;
import net.epsilony.mf.util.convertor.OneManyOneOneLink;
import net.epsilony.mf.util.convertor.OneOneLink;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * need to supplement {@code rectangleToSpaceNodesGridsClosure}
 * </p>
 * 
 * @author Man YUAN <epsilon@epsilony.net>
 */
@Configuration
public class RectangleSpaceNodesGeneratorConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    public Function<MFRectangle, Iterable<MFNode>> rectangleToSpaceNodes() {
        OneManyOneOneLink<MFRectangle, double[], MFNode> linkCoordNode = new OneManyOneOneLink<>(
                rectangleToSpaceNodesCoords(), new CoordToNode<>(MFNode.class));
        return linkCoordNode;
    }

    @Bean
    public Function<MFRectangle, Iterable<double[]>> rectangleToSpaceNodesCoords() {
        @SuppressWarnings("unchecked")
        Function<? super MFRectangle, ? extends ArrayList<? extends ArrayList<double[]>>> rectangleToSpaceNodesGridsClosure = (Function<? super MFRectangle, ? extends ArrayList<? extends ArrayList<double[]>>>) applicationContext
                .getBean("rectangleToSpaceNodesGridsClosure");
        GridInnerPicker<double[]> gridInnerPicker = new GridInnerPicker<>();
        OneOneLink<MFRectangle, ArrayList<? extends ArrayList<double[]>>, ArrayList<ArrayList<double[]>>> innerPicked = new OneOneLink<>(
                rectangleToSpaceNodesGridsClosure, gridInnerPicker);
        return new IterableOutputConcator<>(innerPicked);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
