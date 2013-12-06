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
package net.epsilony.mf.sample.factory;

import java.util.List;

import javax.annotation.Resource;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.assembler.AssemblersConf;
import net.epsilony.mf.process.integrate.MFIntegralProcessorConf;
import net.epsilony.mf.process.integrate.aspect.SimpIntegralCounter;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.Factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableAspectJAutoProxy
@Import({ AssemblersConf.class, MFIntegralProcessorConf.class })
public class ProcessContextConf {

    @Bean
    List<InfluenceRadiusCalculator> influenceRadiusCalculatorHolder() {
        return null;
    }

    @Bean
    public InfluenceRadiusCalculator influenceRadiusCalculator() {
        return influenceRadiusCalculatorHolder().get(0);
    }

    @Bean
    List<AnalysisModel> analysisModelHolder() {
        return null;
    }

    @Bean
    public AnalysisModel analysisModel() {
        return analysisModelHolder().get(0);
    }

    @Resource(name = "threadNumHolder")
    List<Integer> threadNumHolder;

    @Bean
    public Integer threadNum() {
        return threadNumHolder.get(0);
    }

    @Bean
    public SimpIntegralCounter simpIntegralCounter() {
        return new SimpIntegralCounter();
    }

    @Bean
    public int integralDegree() {
        return integralDegreeHolder().get(0);
    }

    @Bean
    List<Integer> integralDegreeHolder() {
        return null;
    }

    @Bean
    public MFSolver mainMatrixSolver() {
        return new RcmSolver();
    }

    @Bean
    public Factory<MFShapeFunction> shapeFunctionFactory() {
        return new Factory<MFShapeFunction>() {

            @Override
            public MFShapeFunction produce() {
                return shapeFunction();
            }
        };
    }

    @Bean
    @Scope("prototype")
    public MFShapeFunction shapeFunction() {
        return new MLS();
    }

}