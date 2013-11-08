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
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.model.subdomain.GeomUnitSubdomain;
import net.epsilony.mf.model.subdomain.MFSubdomain;
import net.epsilony.mf.model.subdomain.SubLineDomain;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.unit.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TwoDIntegrateTaskFactory implements Factory<Map<MFProcessType, List<MFIntegrateUnit>>> {

    private static final int DEFAULT_QUADRATURE_DEGREE = 2;
    AnalysisModel analysisModel;
    Map<MFProcessType, List<MFIntegrateUnit>> integrateUnitsGroup;
    int quadratureDegree = DEFAULT_QUADRATURE_DEGREE;
    LineIntegratePointsFactory lineIntFac = new LineIntegratePointsFactory();
    NormalVolumeIntegratePointsFactory volumeFactory = new NormalVolumeIntegratePointsFactory();

    public void setAnalysisModel(AnalysisModel analysisModel) {
        this.analysisModel = analysisModel;
    }

    public int getQuadratureDegree() {
        return quadratureDegree;
    }

    public void setQuadratureDegree(int quadratureDegree) {
        this.quadratureDegree = quadratureDegree;
    }

    @Override
    public Map<MFProcessType, List<MFIntegrateUnit>> produce() {
        integrateUnitsGroup = new EnumMap<>(MFProcessType.class);
        generateVolumePoints();
        generateBoundaryPoints();
        return integrateUnitsGroup;
    }

    private void generateVolumePoints() {
        volumeFactory.setQuadratureDegree(quadratureDegree);
        List<MFSubdomain> subdomains = analysisModel.getSubdomains(MFProcessType.VOLUME);
        LinkedList<MFIntegrateUnit> volumeTasks = new LinkedList<>();
        volumeFactory.setVolumeLoad(analysisModel.getFractionizedModel().getLoadMap()
                .get(analysisModel.getFractionizedModel().getGeomRoot()));// TODO
                                                                          // use
                                                                          // subdomain
                                                                          // instead!
        for (MFSubdomain subdomain : subdomains) {
            volumeFactory.setQuadratueDomain(subdomain);
            volumeTasks.addAll(volumeFactory.produce());
        }
        integrateUnitsGroup.put(MFProcessType.VOLUME, volumeTasks);
    }

    private void generateBoundaryPoints() {
        Map<GeomUnit, MFLoad> loadMap = analysisModel.getFractionizedModel().getLoadMap();
        lineIntFac.setLoadMap(loadMap);
        lineIntFac.setDegree(quadratureDegree);

        LinkedList<MFIntegrateUnit> neumannPts = new LinkedList<>();
        LinkedList<MFIntegrateUnit> dirichletPts = new LinkedList<>();
        LinkedList<MFSubdomain> subdomains = new LinkedList<>(analysisModel.getSubdomains(MFProcessType.NEUMANN));
        subdomains.addAll(analysisModel.getSubdomains(MFProcessType.DIRICHLET));
        for (MFSubdomain subdomain : subdomains) {
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
            List<MFIntegratePoint> points = lineIntFac.produce();
            for (MFIntegratePoint point : points) {
                MFBoundaryIntegratePoint bp = (MFBoundaryIntegratePoint) point;
                MFLoad load = loadMap.get(bp.getBoundary());
                if (null == load) {
                    throw new IllegalStateException();
                }
                SegmentLoad segLoad = (SegmentLoad) load;
                if (segLoad.isDirichlet()) {
                    dirichletPts.add(point);
                } else {
                    neumannPts.add(point);
                }
            }
        }
        integrateUnitsGroup.put(MFProcessType.DIRICHLET, dirichletPts);
        integrateUnitsGroup.put(MFProcessType.NEUMANN, neumannPts);
    }
}
