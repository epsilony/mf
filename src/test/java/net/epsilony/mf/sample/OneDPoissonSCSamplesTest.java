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

import java.util.Arrays;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.sample.OneDPoissonSamplePhysicalModel;
import net.epsilony.mf.model.sample.OneDPoissonSamplePhysicalModel.OneDPoissonSample;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.tb.TestTool;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDPoissonSCSamplesTest {
    int nodesNum = 21;
    int threadNum = 5;
    double errLimit = 1e-12;
    int samplePointNum = 100;

    OneDPoissonSCSampleContextFactory contextFactory = new OneDPoissonSCSampleContextFactory();
    private ApplicationContext context;

    /**
     * 
     */
    public OneDPoissonSCSamplesTest() {
        contextFactory.setNodesNum(nodesNum);
        contextFactory.setThreadNum(threadNum);
    }

    @Test
    public void testSamples() {
        for (OneDPoissonSample choice : Arrays.asList(OneDPoissonSample.ZERO)) {
            testChoice(choice);
        }
    }

    public void testChoice(OneDPoissonSample choice) {
        System.out.println("choice = " + choice);
        contextFactory.setSampleChoice(choice);
        context = contextFactory.produce();
        MFLinearProcessor processor = context.getBean(MFLinearProcessor.class);

        processor.preprocess();
        processor.solve();
        PostProcessor postProcessor = processor.genPostProcessor();
        double[] samplePoints = genSamplePoints();
        double[] coord = new double[2];
        UnivariateFunction solution = choice.getSolution();
        for (double point : samplePoints) {
            double exp = solution.value(point);
            coord[0] = point;
            double act = postProcessor.value(coord, null)[0];
            try {
                Assert.assertEquals(exp, act, errLimit);
            } catch (AssertionError ex) {
                System.out.println("exp = " + exp);
                System.out.println("act = " + act);
                System.out.println("point = " + point);
            }
        }
    }

    private double[] genSamplePoints() {
        AnalysisModel analysisModel = (AnalysisModel) context.getBean("analysisModel");
        OneDPoissonSamplePhysicalModel model = (OneDPoissonSamplePhysicalModel) analysisModel.getOrigin();
        double start = model.getTerminalPoistion(true);
        double end = model.getTerminalPoistion(false);
        return TestTool.linSpace(start, end, samplePointNum);
    }

}
