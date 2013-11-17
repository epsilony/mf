/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.project;

import static net.epsilony.mf.project.MFProjectKey.ANALYSIS_MODEL;
import static net.epsilony.mf.project.MFProjectKey.ASSEMBLERS_GROUP;
import static net.epsilony.mf.project.MFProjectKey.INFLUENCE_RADIUS_CALCULATOR;
import static net.epsilony.mf.project.MFProjectKey.INTEGRATE_UNITS_GROUP;
import static net.epsilony.mf.project.MFProjectKey.SPATIAL_DIMENSION;
import static net.epsilony.mf.project.MFProjectKey.VALUE_DIMENSION;

import java.util.List;
import java.util.Map;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.ChainPhysicalModel;
import net.epsilony.mf.model.factory.ChainAnalysisModelFactory;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.assembler.Assemblers;
import net.epsilony.mf.process.integrate.ChainIntegrateTaskFactory;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.tb.Factory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneDPoissonProjectFactory implements Factory<MFProject> {

    public static final double DEFAULT_INFLUENCE_RADIUS_RATIO = 3.5;
    public static final int DEFAULT_NODES_NUM = 11;
    ChainIntegrateTaskFactory integrateTaskFactory = new ChainIntegrateTaskFactory();
    SimpMFProject result;
    double influenceRadRatio = DEFAULT_INFLUENCE_RADIUS_RATIO;
    ChainPhysicalModel chainPhysicalModel;
    int nodesNum = DEFAULT_NODES_NUM;

    @Override
    public MFProject produce() {
        result = new SimpMFProject();

        result.put(VALUE_DIMENSION, 1);

        result.put(SPATIAL_DIMENSION, 1);

        result.put(ASSEMBLERS_GROUP, Assemblers.poissonAssemblersGroup());

        result.put(INFLUENCE_RADIUS_CALCULATOR, new ConstantInfluenceRadiusCalculator(getInfluenceRadius()));

        result.put(ANALYSIS_MODEL, genAnalysisModel());

        result.put(INTEGRATE_UNITS_GROUP, genIntegrateUnitsGroup());

        return result;
    }

    public ChainPhysicalModel getChainPhysicalModel() {
        return chainPhysicalModel;
    }

    public void setChainPhysicalModel(ChainPhysicalModel chainPhysicalModel) {
        this.chainPhysicalModel = chainPhysicalModel;
    }

    public double getStart() {
        return chainPhysicalModel.getTerminalPoistion(true);
    }

    public double getEnd() {
        return chainPhysicalModel.getTerminalPoistion(false);
    }

    public double getInfluenceRadius() {
        double radials = (getEnd() - getStart()) / (nodesNum - 1) * influenceRadRatio;
        if (radials <= 0) {
            throw new IllegalStateException();
        }
        return radials;
    }

    public AnalysisModel genAnalysisModel() {
        ChainAnalysisModelFactory chainModelFactory = new ChainAnalysisModelFactory();
        chainModelFactory.setChainPhysicalModel(chainPhysicalModel);
        chainModelFactory.setFractionLengthCap((getEnd() - getStart()) / (nodesNum - 1.1));

        return chainModelFactory.produce();
    }

    private Map<MFProcessType, List<MFIntegrateUnit>> genIntegrateUnitsGroup() {
        integrateTaskFactory.setChainAnalysisModel((AnalysisModel) result.getDatas().get(ANALYSIS_MODEL));
        return integrateTaskFactory.produce();
    }

    public int getQuadratureDegree() {
        return integrateTaskFactory.getQuadratureDegree();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        integrateTaskFactory.setQuadratureDegree(quadratureDegree);
    }

    public double getInfluenceRadRatio() {
        return influenceRadRatio;
    }

    public void setInfluenceRadRatio(double influenceRadRatio) {
        this.influenceRadRatio = influenceRadRatio;
    }

    public int getNodesNum() {
        return nodesNum;
    }

    public void setNodesNum(int nodesNum) {
        this.nodesNum = nodesNum;
    }
}
