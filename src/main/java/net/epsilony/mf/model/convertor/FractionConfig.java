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

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.util.spring.ContextTools;
import net.epsilony.tb.RudeFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class FractionConfig implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private ApplicationContext applicationContext;

    @Bean
    public SingleLineFractionizer.ByUndisturbedNeighbourCoordsDistanceSup singleLineFractionizer() {
        return new SingleLineFractionizer.ByUndisturbedNeighbourCoordsDistanceSup(
                (Double) applicationContext.getBean("neighborCoordDistanceSup"));
    }

    @Bean
    public ChainFractionizer<MFNode> chainFractionizer() {
        return new ChainFractionizer<>(singleLineFractionizer(), new RudeFactory<>(MFNode.class));
    }

    @Bean
    public FacetModelFractionizer facetModelFractionizer() {
        return FacetModelFractionizer.newInstance(chainFractionizer());
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();) {
            applicationContext.register(FractionConfig.class);
            applicationContext.registerBeanDefinition("neighborCoordDistanceSup",
                    ContextTools.definition(Double.class, 3.0));
            applicationContext.refresh();
            System.out.println(applicationContext.getBean(FacetModelFractionizer.class));
        }
    }

}
