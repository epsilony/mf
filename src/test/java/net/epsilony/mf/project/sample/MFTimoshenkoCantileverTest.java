/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import net.epsilony.mf.model.MFRectangleEdge;
import net.epsilony.mf.model.Rectangle2DModel;
import net.epsilony.mf.process.integrate.RectangleTask;
import net.epsilony.mf.model.influence.EnsureNodesNum;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.analysis.Math2D;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFTimoshenkoCantileverTest {

    public MFTimoshenkoCantileverTest() {
    }

    @Test
    public void testErrSquareIntegrationOnXAxis() {
        System.out.println("test Timoshenko standard beam, x axis");
        genTimoshenkoStandardCantileverProcessor();
        processAndGenPostProcessor();
        mechanicalPostProcessor.setDiffOrder(0);

        CurveOnXAxis xAxisCure = new CurveOnXAxis();
        int valueIndex = 1;//y direction displacement
        double[] results = integrateDisplacementErrorSquareOnCurve(xAxisCure, valueIndex);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-11 * accurateValue;
        System.out.println("err = " + err);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
        //assertEquals(1.0728621297419604E-16, err, 1e-16); //typical value
    }

    @Test
    public void testErrSquareIntegrationOnLeftSide() {
        System.out.println("test Timoshinko standard beam, left edge");
        genTimoshenkoStandardCantileverProcessor();
        processAndGenPostProcessor();
        mechanicalPostProcessor.setDiffOrder(0);
        CurveOnLeftSide curve = new CurveOnLeftSide();
        int valueIndex = 1;
        double[] results = integrateDisplacementErrorSquareOnCurve(curve, valueIndex);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-8 * accurateValue;
        System.out.println("err = " + err);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
        //assertEquals(5.9676721435783116E-18, err, 1e-18); //typical value
    }

    @Test
    public void testOnLeftSide_EnsureNodesNum() {
        System.out.println("test Timoshinko standard beam, left edge");
        genTimoshenkoStandardCantileverProcessor();
        mfMechanicalProject.setInfluenceRadiusCalculator(new EnsureNodesNum(4, 10));
        processAndGenPostProcessor();
        mechanicalPostProcessor.setDiffOrder(0);
        CurveOnLeftSide curve = new CurveOnLeftSide();
        int valueIndex = 1;
        double[] results = integrateDisplacementErrorSquareOnCurve(curve, valueIndex);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-7 * accurateValue;
        System.out.println("err = " + err);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
    }

    @Test
    public void testOnXAxis_EnsureNodesNum() {
        System.out.println("test Timoshenko standard beam, x axis");
        genTimoshenkoStandardCantileverProcessor();
        mfMechanicalProject.setInfluenceRadiusCalculator(new EnsureNodesNum(4, 10));
        processAndGenPostProcessor();
        mechanicalPostProcessor.setDiffOrder(0);

        CurveOnXAxis xAxisCure = new CurveOnXAxis();
        int valueIndex = 1;
        double[] results = integrateDisplacementErrorSquareOnCurve(xAxisCure, valueIndex);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-7 * accurateValue;
        System.out.println("err = " + err);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
    }

    @Test
    public void textOnALineInsideBeam() {
        System.out.println("test Timoshenko standard beam, on a given line");
        genTimoshenkoStandardCantileverProcessor();
        mfMechanicalProject.setInfluenceRadiusCalculator(new EnsureNodesNum(4, 10));
        processAndGenPostProcessor();
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
        return new double[]{errValue, actValue, expValue};
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
        return new double[]{errValue, actValue, expValue};
    }
    MechanicalPostProcessor mechanicalPostProcessor;
    TimoshenkoBeamProjectFactory timoFactory;
    SimpMFMechanicalProject mfMechanicalProject;

    public void genTimoshenkoStandardCantileverProcessor() {
        timoFactory = SimpMFMechanicalProject.genTimoshenkoProjectFactory();
        mfMechanicalProject = (SimpMFMechanicalProject) timoFactory.produce();
    }

    private void processAndGenPostProcessor() {
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(mfMechanicalProject);
        System.out.println("Multi Processing: " + processor.isActuallyMultiThreadable());
        processor.preprocess();
        processor.solve();
        mechanicalPostProcessor = processor.genMechanicalPostProcessor();
    }
    public static final double SHRINK = 0.000001;

    public class CurveOnXAxis implements GenericFunction<Double, double[]> {

        @Override
        public double[] value(Double tD, double[] output) {
            if (null == output) {
                output = new double[2];
            }
            double t = tD;

            RectangleTask timoTask = timoFactory.rectangleTask;
            Rectangle2DModel timoModel = timoFactory.rectangleModel;
            double left = timoModel.getRectangleGM().getEdgePosition(MFRectangleEdge.LEFT);
            double right = timoModel.getRectangleGM().getEdgePosition(MFRectangleEdge.RIGHT);
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
            RectangleTask timoTask = timoFactory.rectangleTask;
            Rectangle2DModel timoModel = timoFactory.rectangleModel;
            double left = timoModel.getRectangleGM().getEdgePosition(MFRectangleEdge.LEFT);
            double right = timoModel.getRectangleGM().getEdgePosition(MFRectangleEdge.RIGHT);
            double up = timoModel.getRectangleGM().getEdgePosition(MFRectangleEdge.UP);
            double down = timoModel.getRectangleGM().getEdgePosition(MFRectangleEdge.DOWN);

            start = new double[]{left + (right - left) * 0.22, down + (up - down) * 0.77};
            end = new double[]{left + (right - left) * 0.81, down + (up - down) * 0.6};
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

            double down = timoFactory.rectangleModel.getRectangleGM().getEdgePosition(MFRectangleEdge.DOWN);
            double up = timoFactory.rectangleModel.getRectangleGM().getEdgePosition(MFRectangleEdge.UP);
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
            double[] value = timoFactory.timoBeam.displacement(pt[0], pt[1], 1, null);
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
            double[] value = timoFactory.timoBeam.strain(pt[0], pt[1], null);
            return value[valueIndex];
        }

        public PreciseStrainOnCurve(GenericFunction<Double, double[]> curveFunction, int valueIndex) {
            this.curveFunction = curveFunction;
            this.valueIndex = valueIndex;
        }
    }
}
