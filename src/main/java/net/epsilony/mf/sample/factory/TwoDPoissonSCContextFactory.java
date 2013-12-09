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

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.factory.RectangleAnalysisModelFactory;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.sample.TwoDPoissonSamplePhysicalModel;
import net.epsilony.mf.model.sample.TwoDPoissonSamplePhysicalModel.TwoDPoissonSample;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.twod.StabilizedConformingPolygonVolumeIntegratorCore;
import net.epsilony.tb.TestTool;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TwoDPoissonSCContextFactory extends TwoDPoissonContextFactory {
    @Configuration
    static class OverrideVolumeIntegratorCoreConfig {
        @Bean
        @Scope("prototype")
        public MFIntegratorCore volumeIntegratorCore() {
            return new StabilizedConformingPolygonVolumeIntegratorCore();
        }
    }

    @Override
    protected void modifyContext() {
        super.modifyContext();
        context.register(OverrideVolumeIntegratorCoreConfig.class);
    }

    public static void main(String[] args) {
        double subDomainSize = 0.1;
        double influenceRadiusRatio = 3.5;
        TwoDPoissonSamplePhysicalModel.TwoDPoissonSample sample = TwoDPoissonSample.LINEAR;
        TwoDPoissonSamplePhysicalModel physicalModel = new TwoDPoissonSamplePhysicalModel();
        physicalModel.setTwoDPoissonSample(sample);

        RectangleAnalysisModelFactory analysisModelFactory = new RectangleAnalysisModelFactory();
        analysisModelFactory.setRectangleModel(physicalModel);
        analysisModelFactory.setFractionSizeCap(subDomainSize);
        analysisModelFactory.setSpaceNodesDisturbRatio(0.5);

        AnalysisModel analysisModel = analysisModelFactory.produce();

        TwoDPoissonSCContextFactory contextFactory = new TwoDPoissonSCContextFactory();
        contextFactory.setAnalysisModel(analysisModel);
        contextFactory.setThreadNum(1);
        contextFactory.setIntegralDegree(2);
        contextFactory.setInfluenceRadiusCalculator(new ConstantInfluenceRadiusCalculator(influenceRadiusRatio
                * subDomainSize));
        ApplicationContext context = contextFactory.produce();

        MFLinearProcessor processor = context.getBean(MFLinearProcessor.class);

        processor.preprocess();
        processor.solve();
        PostProcessor postProcessor = processor.genPostProcessor();

        double[] xs = TestTool.linSpace(-0.5, 0.5, 3);
        double[] ys = TestTool.linSpace(-0.5, 0.5, 3);
        for (double x : xs) {
            for (double y : ys) {
                double act = postProcessor.value(new double[] { x, y }, null)[0];
                double exp = sample.getSolution(new double[] { x, y });
                System.out.println(String.format("act = %20.16f , exp = %20.16f, (act-exp) = %20.16f", act, exp, act
                        - exp));
            }
        }
    }
}
