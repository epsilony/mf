/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

import net.epsilony.mf.model.Model2D;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.WeakformProcessor;
import net.epsilony.mf.process.WeakformProcessorFactory;
import net.epsilony.mf.process.WeakformQuadratureTask;
import net.epsilony.tb.analysis.DifferentiableFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.common_func.RadialFunctionCore;
import net.epsilony.mf.shape_func.MFShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MeshfreeLevelSet {

    LevelSetApproximationAssemblier assemblier = new LevelSetApproximationAssemblier();
    MFShapeFunction shapeFunction = new MLS();
    WeakformProcessorFactory weakformProcessorFactory = new WeakformProcessorFactory();

    public void setWeightFunction(RadialFunctionCore weightFunction) {
        assemblier.setWeightFunction(weightFunction);
    }

    public void setWeakformQuadratureTask(WeakformQuadratureTask weakformQuadratureTask) {
        weakformProcessorFactory.setWeakformQuadratureTask(weakformQuadratureTask);
    }

    public void setModel(Model2D model) {
        weakformProcessorFactory.setModel(model);
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        weakformProcessorFactory.setInfluenceRadiusCalculator(influenceRadiusCalculator);
    }

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    public void prepare() {


        weakformProcessorFactory.setAssemblier(assemblier);

        weakformProcessorFactory.setShapeFunction(shapeFunction);
        WeakformProcessor processor = weakformProcessorFactory.produce();

        processor.process();
        processor.solve(weakformProcessorFactory.getModelNodes(), weakformProcessorFactory.getExtraLagNodes());
    }

    public DifferentiableFunction getLevelSetFunction() {
        final PostProcessor postProcessor = weakformProcessorFactory.postProcessor();
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
