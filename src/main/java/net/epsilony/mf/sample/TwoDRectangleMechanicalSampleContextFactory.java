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

import static net.epsilony.mf.util.MFUtils.rudeListDefinition;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.model.factory.RectangleAnalysisModelFactory;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.sample.TensionBarSamplePhysicalModel;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.integrate.aspect.SimpIntegralCounter;
import net.epsilony.tb.TestTool;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TwoDRectangleMechanicalSampleContextFactory extends AbstractSimpJavaConfigContextFactory {

    AnalysisModel analysisModel;
    Integer threadNum = Runtime.getRuntime().availableProcessors();
    int integralDegree = 2;
    ConstitutiveLaw constitutiveLaw;
    InfluenceRadiusCalculator influenceRadiusCalculator;

    @Configuration
    @EnableAspectJAutoProxy
    public static class ConfigurationClass {

        @Resource(name = "influenceRadiusCalculatorHolder")
        List<InfluenceRadiusCalculator> influenceRadiusCalculatorHolder;

        @Bean
        public InfluenceRadiusCalculator influenceRadiusCalculator() {
            return influenceRadiusCalculatorHolder.get(0);
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

        @Resource(name = "constitutiveLawHolder")
        List<ConstitutiveLaw> constitutiveLawHolder;

        @Bean
        public ConstitutiveLaw constitutiveLaw() {
            return constitutiveLawHolder.get(0);
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
        context.register(TwoDMechanicalConf.class, ConfigurationClass.class);
        context.registerBeanDefinition("analysisModelHolder", rudeListDefinition(analysisModel));
        context.registerBeanDefinition("threadNumHolder", rudeListDefinition(threadNum));
        context.registerBeanDefinition("influenceRadiusCalculatorHolder", rudeListDefinition(influenceRadiusCalculator));
        context.registerBeanDefinition("constitutiveLawHolder", rudeListDefinition(constitutiveLaw));
        context.registerBeanDefinition("integralDegreeHolder", rudeListDefinition(integralDegree));
    }

    public AnalysisModel getAnalysisModel() {
        return analysisModel;
    }

    public void setAnalysisModel(AnalysisModel analysisModel) {
        this.analysisModel = analysisModel;
    }

    public Integer getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
    }

    public int getIntegralDegree() {
        return integralDegree;
    }

    public void setIntegralDegree(int quadratureDegree) {
        this.integralDegree = quadratureDegree;
    }

    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public static void main(String[] args) {
        double subDomainSize = 1;
        double influenceRatio = 3.5;

        TensionBarSamplePhysicalModel tensionBar = new TensionBarSamplePhysicalModel();
        RectangleAnalysisModelFactory factory = new RectangleAnalysisModelFactory();
        factory.setRectangleModel(tensionBar);
        factory.setFractionSizeCap(subDomainSize);
        AnalysisModel analysisModel = factory.produce();

        ConstantInfluenceRadiusCalculator influenceRadiusCalculator = new ConstantInfluenceRadiusCalculator(
                subDomainSize * influenceRatio);

        TwoDRectangleMechanicalSampleContextFactory contextFactory = new TwoDRectangleMechanicalSampleContextFactory();
        contextFactory.setAnalysisModel(analysisModel);
        contextFactory.setInfluenceRadiusCalculator(influenceRadiusCalculator);
        contextFactory.setConstitutiveLaw(tensionBar.getConstitutiveLaw());

        ApplicationContext context = contextFactory.produce();
        MFLinearMechanicalProcessor processor = context.getBean(MFLinearMechanicalProcessor.class);
        processor.preprocess();
        processor.solve();
        MechanicalPostProcessor postProcessor = processor.genMechanicalPostProcessor();
        double x = tensionBar.getEdgePosition(MFRectangleEdge.RIGHT) - 0.01;
        double[] ys = TestTool.linSpace(tensionBar.getEdgePosition(MFRectangleEdge.DOWN) + 0.01,
                tensionBar.getEdgePosition(MFRectangleEdge.UP) - 0.01, 10);
        for (double y : ys) {
            double[] center = new double[] { x, y };
            double[] disp = postProcessor.value(center, null);
            System.out.println(Arrays.toString(center) + " : " + Arrays.toString(disp) + ", act = "
                    + Arrays.toString(new double[] { 18, y * -0.6 }));
            // TODO: the act calculator has to be write inside tensionBar
        }
    }

}
