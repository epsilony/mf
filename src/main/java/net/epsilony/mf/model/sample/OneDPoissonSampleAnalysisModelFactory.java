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
package net.epsilony.mf.model.sample;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.factory.ChainAnalysisModelFactory;
import net.epsilony.mf.model.sample.OneDPoissonSamplePhysicalModel.OneDPoissonSample;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDPoissonSampleAnalysisModelFactory implements Factory<AnalysisModel> {
    int nodesNum = 21;
    private final OneDPoissonSamplePhysicalModel oneDPoissonSamplePhysicalModel = new OneDPoissonSamplePhysicalModel();
    double disturbRatio = 0;
    Random random;

    @Override
    public AnalysisModel produce() {

        ChainAnalysisModelFactory analysisModelFactory = new ChainAnalysisModelFactory();
        analysisModelFactory.setChainPhysicalModel(oneDPoissonSamplePhysicalModel);
        analysisModelFactory.setFractionLengthCap(getFractionLengthCap());
        AnalysisModel analysisModel = analysisModelFactory.produce();
        disturbNodes(analysisModel);
        return analysisModel;
    }

    public double getFractionLengthCap() {
        double start = oneDPoissonSamplePhysicalModel.getTerminalPoistion(true);
        double end = oneDPoissonSamplePhysicalModel.getTerminalPoistion(false);
        return (end - start) / (nodesNum - 1.1);
    }

    private void disturbNodes(AnalysisModel analysisModel) {
        if (0 == disturbRatio) {
            return;
        }
        random = new Random();
        Chain chain = (Chain) analysisModel.getGeomRoot();
        LinkedList<double[]> xs = new LinkedList<>();
        for (Segment segment : chain) {
            xs.add(new double[] { segment.getStart().getCoord()[0] });
        }
        double[] first = xs.removeFirst();
        double[] last = xs.removeLast();

        double segSize = Segment2DUtils.chordLength(chain.getHead());
        for (double[] x : xs) {
            x[0] += (-0.99 + random.nextDouble() * disturbRatio * 1.98) * segSize;
        }
        LinkedList<Double> disturbedXs = new LinkedList<>();
        for (double[] x : xs) {
            disturbedXs.add(x[0]);
        }
        Collections.sort(disturbedXs);
        disturbedXs.addFirst(first[0]);
        disturbedXs.addLast(last[0]);
        Iterator<Double> iterator = disturbedXs.iterator();
        for (Segment segment : chain) {
            double x = iterator.next();
            segment.getStart().getCoord()[0] = x;
        }
    }

    public OneDPoissonSample getChoice() {
        return oneDPoissonSamplePhysicalModel.getChoice();
    }

    public void setChoice(OneDPoissonSample choice) {
        oneDPoissonSamplePhysicalModel.setChoice(choice);
    }

    public int getNodesNum() {
        return nodesNum;
    }

    public void setNodesNum(int nodesNum) {
        this.nodesNum = nodesNum;
    }

    public double getDisturbRatio() {
        return disturbRatio;
    }

    public void setDisturbRatio(double disturbRatio) {
        this.disturbRatio = disturbRatio;
    }
}
