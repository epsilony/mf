/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.quadrature.QuadrangleQuadrature;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.solid.Polygon2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleStrainStablilizeTask extends AbstractRectangleTask implements MFStrainStabilizeIntegrateTask {

    private Model2DStrainStabilizeTask model2DStrainStabilizeTask = new Model2DStrainStabilizeTask();

    @Override
    public List<MFStrainStabilizeIntegrateDomain> volumeDomainTask() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            double quadDomainSizeUpBnd,
            int quadratureDegree) {
        QuadrangleQuadrature qQuad = new QuadrangleQuadrature();
        qQuad.setDegree(quadratureDegree);
        LinkedList<MFStrainStabilizeIntegrateDomain> ssDomains = new LinkedList<>();
        model2DStrainStabilizeTask.setVolumeSpecification(volumnForceFunc, ssDomains);
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
                addToModelStrainStabilizeTask(l, d, r, u);
            }
        }
    }

    @Override
    protected AbstractModel2DTask getAbstractModel2DTask() {
        return model2DStrainStabilizeTask;
    }

    @Override
    protected Polygon2D genPolygon() {
        throw new UnsupportedOperationException();
//        checkRectangleParameters();
//
//        double w = getWidth();
//        double h = getHeight();
//        int numCol = (int) Math.ceil(w / spaceNodesDistance);
//        int numRow = (int) Math.ceil(h / spaceNodesDistance);
//        double dw = w / (numCol + 1);
//        double dh = h / (numRow + 1);
//
//        double[][] corners = new double[][]{{left, down}, {right, down}, {right, up}, {left, up}};
//        double[][] delta = new double[][]{{dw, 0}, {0, dh}, {-dw, 0}, {0, -dh}};
//        int[] num = new int[]{numCol, numRow, numCol, numRow};
//        double x0 = left + dw;
//        double y0 = down + dh;
//        ArrayList<MFNode> spaceNodes = new ArrayList<>(numCol * numRow);
//        for (int i = 0; i < numRow; i++) {
//            double y = y0 + dw * i;
//            for (int j = 0; j < numCol; j++) {
//                double x = x0 + dh * j;
//                spaceNodes.add(new MFNode(x, y));
//            }
//        }
    }

    private void addToModelStrainStabilizeTask(double l, double d, double r, double u) {
        List<MFStrainStabilizeIntegrateDomain> ssDomains = model2DStrainStabilizeTask.getVolumeStrainStabilizeDomains();
        double[] coords = new double[]{l, d, r, d, r, u, l, u};
        for (int i = 0; i < 4; i++) {
        }
    }
}
