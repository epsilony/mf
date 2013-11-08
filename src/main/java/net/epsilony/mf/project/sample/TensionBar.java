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

package net.epsilony.mf.project.sample;

import net.epsilony.mf.project.RectangleProjectFactory;
import java.util.Arrays;
import java.util.Random;
import net.epsilony.mf.cons_law.PlaneStress;
import net.epsilony.mf.model.MFRectangleEdge;
import static net.epsilony.mf.model.MFRectangleEdge.*;
import net.epsilony.mf.model.load.ConstantSegmentLoad;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MFPreprocessorKey;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.assembler.Assemblers;
import net.epsilony.mf.process.integrate.MFIntegratorFactory;
import net.epsilony.mf.project.MFProject;
import net.epsilony.tb.Factory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TensionBar implements Factory<MFProject> {

    double tension = 2000;
    double E = 1000;
    double mu = 0.3;
    RectangleProjectFactory rectangleProjectFactory = new RectangleProjectFactory();

    @Override
    public MFProject produce() {
        rectangleProjectFactory.setAssemblersGroup(Assemblers.mechanicalLagrangle());
        rectangleProjectFactory.setValueDimension(2);
        applyLoadsOnRectangle();
        genConstitutiveLaw();
        return rectangleProjectFactory.produce();
    }

    protected void applyLoadsOnRectangle() {
        ConstantSegmentLoad leftLoad = new ConstantSegmentLoad();
        leftLoad.setValue(new double[] { 0, 0 });
        leftLoad.setValidity(new boolean[] { true, false });
        rectangleProjectFactory.setEdgeLoad(LEFT, leftLoad);

        ConstantSegmentLoad rightLoad = new ConstantSegmentLoad();
        rightLoad.setValue(new double[] { tension, 0 });
        rectangleProjectFactory.setEdgeLoad(RIGHT, rightLoad);

        ConstantSegmentLoad downLoad = new ConstantSegmentLoad();
        downLoad.setValue(new double[] { 0, 0 });
        downLoad.setValidity(new boolean[] { false, true });
        rectangleProjectFactory.setEdgeLoad(DOWN, downLoad);
    }

    protected void genConstitutiveLaw() {
        rectangleProjectFactory.setConstitutiveLaw(new PlaneStress(E, mu));
    }

    public boolean isAvialable() {
        return rectangleProjectFactory.isAvialable();
    }

    public double getEdgePosition(MFRectangleEdge edge) {
        return rectangleProjectFactory.getEdgePosition(edge);
    }

    public double getHeight() {
        return rectangleProjectFactory.getHeight();
    }

    public int getQuadratureDegree() {
        return rectangleProjectFactory.getQuadratureDegree();
    }

    public double getWidth() {
        return rectangleProjectFactory.getWidth();
    }

    public void setEdgePosition(MFRectangleEdge edge, double position) {
        rectangleProjectFactory.setEdgePosition(edge, position);
    }

    public void setQuadratureDegree(int quadratureDegree) {
        rectangleProjectFactory.setQuadratureDegree(quadratureDegree);
    }

    public void setSpaceNodesDisturbRatio(double spaceNodesDisturbRatio) {
        rectangleProjectFactory.setSpaceNodesDisturbRatio(spaceNodesDisturbRatio);
    }

    public void setDisturbRand(Random disturbRand) {
        rectangleProjectFactory.setDisturbRand(disturbRand);
    }

    public double getSpaceNodesDisturbRatio() {
        return rectangleProjectFactory.getSpaceNodesDisturbRatio();
    }

    public static void main(String[] args) {
        TensionBar tensionBar = new TensionBar();
        // tensionBar.setSpaceNodesDisturbRatio(0.9);
        MFProject project = tensionBar.produce();
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(project);
        MFIntegratorFactory factory = new MFIntegratorFactory();
        factory.setThreadNum(2);
        processor.getSettings().put(MFPreprocessorKey.INTEGRATOR, factory.produce());
        processor.preprocess();
        processor.solve();
        // PostProcessor pp = processor.genPostProcessor();
        MechanicalPostProcessor mpp = processor.genMechanicalPostProcessor();
        int stepNum = 20;
        for (int i = 0; i < stepNum; i++) {
            double[] pt = new double[] { 2.99, 0.01 + 2.98 * (i * 1.0 / (stepNum - 1)) };
            double[] strain = mpp.engineeringStrain(pt, null);
            double[] value = mpp.value(pt, null);
            System.out.println("pt = " + Arrays.toString(pt) + ", value = " + Arrays.toString(value)
                    + ", eng strain = " + Arrays.toString(strain));
        }
    }
}
