/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.quadrature_task;

import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFQuadraturePoint<T extends QuadraturePoint> {

    public double[] load;
    public boolean[] loadValidity;
    public T quadraturePoint;

    public MFQuadraturePoint(T qp, double[] load, boolean[] loadValidity) {
        this.quadraturePoint = qp;
        this.loadValidity = loadValidity;
        this.load = load;
    }
}
