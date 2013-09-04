/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.process.integrate.MFIntegrateTask;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.geomodel.influence.InfluenceRadiusCalculator;
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
    protected GeomModel2D model;
    protected MFShapeFunction shapeFunction = MFConstants.defaultMFShapeFunction();
    protected Assembler assembler;
    private MFSolver solver = MFConstants.defaultMFSolver();
    protected InfluenceRadiusCalculator influenceRadiusCalculator;

    @Override
    public MFIntegrateTask getMFIntegrateTask() {
        return mfIntegrateTask;
    }

    public void setMFIntegrateTask(MFIntegrateTask task) {
        this.mfIntegrateTask = task;
    }

    @Override
    public GeomModel2D getModel() {
        return model;
    }

    public void setModel(GeomModel2D model) {
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
    public Assembler getAssembler() {
        return assembler;
    }

    public void setAssembler(Assembler assembler) {
        this.assembler = assembler;
    }

    public int getNodeValueDimension() {
        return assembler.getDimension();
    }

    public List<MFNode> getModelNodes() {
        return model.getAllNodes();
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
