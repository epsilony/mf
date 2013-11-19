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

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblersConf;
import net.epsilony.mf.process.indexer.NodesAssembleIndexer;
import net.epsilony.mf.process.indexer.TwoDFacetLagrangleNodesAssembleIndexer;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.MFIntegratorConf;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
@Import({ AssemblersConf.class, MFIntegratorConf.class })
public class TwoDMechanicalConf {
    @Bean
    public MFLinearMechanicalProcessor mflinearMechanicalProcessor() {
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();

        processor.setAssemblersGroup(assemblersGroup);
        processor.setAnalysisModel(analysisModel());
        processor.setIntegrator(mfintegrator);
        processor.setInfluenceRadiusCalculator(influenceRadiusCalculator());
        processor.setMainMatrixSolver(mainMatrixSolver());
        processor.setNodesAssembleIndexer(nodesAssembleIndexer());
        processor.setShapeFunction(shapeFunction());
        processor.setConstitutiveLaw(constitutiveLaw());
        return processor;
    }

    @Bean
    public AnalysisModel analysisModel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Resource(name = "mechanicalAssemblersGroup")
    private Map<MFProcessType, Assembler> assemblersGroup;

    @Resource(name = "mfintegrator")
    private MFIntegrator mfintegrator;

    @Bean
    public InfluenceRadiusCalculator influenceRadiusCalculator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Bean
    public ConstitutiveLaw constitutiveLaw() {
        return null;
    }

    @Bean
    public MFSolver mainMatrixSolver() {
        return new RcmSolver();
    }

    @Bean
    public NodesAssembleIndexer nodesAssembleIndexer() {
        return new TwoDFacetLagrangleNodesAssembleIndexer();
    }

    @Bean
    public MFShapeFunction shapeFunction() {
        return new MLS();
    }
}
