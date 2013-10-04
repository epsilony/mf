/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFSubdomain;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SegmentLoad;
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
    LineIntegratePointsFactory lineIntegratePointsFactory = new LineIntegratePointsFactory();
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
        Set<Map.Entry<GeomUnit, MFLoad>> entrySet = loadMap.entrySet();
        lineIntegratePointsFactory.setQuadratureDegree(quadratureDegree);

        LinkedList<MFIntegratePoint> neumannPts = new LinkedList<>();
        LinkedList<MFIntegratePoint> dirichletPts = new LinkedList<>();
        for (Map.Entry<GeomUnit, MFLoad> entry : entrySet) {
            GeomUnit geomUnit = entry.getKey();
            MFLoad load = entry.getValue();
            if (geomUnit instanceof Line) {
                lineIntegratePointsFactory.setLine((Line) geomUnit);
                SegmentLoad segLoad = (SegmentLoad) load;
                lineIntegratePointsFactory.setLoad(segLoad);
                if (segLoad.isDirichlet()) {
                    dirichletPts.addAll(lineIntegratePointsFactory.produce());
                } else {
                    neumannPts.addAll(lineIntegratePointsFactory.produce());
                }
            }
        }
        rawMFIntegrateTask.setDirichletTasks(dirichletPts);
        rawMFIntegrateTask.setNeumannTasks(neumannPts);
    }
}
