/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.geomodel.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.ProcessResult;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFProject {

    ProcessResult getProcessResult();

    MFSolver getMFSolver();

    Assembler getAssembler();

    MFIntegrateTask getMFQuadratureTask();

    GeomModel2D getModel();

    MFShapeFunction getShapeFunction();

    InfluenceRadiusCalculator getInfluenceRadiusCalculator();
}
