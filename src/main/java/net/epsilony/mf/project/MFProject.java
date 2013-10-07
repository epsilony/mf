/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.shape_func.MFShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFProject {

    MFSolver getMFSolver();

    Assembler getAssembler();

    MFIntegrateTask getMFIntegrateTask();

    AnalysisModel getModel();

    MFShapeFunction getShapeFunction();

    InfluenceRadiusCalculator getInfluenceRadiusCalculator();
}
