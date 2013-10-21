/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFNodeSubdomain;
import net.epsilony.mf.model.subdomain.MFSubdomain;
import net.epsilony.mf.model.subdomain.SegmentSubdomain;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.model.subdomain.MFSubdomainType;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.process.integrate.point.RawMFBoundaryIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ChainIntegrateTaskFactory implements Factory<MFIntegrateTask> {

    public static final int DEFAULT_QUADRATURE_DEGREE = 2;
    RawMFIntegrateTask rawMFIntegrateTask;
    int quadratureDegree = DEFAULT_QUADRATURE_DEGREE;
    AnalysisModel chainAnalysisModel;

    @Override
    public MFIntegrateTask produce() {
        rawMFIntegrateTask = new RawMFIntegrateTask();
        genVolumeTasks();
        genBoundaryTasks();
        return rawMFIntegrateTask;
    }

    private void genVolumeTasks() {
        List<MFSubdomain> subdomains = chainAnalysisModel.getSubdomains(MFSubdomainType.VOLUME);
        LineIntegratePointsFactory lineIntFac = new LineIntegratePointsFactory();
        lineIntFac.setDegree(quadratureDegree);
        lineIntFac.setLoadMap(chainAnalysisModel.getFractionizedModel().getLoadMap());
        lineIntFac.setFetchLoadRecursively(true);
        LinkedList<MFIntegratePoint> volPts = new LinkedList<>();
        for (MFSubdomain subdomain : subdomains) {
            SegmentSubdomain segSubdomain = (SegmentSubdomain) subdomain;
            lineIntFac.setStartLine((Line) segSubdomain.getStartSegment());
            lineIntFac.setStartParameter(segSubdomain.getStartParameter());
            lineIntFac.setEndLine((Line) segSubdomain.getEndSegment());
            lineIntFac.setEndParameter(segSubdomain.getEndParameter());
            volPts.addAll(lineIntFac.produce());
        }
        rawMFIntegrateTask.setVolumeTasks(volPts);
    }

    private void genBoundaryTasks() {
        LinkedList<MFIntegratePoint> diriPts = new LinkedList<>();
        LinkedList<MFIntegratePoint> neuPts = new LinkedList<>();
        Map<GeomUnit, MFLoad> loadMap = chainAnalysisModel.getFractionizedModel().getLoadMap();
        //temperory method: 
        LinkedList<MFSubdomain> subdomains = new LinkedList<>(chainAnalysisModel.getSubdomains(MFSubdomainType.DIRICHLET));
        subdomains.addAll(chainAnalysisModel.getSubdomains(MFSubdomainType.NEUMANN));
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
        rawMFIntegrateTask.setDirichletTasks(diriPts);
        rawMFIntegrateTask.setNeumannTasks(neuPts);
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
