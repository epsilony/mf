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

import java.util.Arrays;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.model.factory.RectangleAnalysisModelFactory;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.sample.TensionBarSamplePhysicalModel;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
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
public class TwoDSCMechanicalSampleContextFactory extends TwoDMechanicalSampleContextFactory {
    @Configuration
    static class OverrideVolumeIntegratorCoreConfig {
        @Bean
        @Scope("prototype")
        public MFIntegratorCore volumeIntegratorCore() {
            return new StabilizedConformingPolygonVolumeIntegratorCore();
        }
    }

    @Override
    protected void fillContextSettings() {
        super.fillContextSettings();
        context.register(OverrideVolumeIntegratorCoreConfig.class);
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

        TwoDSCMechanicalSampleContextFactory contextFactory = new TwoDSCMechanicalSampleContextFactory();
        contextFactory.setAnalysisModel(analysisModel);
        contextFactory.setInfluenceRadiusCalculator(influenceRadiusCalculator);
        contextFactory.setConstitutiveLaw(tensionBar.getConstitutiveLaw());

        ApplicationContext context = contextFactory.produce();
        MFLinearMechanicalProcessor processor = context.getBean(MFLinearMechanicalProcessor.class);
        processor.preprocess();
        processor.solve();
        MechanicalPostProcessor postProcessor = processor.genMechanicalPostProcessor();
        double x = tensionBar.getEdgePosition(MFRectangleEdge.RIGHT) - 1;
        double[] ys = TestTool.linSpace(tensionBar.getEdgePosition(MFRectangleEdge.DOWN) + 1,
                tensionBar.getEdgePosition(MFRectangleEdge.UP) - 1, 10);
        for (double y : ys) {
            double[] center = new double[] { x, y };
            double[] disp = postProcessor.value(center, null);
            System.out.println(Arrays.toString(center) + " : " + Arrays.toString(disp) + ", act = "
                    + Arrays.toString(new double[] { 18, y * -0.6 }));
        }
    }
}
