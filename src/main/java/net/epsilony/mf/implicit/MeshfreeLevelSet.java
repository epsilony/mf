/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

import net.epsilony.mf.geomodel.Model2D;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.SimpMfProject;
import net.epsilony.mf.process.MFQuadratureTask;
import net.epsilony.tb.analysis.DifferentiableFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.common_func.RadialFunctionCore;
import net.epsilony.mf.shape_func.MFShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MeshfreeLevelSet {

    LevelSetApproximationAssembler assembler = new LevelSetApproximationAssembler();
    MFShapeFunction shapeFunction = new MLS();
    SimpMfProject mfProject = new SimpMfProject();

    public void setWeightFunction(RadialFunctionCore weightFunction) {
        assembler.setWeightFunction(weightFunction);
    }

    public void setMFQuadratureTask(MFQuadratureTask mfQuadratureTask) {
        mfProject.setMFQuadratureTask(mfQuadratureTask);
    }

    public void setModel(Model2D model) {
        mfProject.setModel(model);
    }

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    public void prepare() {


        mfProject.setAssembler(assembler);

        mfProject.setShapeFunction(shapeFunction);

        mfProject.process();
        mfProject.solve();
    }

    public DifferentiableFunction getLevelSetFunction() {
        final PostProcessor postProcessor = mfProject.genPostProcessor();
        return new DifferentiableFunction() {
            @Override
            public double[] value(double[] input, double[] output) {
                double[] result = postProcessor.value(input, null);
                if (null == output) {
                    return result;
                } else {
                    System.arraycopy(result, 0, output, 0, result.length);
                    return output;
                }
            }

            @Override
            public int getInputDimension() {
                return 2;
            }

            @Override
            public int getOutputDimension() {
                return 1;
            }

            @Override
            public int getDiffOrder() {
                return postProcessor.getDiffOrder();
            }

            @Override
            public void setDiffOrder(int diffOrder) {
                postProcessor.setDiffOrder(diffOrder);
            }
        };
    }
}
