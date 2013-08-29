/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFIntegratePoint implements MFIntegratePoint {

    QuadraturePoint quadraturePoint;
    double[] load;

    public SimpMFIntegratePoint(QuadraturePoint qp, double[] volForce) {
        quadraturePoint = qp;
        load = volForce;
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
        return null;
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
