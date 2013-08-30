/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.QuadrangleQuadrature;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.solid.Polygon2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleTask extends AbstractRectangleTask implements MFIntegrateTask {

    Model2DTask modelTask = new Model2DTask();
    protected double segmentLengthUpperBound;

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            double quadDomainSizeUpBnd,
            int quadratureDegree) {
        needPrepare = true;
        QuadrangleQuadrature qQuad = new QuadrangleQuadrature();
        qQuad.setDegree(quadratureDegree);
        LinkedList<QuadraturePoint> qPoints = new LinkedList<>();
        double width = getWidth();
        double height = getHeight();
        int numHor = (int) Math.ceil(width / quadDomainSizeUpBnd);
        double dWidth = width / numHor;
        int numVer = (int) Math.ceil(height / quadDomainSizeUpBnd);
        double dHeight = height / numVer;
        double x0 = left;
        double y0 = down;
        for (int i = 0; i < numVer; i++) {
            double d = y0 + dHeight * i;
            double u = d + dHeight;
            for (int j = 0; j < numHor; j++) {
                double l = x0 + dWidth * j;
                double r = l + dWidth;
                qQuad.setQuadrangle(l, d, r, d, r, u, l, u);
                for (QuadraturePoint qP : qQuad) {
                    qPoints.add(qP);
                }
            }
        }
        modelTask.setVolumeSpecification(volumnForceFunc, qPoints);
    }

    @Override
    public List<MFIntegratePoint> volumeTasks() {
        prepareModelAndTask();
        return modelTask.volumeTasks();
    }

    @Override
    protected AbstractModel2DTask getAbstractModel2DTask() {
        return modelTask;
    }

    public void setSegmentLengthUpperBound(double segmentLengthUpperBound) {
        this.segmentLengthUpperBound = segmentLengthUpperBound;
    }

    @Override
    protected Polygon2D genPolygon() {
        checkRectangleParameters();
        Polygon2D poly = Polygon2D.byCoordChains(new double[][][]{{{left, down}, {right, down}, {right, up}, {left, up}}});
        return poly.fractionize(segmentLengthUpperBound);
    }

    @Override
    protected double getBoundarySegmentLengthUpperBound() {
        return segmentLengthUpperBound;
    }
}
