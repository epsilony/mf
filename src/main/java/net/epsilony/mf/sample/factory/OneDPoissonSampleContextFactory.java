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

import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.sample.OneDPoissonSampleAnalysisModelFactory;
import net.epsilony.mf.model.sample.OneDPoissonSamplePhysicalModel.OneDPoissonSample;
import net.epsilony.tb.Factory;

import org.springframework.context.ApplicationContext;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDPoissonSampleContextFactory implements Factory<ApplicationContext> {
    OneDPoissonSample choice;
    int nodesNum = 21;
    int threadNum = 5;
    double influenceRadiusRatio = 3.5;
    ProcessContextFactory processContextFactory;
    OneDPoissonSampleAnalysisModelFactory analysisModelFactory = new OneDPoissonSampleAnalysisModelFactory();

    public OneDPoissonSampleContextFactory(ProcessContextFactory processContextFactory) {
        this.processContextFactory = processContextFactory;
    }

    @Override
    public ApplicationContext produce() {
        processContextFactory.setAnalysisModel(analysisModelFactory.produce());
        processContextFactory.setInfluenceRadiusCalculator(new ConstantInfluenceRadiusCalculator(influenceRadiusRatio
                * analysisModelFactory.getFractionLengthCap()));
        return processContextFactory.produce();
    }

    public int getIntegralDegree() {
        return processContextFactory.getIntegralDegree();
    }

    public void setIntegralDegree(int integralDegree) {
        processContextFactory.setIntegralDegree(integralDegree);
    }

    public Integer getThreadNum() {
        return processContextFactory.getThreadNum();
    }

    public void setThreadNum(Integer threadNum) {
        processContextFactory.setThreadNum(threadNum);
    }

    public double getFractionLengthCap() {
        return analysisModelFactory.getFractionLengthCap();
    }

    public OneDPoissonSample getChoice() {
        return analysisModelFactory.getChoice();
    }

    public void setChoice(OneDPoissonSample choice) {
        analysisModelFactory.setChoice(choice);
    }

    public int getNodesNum() {
        return analysisModelFactory.getNodesNum();
    }

    public void setNodesNum(int nodesNum) {
        analysisModelFactory.setNodesNum(nodesNum);
    }

    public double getDisturbRatio() {
        return analysisModelFactory.getDisturbRatio();
    }

    public void setDisturbRatio(double disturbRatio) {
        analysisModelFactory.setDisturbRatio(disturbRatio);
    }

    public double getInfluenceRadiusRatio() {
        return influenceRadiusRatio;
    }

    public void setInfluenceRadiusRatio(double influenceRadiusRatio) {
        this.influenceRadiusRatio = influenceRadiusRatio;
    }
}
