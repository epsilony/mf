/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import java.util.Map;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.util.MFConstants;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMfProject implements MFProject {

    protected MFIntegrateTask mfIntegrateTask;
    protected AnalysisModel model;
    protected MFShapeFunction shapeFunction = MFConstants.defaultMFShapeFunction();
    protected Map<MFProcessType, Assembler> assemblersGroup;
    private MFSolver solver = MFConstants.defaultMFSolver();
    protected InfluenceRadiusCalculator influenceRadiusCalculator;
    int valueDimension;
    int spatialDimension;

    @Override
    public MFIntegrateTask getMFIntegrateTask() {
        return mfIntegrateTask;
    }

    public void setMFIntegrateTask(MFIntegrateTask task) {
        this.mfIntegrateTask = task;
    }

    @Override
    public AnalysisModel getModel() {
        return model;
    }

    public void setModel(AnalysisModel model) {
        this.model = model;
    }

    @Override
    public MFShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    @Override
    public Map<MFProcessType, Assembler> getAssemblersGroup() {
        return assemblersGroup;
    }

    public void setAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroup) {
        this.assemblersGroup = assemblersGroup;
    }

    @Override
    public int getValueDimension() {
        return valueDimension;
    }

    public void setValueDimension(int valueDimension) {
        this.valueDimension = valueDimension;
    }

    @Override
    public int getSpatialDimension() {
        return spatialDimension;
    }

    public void setSpatialDimension(int spatialDimension) {
        this.spatialDimension = spatialDimension;
    }

    public void setMFSolver(MFSolver solver) {
        this.solver = solver;
    }

    @Override
    public MFSolver getMFSolver() {
        return solver;
    }

    @Override
    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }
}
