/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.mf.geomodel.MFLineBnd;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.Line;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFBoundaryIntegratePoint extends SimpMFIntegratePoint implements MFBoundaryIntegratePoint {

    MFLineBnd boundary;
    double boundaryParameter;
    double[] outNormal;

    public SimpMFBoundaryIntegratePoint(Segment2DQuadraturePoint qp, double[] load, boolean[] loadValidity) {
        this.coord = qp.coord;
        this.weight = qp.weight;
        this.load = load;
        this.loadValidity = loadValidity;
        this.boundary = new MFLineBnd((Line) qp.segment);
        this.boundaryParameter = qp.segmentParameter;
    }

    public SimpMFBoundaryIntegratePoint() {
    }

    public void setBoundary(MFLineBnd boundary) {
        this.boundary = boundary;
    }

    public void setBoundaryParameter(double boundaryParameter) {
        this.boundaryParameter = boundaryParameter;
    }

    @Override
    public MFLineBnd getBoundary() {
        return boundary;
    }

    @Override
    public double getBoundaryParameter() {
        return boundaryParameter;
    }

    @Override
    public double[] getOutNormal() {
        return outNormal;
    }

    public void setOutNormal(double[] outNormal) {
        this.outNormal = outNormal;
    }
}
