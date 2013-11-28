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
package net.epsilony.mf.sample;

import java.util.Map;

import javax.annotation.Resource;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblerType;
import net.epsilony.mf.process.assembler.AssemblersConf;
import net.epsilony.mf.process.indexer.NodesAssembleIndexer;
import net.epsilony.mf.process.indexer.OneDChainLagrangleNodesAssembleIndexer;
import net.epsilony.mf.process.integrate.MFIntegralProcessor;
import net.epsilony.mf.process.integrate.MFIntegralProcessorConf;
import net.epsilony.mf.process.integrate.core.oned.OneDCoreConf;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.mf.util.ApplicationContextHolder;
import net.epsilony.mf.util.ApplicationContextHolderConf;
import net.epsilony.tb.Factory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
@Import({ ApplicationContextHolderConf.class, AssemblersConf.class, MFIntegralProcessorConf.class, OneDCoreConf.class })
public class OneDPoissonConf {

    @Bean
    public MFLinearProcessor mflinearProcessor() {
        MFLinearProcessor processor = new MFLinearProcessor();

        processor.setAnalysisModel(analysisModel());
        processor.setAssemblersGroupFactory(assemblersGroupFactory());
        processor.setInfluenceRadiusCalculator(influenceRadiusCalculator());
        processor.setIntegralProcessor(mfintegralProcessor);
        processor.setMainMatrixSolver(mainMatrixSolver());
        processor.setNodesAssembleIndexer(nodesAssembleIndexer());
        processor.setShapeFunctionFactory(shapeFunctionFactory());

        return processor;
    }

    @Bean
    public AnalysisModel analysisModel() {// don't turn it to resource, just
                                          // default to null
        // TODO Auto-generated method stub
        return null;
    }

    @Resource(name = "threadNum")
    int threadNum;

    @Bean
    public Factory<Map<AssemblerType, Assembler>> assemblersGroupFactory() {
        return new Factory<Map<AssemblerType, Assembler>>() {

            @Override
            public Map<AssemblerType, Assembler> produce() {
                return assemblersGroup();
            }
        };
    }

    @Resource(name = "applicationContextHolder")
    ApplicationContextHolder applicationContextHolder;

    @SuppressWarnings("unchecked")
    @Bean
    @Scope("prototype")
    public Map<AssemblerType, Assembler> assemblersGroup() {
        ApplicationContext context = applicationContextHolder.getContext();
        return (Map<AssemblerType, Assembler>) context.getBean("poissonAssemblersGroup");
    }

    @Bean
    public InfluenceRadiusCalculator influenceRadiusCalculator() {
        return null;
    }

    @Resource(name = "mfintegralProcessor")
    private MFIntegralProcessor mfintegralProcessor;

    @Bean
    public MFSolver mainMatrixSolver() {
        return new RcmSolver();
    }

    @Bean
    public NodesAssembleIndexer nodesAssembleIndexer() {
        return new OneDChainLagrangleNodesAssembleIndexer();
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
