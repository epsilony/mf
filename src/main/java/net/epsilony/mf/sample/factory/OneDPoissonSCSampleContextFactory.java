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

import net.epsilony.mf.model.sample.OneDPoissonSamplePhysicalModel.OneDPoissonSample;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.integrate.MFIntegrateResult;
import net.epsilony.mf.process.integrate.core.MFIntegratorCore;
import net.epsilony.mf.process.integrate.core.oned.StabilizedConformingLineIntegratorCore;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.TestTool;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDPoissonSCSampleContextFactory extends OneDPoissonSampleContextFactory {

    @Configuration
    static class OverrideVolumeIntegratorCoreConfig {
        @Bean
        @Scope("prototype")
        public MFIntegratorCore volumeIntegratorCore() {
            return new StabilizedConformingLineIntegratorCore();
        }
    }

    @Override
    protected void fillContextSettings() {
        super.fillContextSettings();
        context.register(OverrideVolumeIntegratorCoreConfig.class);
    }

    public static void main(String[] args) {
        OneDPoissonSample choice = OneDPoissonSample.ZERO;
        OneDPoissonSCSampleContextFactory contextFactory = new OneDPoissonSCSampleContextFactory();
        contextFactory.setSampleChoice(choice);
        contextFactory.setThreadNum(1);
        contextFactory.setIntegralDegree(4);
        contextFactory.setNodesNum(100);
        contextFactory.setInfluenceRadiusRatio(2.1);
        UnivariateFunction solution = choice.getSolution();

        ApplicationContext context = contextFactory.produce();
        MFLinearProcessor processor = context.getBean(MFLinearProcessor.class);

        processor.preprocess();
        processor.solve();
        PostProcessor postProcessor = processor.genPostProcessor();
        for (double x : TestTool.linSpace(0.1, 0.9, 10)) {
            double[] result = postProcessor.value(new double[] { x, 0 }, null);
            double act = solution.value(x);
            System.out
                    .println("at " + x + " result = " + result[0] + " act = " + act + " error = " + (act - result[0]));
        }

        MFIntegrateResult integrateResult = processor.getIntegrateResult();
        MFMatrix mainVector = integrateResult.getMainVector();
        for (int i = 0; i < mainVector.numRows(); i++) {
            System.out.println(String.format("%3d : %e", i, mainVector.get(i, 0)));
        }
    }
}
