/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.ArrayList;
import java.util.List;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.process.integrate.point.RawMFBoundaryIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.Line;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LineIntegratePointsFactory implements Factory<List<MFIntegratePoint>> {

    Segment2DQuadrature segment2DQuadrature = new Segment2DQuadrature();
    SegmentLoad load;
    Line line;

    public void setLoad(SegmentLoad load) {
        this.load = load;
    }

    public void setLine(Line line) {
        segment2DQuadrature.setSegment(line);
        this.line = line;
    }

    public void setQuadratureDegree(int quadratureDegree) {
        segment2DQuadrature.setDegree(quadratureDegree);
    }

    @Override
    public List<MFIntegratePoint> produce() {
        load.setSegment(line);
        ArrayList<MFIntegratePoint> results = new ArrayList<>();
        for (Segment2DQuadraturePoint qp : segment2DQuadrature) {
            RawMFBoundaryIntegratePoint pt = new RawMFBoundaryIntegratePoint();
            pt.setCoord(qp.coord);
            pt.setBoundary(qp.segment);
            pt.setBoundary(line);
            pt.setWeight(qp.weight);
            pt.setBoundaryParameter(qp.segmentParameter);
            load.setParameter(qp.segmentParameter);
            pt.setLoad(load.getLoad());
            pt.setLoadValidity(load.getLoadValidity());

            results.add(pt);
        }
        return results;
    }
}
