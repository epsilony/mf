/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFStrainStabilizeIntegrateDomain;
import net.epsilony.mf.process.integrate.point.SimpMFStrainStabilizeIntegrateDomain;
import net.epsilony.mf.process.integrate.point.MFStrainStabilizeIntegratePoint;
import net.epsilony.mf.process.integrate.point.SimpMFStrainStabilizeIntegratePoint;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.RawSegment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleStrainStablilizeTask {//extends AbstractRectangleTask implements MFStrainStabilizeIntegrateTask {
//
//    private final Model2DStrainStabilizeTask model2DStrainStabilizeTask = new Model2DStrainStabilizeTask();
//    private int volumneQuadrtureDegree;
//    GenericFunction<double[], double[]> volumeLoadFunction;
//    RawSegment volumeQuadratureSegment;
//    Segment2DQuadrature volumenBoundarQuadrature = new Segment2DQuadrature();
//
//    public RectangleStrainStablilizeTask() {
//        volumeQuadratureSegment = new RawSegment();
//        volumeQuadratureSegment.setSucc(new RawSegment());
//        volumeQuadratureSegment.setStart(new Node(new double[2]));
//        volumeQuadratureSegment.getSucc().setStart(new Node(new double[2]));
//        volumenBoundarQuadrature.setSegment(volumeQuadratureSegment);
//    }
//
//    @Override
//    public List<MFStrainStabilizeIntegrateDomain> volumeDomainTask() {
//        prepareModelAndTask();
//        volumenBoundarQuadrature.setDegree(volumneQuadrtureDegree);
//        LinkedList<MFStrainStabilizeIntegrateDomain> ssDomains = new LinkedList<>();
//        model2DStrainStabilizeTask.setVolumeSpecification(volumeLoadFunction, ssDomains);
//        double width = rectangle2DModel.getWidth();
//        double height = rectangle2DModel.getHeight();
//        int numHor = numAllNodesCols();
//        double dWidth = width / numHor;
//        int numVer = numAllNodesRows();
//        double dHeight = height / numVer;
//        double x0 = left;
//        double y0 = down;
//        for (int i = 0; i < numVer; i++) {
//            double d = y0 + dHeight * i;
//            double u = i < numVer - 1 ? d + dHeight : up;
//            for (int j = 0; j < numHor; j++) {
//                double l = x0 + dWidth * j;
//                double r = j < numHor - 1 ? l + dWidth : right;
//                addToModelStrainStabilizeTask(l, d, r, u);
//            }
//        }
//
//        return model2DStrainStabilizeTask.volumeDomainTask();
//    }
//
//    private void addToModelStrainStabilizeTask(double l, double d, double r, double u) {
//        List<MFStrainStabilizeIntegrateDomain> ssDomains = model2DStrainStabilizeTask.getVolumeStrainStabilizeDomains();
//        double[] coords = new double[]{l, d, r, d, r, u, l, u};
//        double[] startCoord = volumeQuadratureSegment.getStartCoord();
//        double[] endCoord = volumeQuadratureSegment.getEndCoord();
//        for (int i = 0; i < 4; i++) {
//            startCoord[0] = coords[i * 2];
//            startCoord[1] = coords[i * 2 + 1];
//            int t = (i + 1) % 4;
//            endCoord[0] = coords[t * 2];
//            endCoord[1] = coords[t * 2 + 1];
//        }
//        SimpMFStrainStabilizeIntegrateDomain domain = new SimpMFStrainStabilizeIntegrateDomain();
//        domain.setLoadFunction(volumeLoadFunction);
//        LinkedList<MFStrainStabilizeIntegratePoint> pts = new LinkedList<>();
//        for (Segment2DQuadraturePoint sqp : volumenBoundarQuadrature) {
//            SimpMFStrainStabilizeIntegratePoint pt = new SimpMFStrainStabilizeIntegratePoint();
//            pt.setCoord(sqp.coord);
//            pt.setWeight(sqp.weight);
//            pt.setUnitOutNormal(sqp.outerNormal);
//            pt.setSolidBoundary(new MFLineBnd((Line) findSolidBoundary(startCoord, endCoord)));
//            pts.add(pt);
//        }
//        domain.setBoundaryIntegratePoints(pts);
//        ssDomains.add(domain);
//    }
//
//    public void setVolumeSpecification(
//            GenericFunction<double[], double[]> volumnForceFunc,
//            int quadratureDegree) {
//        needPrepare = true;
//        this.volumneQuadrtureDegree = quadratureDegree;
//        this.volumeLoadFunction = volumnForceFunc;
//    }
//
//    private int numAllNodesRows() {
//        return (int) Math.ceil(rectangle2DModel.getHeight() / spaceNodesDistance + 1);
//    }
//
//    private int numAllNodesCols() {
//        return (int) Math.ceil(rectangle2DModel.getWidth() / spaceNodesDistance + 1);
//    }
//
//    private Segment findSolidBoundary(double[] startCoord, double[] endCoord) {
//        double err = minBoundaryLength / 10;
//        double sx = startCoord[0];
//        double sy = startCoord[1];
//
//        double left = rectangle2DModel.getLeft();
//        double right = rectangle2DModel.getRight();
//        double up = rectangle2DModel.getUp();
//        double down = rectangle2DModel.getDown();
//
//        if (Math.abs(sx - left) > err && Math.abs(sx - right) > err && Math.abs(sy - up) > err && Math.abs(sy - down) > err) {
//            return null;
//        }
//
//        for (MFBoundary bnd : model2DStrainStabilizeTask.model.getBoundaries()) {
//            Segment seg = ((MFLineBnd) bnd).getLine();
//            if (Math2D.distance(seg.getStart().getCoord(), startCoord) < err && Math2D.distance(endCoord, seg.getEnd().getCoord()) < err) {
//                return seg;
//            }
//        }
//        throw new IllegalStateException();
//    }
}
