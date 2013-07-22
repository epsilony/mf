/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFQuadraturePoint extends QuadraturePoint {

    public double[] value;
    public boolean[] mark;

    public MFQuadraturePoint(QuadraturePoint qp, double[] value, boolean[] mark) {
        this.weight = qp.weight;
        this.coord = qp.coord;
        this.segment = qp.segment;
        this.segmentParameter = qp.segmentParameter;
        this.mark = mark;
        this.value = value;
    }
}
