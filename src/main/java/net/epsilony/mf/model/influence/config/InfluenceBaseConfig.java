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
package net.epsilony.mf.model.influence.config;

import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.OneDInfluenceRadiusProcesser;
import net.epsilony.mf.model.influence.TwoDInfluenceRadiusProcessor;
import net.epsilony.mf.util.parm.MFParmContainer;
import net.epsilony.mf.util.parm.RelayParmContainerBuilder;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class InfluenceBaseConfig extends ApplicationContextAwareImpl {
    // need to be configed------------
    public static final String INFLUENCE_RADIUS_CALCULATOR_PROTO = "influenceRadiusCalculatorProto";

    // end of

    @Bean
    public MFParmContainer influenceBaseParmContainer() {
        return new RelayParmContainerBuilder().addParms("spatialDimension", "nodes", "spaceNodes", "boundaries").get();
    }

    public static final String INFLUENCE_PROCESSOR = "influenceProcessor";

    @Bean(name = INFLUENCE_PROCESSOR)
    public Runnable influenceProcessor() {
        SpatialDemandRunnable result = new SpatialDemandRunnable();
        result.setRunnables(new Runnable[] { oneDInfluenceRadiusProcessor(), twoDInfluenceRadiusProcessor() });
        influenceBaseParmContainer().autoRegister(result);
        return result;
    }

    @Bean
    OneDInfluenceRadiusProcesser oneDInfluenceRadiusProcessor() {
        OneDInfluenceRadiusProcesser result = new OneDInfluenceRadiusProcesser();
        result.setInfluenceRadiusCalculator(getInfluenceCalculatorProto());
        influenceBaseParmContainer().autoRegister(result);
        return result;
    }

    @Bean
    TwoDInfluenceRadiusProcessor twoDInfluenceRadiusProcessor() {
        TwoDInfluenceRadiusProcessor result = new TwoDInfluenceRadiusProcessor();
        result.setInfluenceRadiusCalculator(getInfluenceCalculatorProto());
        influenceBaseParmContainer().autoRegister(result);
        return result;
    }

    private InfluenceRadiusCalculator getInfluenceCalculatorProto() {
        return applicationContext.getBean(INFLUENCE_RADIUS_CALCULATOR_PROTO, InfluenceRadiusCalculator.class);
    }

    public static class SpatialDemandRunnable implements Runnable {
        Runnable[] runnables;
        int        spatialDimension;

        public void setRunnables(Runnable[] runnables) {
            this.runnables = runnables;
        }

        public void setSpatialDimension(int spatialDimension) {
            this.spatialDimension = spatialDimension;
        }

        @Override
        public void run() {
            runnables[spatialDimension - 1].run();
        }
    }
}
