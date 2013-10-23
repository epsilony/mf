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

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFProject {

    int getValueDimension();

    int getSpatialDimension();

    MFSolver getMFSolver();

    Map<MFProcessType, Assembler> getAssemblersGroup();

    MFIntegrateTask getMFIntegrateTask();

    AnalysisModel getModel();

    MFShapeFunction getShapeFunction();

    InfluenceRadiusCalculator getInfluenceRadiusCalculator();
}
