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

import java.util.Map;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.indexer.NodesAssembleIndexer;
import net.epsilony.mf.process.indexer.TwoDFacetLagrangleNodesAssembleIndexer;
import net.epsilony.mf.process.integrate.MFIntegralProcessor;
import net.epsilony.mf.process.integrate.aspect.SimpIntegralCounter;
import net.epsilony.mf.process.integrate.core.twod.TwoDCoreConf;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.tb.Factory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */

@Configuration
@Import({ TwoDCoreConf.class })
public class TwoDPoissonConf implements ApplicationContextAware {
    @SuppressWarnings("unchecked")
    @Bean
    public MFLinearProcessor mflinearProcessor() {
        MFLinearProcessor processor = new MFLinearProcessor();

        processor.setAnalysisModel(applicationContext.getBean("analysisModel", AnalysisModel.class));
        processor.setAssemblersGroupFactory(assemblersGroupFactory());
        processor.setInfluenceRadiusCalculator(applicationContext.getBean("influenceRadiusCalculator",
                InfluenceRadiusCalculator.class));
        processor.setIntegralProcessor(applicationContext.getBean("mfintegralProcessor", MFIntegralProcessor.class));
        processor.setMainMatrixSolver(applicationContext.getBean("mainMatrixSolver", MFSolver.class));
        processor.setNodesAssembleIndexer(nodesAssembleIndexer());
        processor
                .setShapeFunctionFactory((Factory<MFShapeFunction>) applicationContext.getBean("shapeFunctionFactory"));

        return processor;
    }

    @Bean
    public Factory<Map<AssemblerType, Assembler>> assemblersGroupFactory() {
        return new Factory<Map<AssemblerType, Assembler>>() {

            @Override
            public Map<AssemblerType, Assembler> produce() {
                return assemblersGroup();
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Bean
    @Scope("prototype")
    public Map<AssemblerType, Assembler> assemblersGroup() {
        return (Map<AssemblerType, Assembler>) applicationContext.getBean("poissonAssemblersGroup");
    }

    @Bean
    public SimpIntegralCounter simpIntegralCounter() {
        return new SimpIntegralCounter();
    }

    private ApplicationContext applicationContext;

    @Bean
    public NodesAssembleIndexer nodesAssembleIndexer() {
        return new TwoDFacetLagrangleNodesAssembleIndexer();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
