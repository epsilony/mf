/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.mf.process.integrate.RectangleTask;
import net.epsilony.tb.Factory;
import static java.lang.Math.*;
import java.util.Arrays;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.cons_law.PlaneStress;
import net.epsilony.mf.model.Rectangle2DModel;
import net.epsilony.mf.model.influence.EnsureNodesNum;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.assembler.MechanicalLagrangeAssembler;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.mf.util.MFConstants;
import net.epsilony.tb.analysis.GenericFunction;
import static net.epsilony.mf.model.MFRectangleEdge.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TensionBar implements Factory<SimpMFMechanicalProject> {

    RectangleTask rectangleTask;
    Rectangle2DModel rectangleModel;
    double height = 3;
    double width = 3;
    double x0 = 0, y0 = 0;
    double segLenSup = min(height, width) * 0.3;
    int quadratureDegree = 2;
    double tension = 2000;
    double E = 1000;
    double mu = 0.3;
    double spaceNodesDistance = segLenSup;

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getX0() {
        return x0;
    }

    public double getY0() {
        return y0;
    }

    @Override
    public SimpMFMechanicalProject produce() {
        prepare();
        MFShapeFunction shapeFunc = new MLS();
        ConstitutiveLaw constitutiveLaw = genConstitutiveLaw();
        MechanicalLagrangeAssembler assembler = new MechanicalLagrangeAssembler();
        assembler.setConstitutiveLaw(constitutiveLaw);
        InfluenceRadiusCalculator influenceRadsCalc = new EnsureNodesNum(segLenSup * 1.5, 10);
        //InfluenceRadiusCalculator influenceRadsCalc = new ConstantInfluenceRadiusCalculator(segLenSup * 2);
        SimpMFMechanicalProject result = new SimpMFMechanicalProject();
        result.setMFIntegrateTask(rectangleTask);
        result.setShapeFunction(shapeFunc);
        result.setAssembler(assembler);
        result.setConstitutiveLaw(constitutiveLaw);
        result.setInfluenceRadiusCalculator(influenceRadsCalc);
        result.setModel(rectangleModel);
        return result;
    }

    private void prepare() {
        rectangleModel = new Rectangle2DModel();
        rectangleModel.setDown(y0);
        rectangleModel.setUp(y0 + height);
        rectangleModel.setLeft(x0);
        rectangleModel.setRight(x0 + width);
        rectangleModel.setNodesDistanceUpperBound(segLenSup);

        rectangleTask = new RectangleTask();
        rectangleTask.setRectangleModel(rectangleModel);
        rectangleTask.setQuadratureDegree(quadratureDegree);
        rectangleTask.setVolumeSpecification(null, segLenSup);

        rectangleTask.addBoundaryConditionOnEdge(
                LEFT,
                new GenericFunction<double[], double[]>() {
            @Override
            public double[] value(double[] input, double[] output) {
                if (null == output) {
                    output = new double[2];
                } else {
                    Arrays.fill(output, 0);
                }
                return output;
            }
        },
                new GenericFunction<double[], boolean[]>() {
            @Override
            public boolean[] value(double[] input, boolean[] output) {
                if (null == output) {
                    output = new boolean[2];
                }
                output[0] = true;
                output[1] = false;
                return output;
            }
        });

        rectangleTask.addBoundaryConditionOnEdge(
                DOWN,
                new GenericFunction<double[], double[]>() {
            @Override
            public double[] value(double[] input, double[] output) {
                if (null == output) {
                    output = new double[2];
                } else {
                    Arrays.fill(output, 0);
                }
                return output;
            }
        },
                new GenericFunction<double[], boolean[]>() {
            @Override
            public boolean[] value(double[] input, boolean[] output) {
                if (null == output) {
                    output = new boolean[2];
                }
                output[0] = false;
                output[1] = true;
                return output;
            }
        });

        rectangleTask.addBoundaryConditionOnEdge(RIGHT, new GenericFunction<double[], double[]>() {
            @Override
            public double[] value(double[] input, double[] output) {
                if (null == output) {
                    output = new double[2];
                }
                output[0] = tension;
                output[1] = 0;
                return output;
            }
        }, null);

//        rectangleTask.prepareModelAndTask();
    }

    private ConstitutiveLaw genConstitutiveLaw() {
        return new PlaneStress(E, mu);
    }

    public static void main(String[] args) {

        TensionBar tensionBar = new TensionBar();
        SimpMFMechanicalProject project = tensionBar.produce();
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(project);
        processor.getSettings().put(MFConstants.KEY_ENABLE_MULTI_THREAD, false);
        processor.preprocess();
        processor.solve();

        PostProcessor pp = processor.genPostProcessor();
        MechanicalPostProcessor mpp = processor.genMechanicalPostProcessor();
        int stepNum = 20;

        for (int i = 0; i < stepNum; i++) {
            double[] pt = new double[]{2.99, 0.01 + 2.98 * (i * 1.0 / (stepNum - 1))};
            double[] strain = mpp.engineeringStrain(pt, null);
            double[] value = mpp.value(pt, null);

            System.out.println("pt = " + Arrays.toString(pt) + ", value = " + Arrays.toString(value) + ", eng strain = " + Arrays.toString(strain));
        }
    }
}
