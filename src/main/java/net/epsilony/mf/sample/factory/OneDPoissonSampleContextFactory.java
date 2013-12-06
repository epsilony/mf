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

    @Override
    public ApplicationContext produce() {
        OneDPoissonSampleAnalysisModelFactory analysisModelFactory = new OneDPoissonSampleAnalysisModelFactory();
        analysisModelFactory.setChoice(choice);
        analysisModelFactory.setNodesNum(nodesNum);
        processContextFactory.setThreadNum(threadNum);
        processContextFactory.setAnalysisModel(analysisModelFactory.produce());
        processContextFactory.setInfluenceRadiusCalculator(new ConstantInfluenceRadiusCalculator(influenceRadiusRatio
                * analysisModelFactory.getFractionLengthCap()));
        return processContextFactory.produce();
    }

    public int getNodesNum() {
        return nodesNum;
    }

    public void setNodesNum(int nodesNum) {
        this.nodesNum = nodesNum;
    }

    public double getInfluenceRadiusRatio() {
        return influenceRadiusRatio;
    }

    public void setInfluenceRadiusRatio(double influenceRadiusRatio) {
        this.influenceRadiusRatio = influenceRadiusRatio;
    }

    public OneDPoissonSample getChoice() {
        return choice;
    }

    public void setChoice(OneDPoissonSample choice) {
        this.choice = choice;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public ProcessContextFactory getProcessContextFactory() {
        return processContextFactory;
    }

    public void setProcessContextFactory(ProcessContextFactory processContextFactory) {
        this.processContextFactory = processContextFactory;
    }
}
