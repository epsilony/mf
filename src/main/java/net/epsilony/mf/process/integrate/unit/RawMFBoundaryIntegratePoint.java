/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.unit;

import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawMFBoundaryIntegratePoint extends RawMFIntegratePoint implements MFBoundaryIntegratePoint {

    GeomUnit boundary;
    double boundaryParameter;
    double[] unitOutNormal;

//    public SimpMFBoundaryIntegratePoint(Segment2DQuadraturePoint qp, double[] load, boolean[] loadValidity) {
//        this.coord = qp.coord;
//        this.weight = qp.weight;
//        this.load = load;
//        this.loadValidity = loadValidity;
//        this.boundary = new MFLineBnd((Line) qp.segment);
//        this.boundaryParameter = qp.segmentParameter;
//    }
    public RawMFBoundaryIntegratePoint() {
    }

    public void setBoundary(GeomUnit boundary) {
        this.boundary = boundary;
    }

    public void setBoundaryParameter(double boundaryParameter) {
        this.boundaryParameter = boundaryParameter;
    }

    @Override
    public GeomUnit getBoundary() {
        return boundary;
    }

    @Override
    public double getBoundaryParameter() {
        return boundaryParameter;
    }

    @Override
    public double[] getUnitOutNormal() {
        return unitOutNormal;
    }

    public void setUnitOutNormal(double[] outNormal) {
        this.unitOutNormal = outNormal;
    }
}
