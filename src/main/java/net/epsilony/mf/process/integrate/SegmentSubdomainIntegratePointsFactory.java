/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.model.SegmentSubdomain;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.process.integrate.point.RawMFBoundaryIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SegmentSubdomainIntegratePointsFactory implements Factory<List<MFIntegratePoint>> {

    Map<GeomUnit, MFLoad> loadMap;
    Segment2DQuadrature segment2DQuadrature = new Segment2DQuadrature();
    SegmentSubdomain segmentSubdomain;

    @Override
    public List<MFIntegratePoint> produce() {
        SegmentLoad load = (SegmentLoad) loadMap.get(segmentSubdomain.getSegment());

        load.setSegment(segmentSubdomain.getSegment());
        segment2DQuadrature.setSegment(segmentSubdomain.getSegment());
        segment2DQuadrature.setStartEndParameter(segmentSubdomain.getStartParameter(), segmentSubdomain.getEndParameter());
        ArrayList<MFIntegratePoint> results = new ArrayList<>();
        for (Segment2DQuadraturePoint qp : segment2DQuadrature) {
            RawMFBoundaryIntegratePoint pt = new RawMFBoundaryIntegratePoint();
            pt.setCoord(qp.coord);
            pt.setBoundary(qp.segment);
            pt.setWeight(qp.weight);
            pt.setBoundaryParameter(qp.segmentParameter);
            load.setParameter(qp.segmentParameter);
            pt.setLoad(load.getLoad());
            pt.setLoadValidity(load.getLoadValidity());

            results.add(pt);
        }
        return results;

    }

    public void setLoadMap(Map<GeomUnit, MFLoad> loadMap) {
        this.loadMap = loadMap;
    }

    public void setSegmentSubdomain(SegmentSubdomain segmentSubdomain) {
        this.segmentSubdomain = segmentSubdomain;
    }

    public void setDegree(int degree) {
        segment2DQuadrature.setDegree(degree);
    }

    public int getDegree() {
        return segment2DQuadrature.getDegree();
    }
}
