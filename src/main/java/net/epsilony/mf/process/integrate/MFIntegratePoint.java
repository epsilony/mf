/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFIntegratePoint<T extends QuadraturePoint> {

    public double[] load;
    public boolean[] loadValidity;
    public T quadraturePoint;

    public MFIntegratePoint(T qp, double[] load, boolean[] loadValidity) {
        this.quadraturePoint = qp;
        this.loadValidity = loadValidity;
        this.load = load;
    }
}
