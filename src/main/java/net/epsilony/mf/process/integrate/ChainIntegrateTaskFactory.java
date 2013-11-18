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

package net.epsilony.mf.process.integrate;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.unit.GeomUnitSubdomain;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.process.integrate.unit.NodeIntegrateUnit;
import net.epsilony.mf.process.integrate.unit.RawMFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.unit.SubLineDomain;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ChainIntegrateTaskFactory implements Factory<Map<MFProcessType, List<MFIntegrateUnit>>> {

    public static final int DEFAULT_QUADRATURE_DEGREE = 2;
    Map<MFProcessType, List<MFIntegrateUnit>> integrateUnitsGroup;
    int quadratureDegree = DEFAULT_QUADRATURE_DEGREE;
    AnalysisModel chainAnalysisModel;

    @Override
    public Map<MFProcessType, List<MFIntegrateUnit>> produce() {
        integrateUnitsGroup = new EnumMap<>(MFProcessType.class);
        genVolumeTasks();
        genBoundaryTasks();
        return integrateUnitsGroup;
    }

    private void genVolumeTasks() {
        List<? extends MFIntegrateUnit> subdomains = chainAnalysisModel.getIntegrateUnitsGroup().get(
                MFProcessType.VOLUME);
        LineIntegratePointsFactory lineIntFac = new LineIntegratePointsFactory();
        lineIntFac.setDegree(quadratureDegree);
        lineIntFac.setLoadMap(chainAnalysisModel.getLoadMap());
        lineIntFac.setFetchLoadRecursively(true);
        LinkedList<MFIntegrateUnit> volPts = new LinkedList<>();
        for (MFIntegrateUnit subdomain : subdomains) {
            if (subdomain instanceof SubLineDomain) {
                SubLineDomain subLineDomain = (SubLineDomain) subdomain;
                lineIntFac.setStartLine((Line) subLineDomain.getStartSegment());
                lineIntFac.setStartParameter(subLineDomain.getStartParameter());
                lineIntFac.setEndLine((Line) subLineDomain.getEndSegment());
                lineIntFac.setEndParameter(subLineDomain.getEndParameter());
            } else if (subdomain instanceof GeomUnitSubdomain) {
                GeomUnitSubdomain segSubdomain = (GeomUnitSubdomain) subdomain;
                lineIntFac.setStartLine((Line) segSubdomain.getGeomUnit());
                lineIntFac.setStartParameter(0);
                lineIntFac.setEndLine(null);
                lineIntFac.setEndParameter(1);
            }
            volPts.addAll(lineIntFac.produce());
        }
        integrateUnitsGroup.put(MFProcessType.VOLUME, volPts);
    }

    private void genBoundaryTasks() {
        LinkedList<MFIntegrateUnit> diriPts = new LinkedList<>();
        LinkedList<MFIntegrateUnit> neuPts = new LinkedList<>();
        Map<GeomUnit, MFLoad> loadMap = chainAnalysisModel.getLoadMap();
        // temporary method:
        LinkedList<MFIntegrateUnit> subdomains = new LinkedList<>(chainAnalysisModel.getIntegrateUnitsGroup().get(
                MFProcessType.DIRICHLET));
        subdomains.addAll(chainAnalysisModel.getIntegrateUnitsGroup().get(MFProcessType.NEUMANN));
        for (MFIntegrateUnit subdomain : subdomains) {
            NodeIntegrateUnit nodeSubdomain = (NodeIntegrateUnit) subdomain;
            RawMFBoundaryIntegratePoint pt = new RawMFBoundaryIntegratePoint();
            NodeLoad load = (NodeLoad) loadMap.get(nodeSubdomain.getNode());
            pt.setBoundary(nodeSubdomain.getNode());
            pt.setBoundaryParameter(0);
            pt.setCoord(nodeSubdomain.getNode().getCoord());
            pt.setLoad(load.getValue());
            pt.setLoadValidity(load.getValidity());
            pt.setWeight(1);
            if (load.isDirichlet()) {
                diriPts.add(pt);
            } else {
                neuPts.add(pt);
            }
        }
        integrateUnitsGroup.put(MFProcessType.DIRICHLET, diriPts);
        integrateUnitsGroup.put(MFProcessType.NEUMANN, neuPts);
    }

    public AnalysisModel getChainAnalysisModel() {
        return chainAnalysisModel;
    }

    public void setChainAnalysisModel(AnalysisModel chainAnalysisModel) {
        this.chainAnalysisModel = chainAnalysisModel;
    }

    public int getQuadratureDegree() {
        return quadratureDegree;
    }

    public void setQuadratureDegree(int quadratureDegree) {
        this.quadratureDegree = quadratureDegree;
    }
}
