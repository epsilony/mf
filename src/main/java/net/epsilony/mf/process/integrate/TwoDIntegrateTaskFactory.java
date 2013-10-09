/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.subdomain.MFSubdomain;
import net.epsilony.mf.model.subdomain.SegmentSubdomain;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TwoDIntegrateTaskFactory implements Factory<MFIntegrateTask> {

    private static final int DEFAULT_QUADRATURE_DEGREE = 2;
    AnalysisModel analysisModel;
    RawMFIntegrateTask rawMFIntegrateTask;
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
    public MFIntegrateTask produce() {
        rawMFIntegrateTask = new RawMFIntegrateTask();
        generateVolumePoints();
        generateBoundaryPoints();
        return rawMFIntegrateTask;
    }

    private void generateVolumePoints() {
        volumeFactory.setQuadratureDegree(quadratureDegree);
        List<MFSubdomain> subdomains = analysisModel.getSubdomains(2);
        LinkedList<MFIntegratePoint> volumeTasks = new LinkedList<>();
        volumeFactory.setVolumeLoad(analysisModel.getFractionizedModel().getLoadMap().get(analysisModel.getFractionizedModel().getGeomRoot()));//TODO use sudomain instead!
        for (MFSubdomain subdomain : subdomains) {
            volumeFactory.setQuadratueDomain(subdomain);
            volumeTasks.addAll(volumeFactory.produce());
        }
        rawMFIntegrateTask.setVolumeTasks(volumeTasks);
    }

    private void generateBoundaryPoints() {
        Map<GeomUnit, MFLoad> loadMap = analysisModel.getFractionizedModel().getLoadMap();
        lineIntFac.setLoadMap(loadMap);
        lineIntFac.setQuadratureDegree(quadratureDegree);


        LinkedList<MFIntegratePoint> neumannPts = new LinkedList<>();
        LinkedList<MFIntegratePoint> dirichletPts = new LinkedList<>();
        for (MFSubdomain subdomain : analysisModel.getSubdomains(1)) {
            SegmentSubdomain segSubdomain = (SegmentSubdomain) subdomain;
            lineIntFac.setStartLine((Line) segSubdomain.getStartSegment());
            lineIntFac.setStartParameter(segSubdomain.getStartParameter());
            lineIntFac.setEndLine((Line) segSubdomain.getEndSegment());
            lineIntFac.setEndParameter(segSubdomain.getEndParameter());
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
        rawMFIntegrateTask.setDirichletTasks(dirichletPts);
        rawMFIntegrateTask.setNeumannTasks(neumannPts);
    }
}
