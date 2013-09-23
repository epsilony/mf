/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import net.epsilony.mf.geomodel.MFLine;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFBoundaryIntegratePoint extends SimpMFIntegratePoint implements MFBoundaryIntegratePoint {

    MFLine boundary;
    double boundaryParameter;
    double[] outNormal;

    public SimpMFBoundaryIntegratePoint(Segment2DQuadraturePoint qp, double[] load, boolean[] loadValidity) {
        this.coord = qp.coord;
        this.weight = qp.weight;
        this.load = load;
        this.loadValidity = loadValidity;
        this.boundary = (MFLine) qp.segment;
        this.boundaryParameter = qp.segmentParameter;
    }

    public SimpMFBoundaryIntegratePoint() {
    }

    public void setBoundary(MFLine boundary) {
        this.boundary = boundary;
    }

    public void setBoundaryParameter(double boundaryParameter) {
        this.boundaryParameter = boundaryParameter;
    }

    @Override
    public MFLine getBoundary() {
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
