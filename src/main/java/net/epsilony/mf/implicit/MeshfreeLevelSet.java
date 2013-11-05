/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

import java.util.Map;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.project.SimpMFProject;
import net.epsilony.tb.analysis.DifferentiableFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.tb.common_func.RadialBasisCore;
import net.epsilony.mf.shape_func.MFShapeFunction;
import static net.epsilony.mf.project.MFProjectKey.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MeshfreeLevelSet {

    LevelSetApproximationAssembler assembler = new LevelSetApproximationAssembler();
    MFShapeFunction shapeFunction = new MLS();
    SimpMFProject mfProject = new SimpMFProject();
    protected MFLinearMechanicalProcessor processor;

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        mfProject.put(INFLUENCE_RADIUS_CALCULATOR, influenceRadiusCalculator);
    }

    public void setWeightFunction(RadialBasisCore weightFunction) {
        assembler.setWeightFunction(weightFunction);
    }

    public void setMFQuadratureTask(Map<MFProcessType, MFIntegrateUnit> mfQuadratureTask) {
        mfProject.put(INTEGRATE_UNITS_GROUP, mfQuadratureTask);
    }

    public void setModel(AnalysisModel model) {
        mfProject.put(ANALYSIS_MODEL, model);
    }

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    public void prepare() {

//        mfProject.setAssembler(assembler);
        mfProject.put(SHAPE_FUNCTION, shapeFunction);
        processor = new MFLinearMechanicalProcessor();
        processor.setProject(mfProject);
        processor.preprocess();
        processor.solve();
    }

    public DifferentiableFunction getLevelSetFunction() {
        final PostProcessor postProcessor = processor.genPostProcessor();
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
