/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.process.integrate;

import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.process.integrate.unit.RawMFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.unit.RawMFIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 *
 * @author epsilon
 */
public class LinearIntegratePointsFactory implements Factory<List<MFIntegratePoint>> {

    Segment2DQuadrature segment2DQuadrature;
    private Line lineForGaussianQuadrature;

    public LinearIntegratePointsFactory() {
        init_segment2DQuadrature();
    }

    private void init_segment2DQuadrature() {
        lineForGaussianQuadrature = new Line(new Node(new double[2]));
        Line succ = new Line(new Node(new double[2]));
        Segment2DUtils.link(lineForGaussianQuadrature, succ);

        segment2DQuadrature = new Segment2DQuadrature();
        segment2DQuadrature.setSegment(lineForGaussianQuadrature);
    }

    public void setStartCoord(double[] startCoord) {
        lineForGaussianQuadrature.getStart().setCoord(startCoord);
    }

    public void setEndCoord(double[] endCoord) {
        lineForGaussianQuadrature.getEnd().setCoord(endCoord);
    }

    public void setDegree(int degree) {
        segment2DQuadrature.setDegree(degree);
    }

    public int getNumOfPoints() {
        return segment2DQuadrature.getNumOfPoints();
    }

    public int getDegree() {
        return segment2DQuadrature.getDegree();
    }

    @Override
    public List<MFIntegratePoint> produce() {
        List<MFIntegratePoint> result = new LinkedList<>();
        for (Segment2DQuadraturePoint segPt : segment2DQuadrature) {
            result.add(genNewPoint(segPt));
        }
        return result;
    }

    private MFIntegratePoint genNewPoint(Segment2DQuadraturePoint segPt) {
        RawMFIntegratePoint point = new RawMFBoundaryIntegratePoint();
        point.setWeight(segPt.weight);
        point.setCoord(segPt.coord);
        point.setDimension(2);
        return point;
    }
}
