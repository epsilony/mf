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
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.model.factory.RectangleAnalysisModelFactory;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.EnsureNodesNum;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.sample.TimoshenkoCantileverPhysicalModel;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.sample.factory.TwoDMechanicalSampleContextFactory;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.analysis.Math2D;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFTimoshenkoCantileverTest {

    MechanicalPostProcessor mechanicalPostProcessor;
    InfluenceRadiusCalculator influenceRadiusCalculator;
    TimoshenkoCantileverPhysicalModel timoshenkoCantileverPhysicalModel;
    double subdomainSize = 1;
    int quadratureDegree = 4;
    double influenceRadiusRatio = 4.1;
    Integer threadNum = 25;
    AnalysisModel analysisModel;
    ApplicationContext testContext;

    private void genPhysicalModel() {
        timoshenkoCantileverPhysicalModel = new TimoshenkoCantileverPhysicalModel();

        timoshenkoCantileverPhysicalModel.setEdgePosition(MFRectangleEdge.LEFT, 0);
        timoshenkoCantileverPhysicalModel.setEdgePosition(MFRectangleEdge.DOWN, -6);
        timoshenkoCantileverPhysicalModel.setEdgePosition(MFRectangleEdge.UP, 6);
        timoshenkoCantileverPhysicalModel.setEdgePosition(MFRectangleEdge.RIGHT, 48);

        timoshenkoCantileverPhysicalModel.setE(3e7);
        timoshenkoCantileverPhysicalModel.setNu(0.3);
        timoshenkoCantileverPhysicalModel.setP(-1000);
    }

    private void genAnalysisModel() {
        RectangleAnalysisModelFactory factory = new RectangleAnalysisModelFactory();
        factory.setRectangleModel(timoshenkoCantileverPhysicalModel);
        factory.setFractionSizeCap(subdomainSize);
        analysisModel = factory.produce();
    }

    private void genConstantInfluenceRadiusCalculator() {
        influenceRadiusCalculator = new ConstantInfluenceRadiusCalculator(subdomainSize * influenceRadiusRatio);
    }

    private void genEnsureNodesNumCalculator() {
        influenceRadiusCalculator = new EnsureNodesNum(4, 10);
    }

    private void genContext() {
        TwoDMechanicalSampleContextFactory factory = new TwoDMechanicalSampleContextFactory();
        factory.setThreadNum(threadNum);
        factory.setAnalysisModel(analysisModel);
        factory.setIntegralDegree(quadratureDegree);
        factory.setConstitutiveLaw(timoshenkoCantileverPhysicalModel.constitutiveLaw());
        factory.setInfluenceRadiusCalculator(influenceRadiusCalculator);
        testContext = factory.produce();
    }

    @Test
    public void testWithConstantInfluenceCalculator() {
        System.out.println("test with constant inf rads");

        genPhysicalModel();
        genAnalysisModel();
        genConstantInfluenceRadiusCalculator();
        genContext();
        MFLinearMechanicalProcessor processor = testContext.getBean(MFLinearMechanicalProcessor.class);
        processor.preprocess();
        processor.solve();
        mechanicalPostProcessor = processor.genMechanicalPostProcessor();

        testOnXAxis();
        testOnLeftSide();
    }

    public void testOnXAxis() {
        System.out.println("test Timoshenko standard beam, x axis");
        mechanicalPostProcessor.setDiffOrder(0);
        CurveOnXAxis xAxisCure = new CurveOnXAxis();
        int valueIndex = 1;// y direction displacement
        double[] results = integrateDisplacementErrorSquareOnCurve(xAxisCure, valueIndex);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-11 * accurateValue;
        System.out.println("err = " + err);
        System.out.println("expErr = " + expErr);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
        // assertEquals(1.0728621297419604E-16, err, 1e-16); //typical value
    }

    public void testOnLeftSide() {
        System.out.println("test Timoshinko standard beam, left edge");
        mechanicalPostProcessor.setDiffOrder(0);
        CurveOnLeftSide curve = new CurveOnLeftSide();
        int valueIndex = 1;
        double[] results = integrateDisplacementErrorSquareOnCurve(curve, valueIndex);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-8 * accurateValue;
        System.out.println("err = " + err);
        System.out.println("expErr = " + expErr);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
        // assertEquals(5.9676721435783116E-18, err, 1e-18); //typical value
    }

    @Test
    public void testWithEnsureNodes() {
        genPhysicalModel();
        genAnalysisModel();
        genEnsureNodesNumCalculator();
        genContext();
        MFLinearMechanicalProcessor processor = testContext.getBean(MFLinearMechanicalProcessor.class);
        processor.preprocess();
        processor.solve();
        mechanicalPostProcessor = processor.genMechanicalPostProcessor();

        testOnLeftSide_EnsureNodesNum();
        testOnXAxis_EnsureNodesNum();
        textOnALineInsideBeam_EnsureNodesNum();
    }

    public void testOnLeftSide_EnsureNodesNum() {
        System.out.println("test Timoshinko standard beam, left edge");
        mechanicalPostProcessor.setDiffOrder(0);
        CurveOnLeftSide curve = new CurveOnLeftSide();
        int valueIndex = 1;
        double[] results = integrateDisplacementErrorSquareOnCurve(curve, valueIndex);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-7 * accurateValue;
        System.out.println("err = " + err);
        System.out.println("expErr = " + expErr);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
    }

    public void testOnXAxis_EnsureNodesNum() {
        System.out.println("test Timoshenko standard beam, x axis");
        mechanicalPostProcessor.setDiffOrder(0);

        CurveOnXAxis xAxisCure = new CurveOnXAxis();
        int valueIndex = 1;
        double[] results = integrateDisplacementErrorSquareOnCurve(xAxisCure, valueIndex);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-7 * accurateValue;
        System.out.println("err = " + err);
        System.out.println("expErr = " + expErr);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
    }

    public void textOnALineInsideBeam_EnsureNodesNum() {
        System.out.println("test Timoshenko standard beam, on a given line");
        mechanicalPostProcessor.setDiffOrder(1);
        ALineInsideRectangle curve = new ALineInsideRectangle();
        System.out.println("test displacements");
        double curveLength = curve.getLength();
        for (int valueIndex = 0; valueIndex < 6; valueIndex++) {
            double[] results = integrateDisplacementErrorSquareOnCurve(curve, valueIndex);
            double err = results[0];
            double accurateValue = results[1];
            double expValue = results[2];
            double expErr = 5e-3 * accurateValue / curveLength;
            System.out.println("valueIndex = " + valueIndex);
            System.out.println("err = " + err);
            System.out.println("expErr = " + expErr);
            System.out.println("accurateValue = " + accurateValue);
            System.out.println("expValue = " + expValue);
            assertTrue(err <= expErr);
        }

        System.out.println("test strain");
        for (int valueIndex = 0; valueIndex < 3; valueIndex++) {
            double[] results = integrateStrainErrorSquareOnCurve(curve, valueIndex);
            double err = results[0];
            double accurateValue = results[1];
            double expValue = results[2];
            double expErr = 2e-2 * accurateValue / curveLength;
            System.out.println("valueIndex = " + valueIndex);
            System.out.println("err = " + err);
            System.out.println("expErr = " + expErr);
            System.out.println("accurateValue = " + accurateValue);
            System.out.println("expValue = " + expValue);
            assertTrue(err <= expErr);
        }
    }

    public double[] integrateDisplacementErrorSquareOnCurve(GenericFunction<Double, double[]> curve, int valueIndex) {
        final UnivariateFunction actFunc = new NumericalDisplacementOnCurve(curve, valueIndex);
        final UnivariateFunction expFunc = new PreciseDisplacementOnCurve(curve, valueIndex);
        UnivariateFunction func = new UnivariateFunction() {
            @Override
            public double value(double x) {
                double d = (actFunc.value(x) - expFunc.value(x));
                return d * d;
            }
        };

        UnivariateFunction oriFunc = new UnivariateFunction() {
            @Override
            public double value(double x) {
                double d = actFunc.value(x);
                return d * d;
            }
        };

        UnivariateFunction expFuncSq = new UnivariateFunction() {
            @Override
            public double value(double x) {
                double d = expFunc.value(x);
                return d * d;
            }
        };
        SimpsonIntegrator integrator = new SimpsonIntegrator();
        double actValue = integrator.integrate(10000, oriFunc, 0, 1);
        double errValue = integrator.integrate(10000, func, 0, 1);
        double expValue = integrator.integrate(10000, expFuncSq, 0, 1);
        return new double[] { errValue, actValue, expValue };
    }

    public double[] integrateStrainErrorSquareOnCurve(GenericFunction<Double, double[]> curve, int valueIndex) {
        final UnivariateFunction actFunc = new NumericalStrainOnCurve(curve, valueIndex);
        final UnivariateFunction expFunc = new PreciseStrainOnCurve(curve, valueIndex);
        UnivariateFunction func = new UnivariateFunction() {
            @Override
            public double value(double x) {
                double d = (actFunc.value(x) - expFunc.value(x));
                return d * d;
            }
        };

        UnivariateFunction oriFunc = new UnivariateFunction() {
            @Override
            public double value(double x) {
                double d = actFunc.value(x);
                return d * d;
            }
        };

        UnivariateFunction expFuncSq = new UnivariateFunction() {
            @Override
            public double value(double x) {
                double d = expFunc.value(x);
                return d * d;
            }
        };
        SimpsonIntegrator integrator = new SimpsonIntegrator();
        double actValue = integrator.integrate(10000, oriFunc, 0, 1);
        double errValue = integrator.integrate(10000, func, 0, 1);
        double expValue = integrator.integrate(10000, expFuncSq, 0, 1);
        return new double[] { errValue, actValue, expValue };
    }

    public static final double SHRINK = 0.000001;

    public class CurveOnXAxis implements GenericFunction<Double, double[]> {

        @Override
        public double[] value(Double tD, double[] output) {
            if (null == output) {
                output = new double[2];
            }
            double t = tD;
            double left = timoshenkoCantileverPhysicalModel.getEdgePosition(MFRectangleEdge.LEFT);
            double right = timoshenkoCantileverPhysicalModel.getEdgePosition(MFRectangleEdge.RIGHT);
            left += SHRINK;
            right -= SHRINK;
            output[1] = 0;
            output[0] = left * (1 - t) + right * t;
            return output;
        }
    }

    public class ALineInsideRectangle implements GenericFunction<Double, double[]> {

        double[] start;
        double[] end;

        public double getLength() {
            return Math2D.distance(end, start);
        }

        public ALineInsideRectangle() {
            double left = timoshenkoCantileverPhysicalModel.getEdgePosition(MFRectangleEdge.LEFT);
            double right = timoshenkoCantileverPhysicalModel.getEdgePosition(MFRectangleEdge.RIGHT);
            double up = timoshenkoCantileverPhysicalModel.getEdgePosition(MFRectangleEdge.UP);
            double down = timoshenkoCantileverPhysicalModel.getEdgePosition(MFRectangleEdge.DOWN);

            start = new double[] { left + (right - left) * 0.22, down + (up - down) * 0.77 };
            end = new double[] { left + (right - left) * 0.81, down + (up - down) * 0.6 };
        }

        @Override
        public double[] value(Double input, double[] output) {
            return Math2D.pointOnSegment(start, end, input, output);
        }
    }

    public class CurveOnLeftSide implements GenericFunction<Double, double[]> {

        @Override
        public double[] value(Double tD, double[] output) {
            if (null == output) {
                output = new double[2];
            }
            double t = tD;

            double down = timoshenkoCantileverPhysicalModel.getEdgePosition(MFRectangleEdge.DOWN);
            double up = timoshenkoCantileverPhysicalModel.getEdgePosition(MFRectangleEdge.UP);
            down += SHRINK;
            up -= SHRINK;
            output[1] = down * (1 - t) + up * t;
            output[0] = SHRINK;
            return output;
        }
    }

    public class NumericalDisplacementOnCurve implements UnivariateFunction {

        GenericFunction<Double, double[]> curveFunction;
        int valueIndex;

        @Override
        public double value(double t) {
            double[] pt = curveFunction.value(t, null);
            double[] value = mechanicalPostProcessor.value(pt, null);
            return value[valueIndex];
        }

        public NumericalDisplacementOnCurve(GenericFunction<Double, double[]> curveFunction, int valueIndex) {
            this.curveFunction = curveFunction;
            this.valueIndex = valueIndex;
        }
    }

    public class PreciseDisplacementOnCurve implements UnivariateFunction {

        GenericFunction<Double, double[]> curveFunction;
        int valueIndex;

        @Override
        public double value(double t) {
            double[] pt = curveFunction.value(t, null);
            double[] value = timoshenkoCantileverPhysicalModel.displacement(pt[0], pt[1], 1, null);
            return value[valueIndex];
        }

        public PreciseDisplacementOnCurve(GenericFunction<Double, double[]> curveFunction, int valueIndex) {
            this.curveFunction = curveFunction;
            this.valueIndex = valueIndex;
        }
    }

    public class NumericalStrainOnCurve implements UnivariateFunction {

        GenericFunction<Double, double[]> curveFunction;
        int valueIndex;

        @Override
        public double value(double t) {
            double[] pt = curveFunction.value(t, null);
            double[] value = mechanicalPostProcessor.engineeringStrain(pt, null);
            return value[valueIndex];
        }

        public NumericalStrainOnCurve(GenericFunction<Double, double[]> curveFunction, int valueIndex) {
            this.curveFunction = curveFunction;
            this.valueIndex = valueIndex;
        }
    }

    public class PreciseStrainOnCurve implements UnivariateFunction {

        GenericFunction<Double, double[]> curveFunction;
        int valueIndex;

        @Override
        public double value(double t) {
            double[] pt = curveFunction.value(t, null);
            double[] value = timoshenkoCantileverPhysicalModel.strain(pt[0], pt[1], null);
            return value[valueIndex];
        }

        public PreciseStrainOnCurve(GenericFunction<Double, double[]> curveFunction, int valueIndex) {
            this.curveFunction = curveFunction;
            this.valueIndex = valueIndex;
        }
    }
}
