/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFBoundaryIntegratePoint implements MFBoundaryIntegratePoint {

    Segment2DQuadraturePoint quadraturePoint;
    double[] load;
    boolean[] loadValidity;

    public SimpMFBoundaryIntegratePoint(Segment2DQuadraturePoint qp, double[] load, boolean[] loadValidity) {
        this.quadraturePoint = qp;
        this.load = load;
        this.loadValidity = loadValidity;
    }

    @Override
    public Segment getBoundary() {
        return quadraturePoint.segment;
    }

    @Override
    public double getBoundaryParameter() {
        return quadraturePoint.segmentParameter;
    }

    @Override
    public double[] getCoord() {
        return quadraturePoint.coord;
    }

    @Override
    public double getWeight() {
        return quadraturePoint.weight;
    }

    @Override
    public double[] getLoad() {
        return load;
    }

    @Override
    public boolean[] getLoadValidity() {
        return loadValidity;
    }

    @Override
    public void setDimension(int dim) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDimension() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
