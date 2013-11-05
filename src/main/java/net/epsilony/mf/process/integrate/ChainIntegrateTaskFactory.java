/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFNodeSubdomain;
import net.epsilony.mf.model.subdomain.MFSubdomain;
import net.epsilony.mf.model.subdomain.SubLineDomain;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.model.subdomain.SegmentSubdomain;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFIntegrateUnit;
import net.epsilony.mf.process.integrate.point.RawMFBoundaryIntegratePoint;
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
        List<MFSubdomain> subdomains = chainAnalysisModel.getSubdomains(MFProcessType.VOLUME);
        LineIntegratePointsFactory lineIntFac = new LineIntegratePointsFactory();
        lineIntFac.setDegree(quadratureDegree);
        lineIntFac.setLoadMap(chainAnalysisModel.getFractionizedModel().getLoadMap());
        lineIntFac.setFetchLoadRecursively(true);
        LinkedList<MFIntegratePoint> volPts = new LinkedList<>();
        for (MFSubdomain subdomain : subdomains) {
            if (subdomain instanceof SubLineDomain) {
                SubLineDomain subLineDomain = (SubLineDomain) subdomain;
                lineIntFac.setStartLine((Line) subLineDomain.getStartSegment());
                lineIntFac.setStartParameter(subLineDomain.getStartParameter());
                lineIntFac.setEndLine((Line) subLineDomain.getEndSegment());
                lineIntFac.setEndParameter(subLineDomain.getEndParameter());
            } else if (subdomain instanceof SegmentSubdomain) {
                SegmentSubdomain segSubdomain = (SegmentSubdomain) subdomain;
                lineIntFac.setStartLine((Line) segSubdomain.getSegment());
                lineIntFac.setStartParameter(0);
                lineIntFac.setEndLine(null);
                lineIntFac.setEndParameter(1);
            }
            volPts.addAll(lineIntFac.produce());
        }
        integrateUnitsGroup.put(MFProcessType.VOLUME, (List) volPts);
    }

    private void genBoundaryTasks() {
        LinkedList<MFIntegratePoint> diriPts = new LinkedList<>();
        LinkedList<MFIntegratePoint> neuPts = new LinkedList<>();
        Map<GeomUnit, MFLoad> loadMap = chainAnalysisModel.getFractionizedModel().getLoadMap();
        //temperory method: 
        LinkedList<MFSubdomain> subdomains = new LinkedList<>(chainAnalysisModel.getSubdomains(MFProcessType.DIRICHLET));
        subdomains.addAll(chainAnalysisModel.getSubdomains(MFProcessType.NEUMANN));
        for (MFSubdomain subdomain : subdomains) {
            MFNodeSubdomain nodeSubdomain = (MFNodeSubdomain) subdomain;
            RawMFBoundaryIntegratePoint pt = new RawMFBoundaryIntegratePoint();
            NodeLoad load = (NodeLoad) loadMap.get(nodeSubdomain.getNode());
            pt.setBoundary(nodeSubdomain.getNode());
            pt.setBoundaryParameter(0);
            pt.setCoord(nodeSubdomain.getNode().getCoord());
            pt.setLoad(load.getLoad());
            pt.setLoadValidity(load.getLoadValidity());
            pt.setWeight(1);
            if (load.isDirichlet()) {
                diriPts.add(pt);
            } else {
                neuPts.add(pt);
            }
        }
        integrateUnitsGroup.put(MFProcessType.DIRICHLET, (List) diriPts);
        integrateUnitsGroup.put(MFProcessType.NEUMANN, (List) neuPts);
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
