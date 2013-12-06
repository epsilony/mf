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

import static net.epsilony.mf.util.MFUtils.rudeListDefinition;

import java.util.LinkedList;
import java.util.List;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.tb.Factory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ProcessContextFactory implements Factory<ApplicationContext> {

    protected AnnotationConfigApplicationContext context;
    protected int integralDegree = 2;
    protected Integer threadNum = Runtime.getRuntime().availableProcessors();
    protected AnalysisModel analysisModel;
    protected InfluenceRadiusCalculator influenceRadiusCalculator;
    private final List<ContextModifier> modifiers = new LinkedList<>();

    public interface ContextModifier {
        void modify(AnnotationConfigApplicationContext context);
    }

    public ProcessContextFactory() {
        super();
    }

    @Override
    public ApplicationContext produce() {
        context = new AnnotationConfigApplicationContext();
        context.register(ProcessContextConf.class);
        context.registerBeanDefinition("analysisModelHolder", rudeListDefinition(analysisModel));
        context.registerBeanDefinition("threadNumHolder", rudeListDefinition(threadNum));
        context.registerBeanDefinition("influenceRadiusCalculatorHolder", rudeListDefinition(influenceRadiusCalculator));
        context.registerBeanDefinition("integralDegreeHolder", rudeListDefinition(integralDegree));
        modifyContext();
        context.refresh();
        return context;
    }

    protected void modifyContext() {

    }

    public List<ContextModifier> getModifiers() {
        return modifiers;
    }

    public int getIntegralDegree() {
        return integralDegree;
    }

    public void setIntegralDegree(int integralDegree) {
        this.integralDegree = integralDegree;
    }

    public Integer getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
    }

    public AnalysisModel getAnalysisModel() {
        return analysisModel;
    }

    public void setAnalysisModel(AnalysisModel analysisModel) {
        this.analysisModel = analysisModel;
    }

    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

}