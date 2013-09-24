/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.QuadrangleQuadrature;
import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleTask extends AbstractRectangleTask implements MFIntegrateTask {

    GenericFunction<double[], double[]> volumnForceFunc;
    private double quadDomainSizeUpBnd;

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            double quadDomainSizeUpBnd) {

        needPrepare = true;
        this.volumnForceFunc = volumnForceFunc;
        this.quadDomainSizeUpBnd = quadDomainSizeUpBnd;
    }

    @Override
    protected void prepareVolume() {
        QuadrangleQuadrature qQuad = new QuadrangleQuadrature();
        qQuad.setDegree(getQuadratureDegree());
        LinkedList<QuadraturePoint> qPoints = new LinkedList<>();
        double width = rectangle2DModel.getWidth();
        double height = rectangle2DModel.getHeight();
        int numHor = (int) Math.ceil(width / quadDomainSizeUpBnd);
        double dWidth = width / numHor;
        int numVer = (int) Math.ceil(height / quadDomainSizeUpBnd);
        double dHeight = height / numVer;
        double x0 = rectangle2DModel.getLeft();
        double y0 = rectangle2DModel.getDown();
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
        model2DTask.setVolumeSpecification(volumnForceFunc, qPoints);
    }

    @Override
    protected void prepare() {
        super.prepare();
    }

    @Override
    public List<MFIntegratePoint> volumeTasks() {
        prepare();
        return model2DTask.volumeTasks();
    }
}
