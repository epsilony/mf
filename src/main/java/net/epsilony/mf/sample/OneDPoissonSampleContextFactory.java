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

import static net.epsilony.mf.util.MFUtils.rudeDefinition;
import static net.epsilony.mf.util.MFUtils.rudeListDefinition;

import java.util.List;

import javax.annotation.Resource;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.factory.ChainAnalysisModelFactory;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.sample.OneDPoissonSamplePhysicalModel;
import net.epsilony.mf.model.sample.OneDPoissonSamplePhysicalModel.OneDPoissonSample;
import net.epsilony.mf.process.MFLinearProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.integrate.MFIntegrateResult;
import net.epsilony.mf.process.integrate.aspect.SimpIntegralCounter;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.TestTool;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDPoissonSampleContextFactory extends AbstractSimpJavaConfigContextFactory {

    int nodesNum = 21;
    double influenceRadiusRatio = 3.5;
    int integralDegree = 2;
    Integer threadNum = Runtime.getRuntime().availableProcessors();
    InfluenceRadiusCalculator influenceRadiusCalculator;
    OneDPoissonSample sampleChoice;
    private AnalysisModel analysisModel;
    private OneDPoissonSamplePhysicalModel oneDPoissonSamplePhysicalModel;

    @Configuration
    @EnableAspectJAutoProxy
    public static class ConfigurationClass {
        @Resource(name = "influenceRadius")
        double influenceRadius;

        @Bean
        public InfluenceRadiusCalculator influenceRadiusCalculator() {
            return new ConstantInfluenceRadiusCalculator(influenceRadius);
        }

        @Resource(name = "analysisModelHolder")
        List<AnalysisModel> analysisModelHolder;

        @Bean
        public AnalysisModel analysisModel() {
            return analysisModelHolder.get(0);
        }

        @Resource(name = "threadNumHolder")
        List<Integer> threadNumHolder;

        @Bean
        public Integer threadNum() {
            return threadNumHolder.get(0);
        }

        @Bean
        public SimpIntegralCounter simpIntegralCounter() {
            return new SimpIntegralCounter();
        }

        @Bean
        public int integralDegree() {
            return integralDegreeHolder.get(0);
        }

        @Resource(name = "integralDegreeHolder")
        List<Integer> integralDegreeHolder;
    }

    @Override
    protected void fillContextSettings() {
        context.register(OneDPoissonConf.class, ConfigurationClass.class);
        genAnalysisModel();
        context.registerBeanDefinition("analysisModelHolder", rudeListDefinition(analysisModel));
        context.registerBeanDefinition("threadNumHolder", rudeListDefinition(threadNum));
        context.registerBeanDefinition("influenceRadius", rudeDefinition(Double.class, genInfluenceRadius()));
        context.registerBeanDefinition("integralDegreeHolder", rudeListDefinition(integralDegree));
    }

    private void genAnalysisModel() {
        oneDPoissonSamplePhysicalModel = new OneDPoissonSamplePhysicalModel();
        oneDPoissonSamplePhysicalModel.setChoice(sampleChoice);

        ChainAnalysisModelFactory analysisModelFactory = new ChainAnalysisModelFactory();
        analysisModelFactory.setChainPhysicalModel(oneDPoissonSamplePhysicalModel);
        analysisModelFactory.setFractionLengthCap(genFractionLengthCap());
        analysisModel = analysisModelFactory.produce();

    }

    private double genInfluenceRadius() {
        OneDPoissonSamplePhysicalModel model = (OneDPoissonSamplePhysicalModel) analysisModel.getOrigin();

        double start = model.getTerminalPoistion(true);
        double end = model.getTerminalPoistion(false);
        double radials = (end - start) / (nodesNum - 1) * influenceRadiusRatio;
        if (radials <= 0) {
            throw new IllegalStateException();
        }
        return radials;
    }

    private double genFractionLengthCap() {
        double start = oneDPoissonSamplePhysicalModel.getTerminalPoistion(true);
        double end = oneDPoissonSamplePhysicalModel.getTerminalPoistion(false);
        return (end - start) / (nodesNum - 1.1);
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

    public Integer getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
    }

    public OneDPoissonSample getSampleChoice() {
        return sampleChoice;
    }

    public void setSampleChoice(OneDPoissonSample sampleChoice) {
        this.sampleChoice = sampleChoice;
    }

    public void setIntegralDegree(int integralDegree) {
        this.integralDegree = integralDegree;
    }

    public static void main(String[] args) {
        OneDPoissonSample choice = OneDPoissonSample.ZERO;
        OneDPoissonSampleContextFactory contextFactory = new OneDPoissonSampleContextFactory();
        contextFactory.setSampleChoice(choice);
        contextFactory.setThreadNum(1);
        contextFactory.setIntegralDegree(4);
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
            System.out.println(String.format("%3d : %.14e", i, mainVector.get(i, 0)));
        }
    }
}
