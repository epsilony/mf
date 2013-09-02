/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.process.ProcessResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFProject {

    void process();

    void solve();

    ProcessResult getProcessResult();

    void setMFSolver(MFSolver solver);

    MFSolver getMFSolver();

    void setAssembler(Assembler assembler);

    Assembler getAssembler();

    void setMFQuadratureTask(MFIntegrateTask task);

    MFIntegrateTask getMFQuadratureTask();

    void setModel(GeomModel2D model);

    GeomModel2D getModel();

    void setShapeFunction(MFShapeFunction shapeFunction);

    MFShapeFunction getShapeFunction();
}
