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

import static org.junit.Assert.assertTrue;

import java.util.Map;

import net.epsilony.mf.model.sample.OneDPoissonSamplePhysicalModel;
import net.epsilony.mf.model.sample.OneDPoissonSamplePhysicalModel.OneDPoissonSample;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.util.MFUtils;
import net.epsilony.tb.TestTool;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDPoissonSamplesTest {
    int nodesNum = 21;
    int threadNum = 5;
    double errLimit = 5e-3;
    int samplePointNum = 100;
    OneDPoissonSampleContextFactory contextFactory = new OneDPoissonSampleContextFactory();
    private Map<String, Object> context;

    /**
     * 
     */
    public OneDPoissonSamplesTest() {
        contextFactory.setNodesNum(nodesNum);
        contextFactory.setThreadNum(threadNum);
    }

    @Test
    public void testSamples() {
        for (OneDPoissonSample choice : OneDPoissonSample.values()) {
            testChoice(choice);
        }
    }

    public void testChoice(OneDPoissonSample choice) {
        System.out.println("choice = " + choice);
        contextFactory.setSampleChoice(choice);
        context = contextFactory.produce();
        MFLinearProcessor processor = (MFLinearProcessor) context.get(MFUtils.singletonName(MFLinearProcessor.class));

        processor.preprocess();
        processor.solve();
        PostProcessor postProcessor = processor.genPostProcessor();
        double[] samplePoints = genSamplePoints();
        double[] coord = new double[2];
        UnivariateFunction solution = choice.getSolution();
        double errSum = 0;
        int errSumNum = 0;
        for (double point : samplePoints) {
            double exp = solution.value(point);
            if (exp < 1e-7) {
                continue;
            }
            coord[0] = point;
            double act = postProcessor.value(coord, null)[0];
            errSum += FastMath.abs(exp - act) / act;
            errSumNum++;
        }
        double avgErr = errSum / errSumNum;
        System.out.println("avgErr = " + avgErr);
        System.out.println("errSumNum = " + errSumNum);
        assertTrue(errSumNum > 0);
        assertTrue(avgErr < errLimit);
    }

    private double[] genSamplePoints() {
        OneDPoissonSamplePhysicalModel model = (OneDPoissonSamplePhysicalModel) context.get(MFUtils
                .singletonName(OneDPoissonSamplePhysicalModel.class));
        double start = model.getTerminalPoistion(true);
        double end = model.getTerminalPoistion(false);
        return TestTool.linSpace(start, end, samplePointNum);
    }

}
