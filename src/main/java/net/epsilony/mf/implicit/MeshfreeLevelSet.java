/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

import net.epsilony.mf.model.Model2D;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.WeakformProcessor;
import net.epsilony.mf.process.WeakformQuadratureTask;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.shape_func.MLS;
import net.epsilony.tb.shape_func.RadialFunctionCore;
import net.epsilony.tb.shape_func.ShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MeshfreeLevelSet {

    LevelSetApproximationAssemblier assemblier = new LevelSetApproximationAssemblier();
    ShapeFunction shapeFunction = new MLS();
    WeakformProcessor weakformProcessor = new WeakformProcessor();

    public void setWeightFunction(RadialFunctionCore weightFunction) {
        assemblier.setWeightFunction(weightFunction);
    }

    public void setWeakformQuadratureTask(WeakformQuadratureTask weakformQuadratureTask) {
        weakformProcessor.setWeakformQuadratureTask(weakformQuadratureTask);
    }

    public void setModel(Model2D model) {
        weakformProcessor.setModel(model);
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        weakformProcessor.setInfluenceRadiusCalculator(influenceRadiusCalculator);
    }

    public void setShapeFunction(ShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    public void prepare() {


        weakformProcessor.setAssemblier(assemblier);

        weakformProcessor.setShapeFunction(shapeFunction);
        weakformProcessor.prepare();

        weakformProcessor.process();
        weakformProcessor.solve();
    }

    public GenericFunction<double[], double[]> getLevelSetFunction() {
        final PostProcessor postProcessor = weakformProcessor.postProcessor();
        return new GenericFunction<double[], double[]>() {
            @Override
            public double[] value(double[] input, double[] output) {
                postProcessor.setDiffOrder(0);
                double[] result = postProcessor.value(input, null);
                if (null == output) {
                    return result;
                } else {
                    System.arraycopy(result, 0, output, 0, result.length);
                    return output;
                }
            }
        };
    }
}
