/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFBoundaryIntegratePoint extends SimpMFIntegratePoint implements MFBoundaryIntegratePoint {

    Segment boundary;
    double boundaryParameter;

    public SimpMFBoundaryIntegratePoint(Segment2DQuadraturePoint qp, double[] load, boolean[] loadValidity) {
        this.coord = qp.coord;
        this.weight = qp.weight;
        this.load = load;
        this.loadValidity = loadValidity;
        this.boundary = qp.segment;
        this.boundaryParameter = qp.segmentParameter;
    }

    public SimpMFBoundaryIntegratePoint() {
    }

    public void setBoundary(Segment boundary) {
        this.boundary = boundary;
    }

    public void setBoundaryParameter(double boundaryParameter) {
        this.boundaryParameter = boundaryParameter;
    }

    @Override
    public Segment getBoundary() {
        return boundary;
    }

    @Override
    public double getBoundaryParameter() {
        return boundaryParameter;
    }
}
