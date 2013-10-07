/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFNodeSubdomain;
import net.epsilony.mf.model.MFSubdomain;
import net.epsilony.mf.model.SegmentSubdomain;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.process.integrate.point.RawMFBoundaryIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ChainIntegrateTaskFactory implements Factory<MFIntegrateTask> {

    RawMFIntegrateTask rawMFIntegrateTask;
    int quadratureDegree;
    AnalysisModel chainAnalysisModel;

    @Override
    public MFIntegrateTask produce() {
        rawMFIntegrateTask = new RawMFIntegrateTask();
        genVolumeTasks();
        genBoundaryTasks();
        return rawMFIntegrateTask;
    }

    private void genVolumeTasks() {
        List<MFSubdomain> subdomains = chainAnalysisModel.getSubdomains(1);
        SegmentSubdomainIntegratePointsFactory segIntegratePointsFactory = new SegmentSubdomainIntegratePointsFactory();
        segIntegratePointsFactory.setDegree(quadratureDegree);
        segIntegratePointsFactory.setLoadMap(chainAnalysisModel.getFractionizedModel().getLoadMap());
        segIntegratePointsFactory.setFetchLoadRecursively(true);
        LinkedList<MFIntegratePoint> volPts = new LinkedList<>();
        for (MFSubdomain subdomain : subdomains) {
            SegmentSubdomain segSubdomain = (SegmentSubdomain) subdomain;
            segIntegratePointsFactory.setSegmentSubdomain(segSubdomain);
            volPts.addAll(segIntegratePointsFactory.produce());
        }
        rawMFIntegrateTask.setVolumeTasks(volPts);
    }

    private void genBoundaryTasks() {
        LinkedList<MFIntegratePoint> diriPts = new LinkedList<>();
        LinkedList<MFIntegratePoint> neuPts = new LinkedList<>();
        Map<GeomUnit, MFLoad> loadMap = chainAnalysisModel.getFractionizedModel().getLoadMap();
        for (MFSubdomain subdomain : chainAnalysisModel.getSubdomains(0)) {
            MFNodeSubdomain nodeSubdomain = (MFNodeSubdomain) subdomain;
            RawMFBoundaryIntegratePoint pt = new RawMFBoundaryIntegratePoint();
            NodeLoad load = (NodeLoad) loadMap.get(nodeSubdomain.getNode());
            pt.setBoundary(nodeSubdomain.getNode());
            pt.setBoundaryParameter(0);
            pt.setCoord(nodeSubdomain.getNode().getCoord());
            pt.setLoad(load.getLoad());
            pt.setLoadValidity(load.getLoadValidity());
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
