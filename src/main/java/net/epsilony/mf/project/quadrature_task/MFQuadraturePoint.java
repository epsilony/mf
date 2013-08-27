/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.quadrature_task;

import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFQuadraturePoint extends Segment2DQuadraturePoint {

    public double[] value;
    public boolean[] mark;

    public MFQuadraturePoint(QuadraturePoint qp, double[] value, boolean[] mark) {
        this.weight = qp.weight;
        this.coord = qp.coord;
        this.mark = mark;
        this.value = value;
    }
}
