/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.SingleLine2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleStrainStablilizeTask extends AbstractRectangleTask implements MFStrainStabilizeIntegrateTask {

    private Model2DStrainStabilizeTask model2DStrainStabilizeTask = new Model2DStrainStabilizeTask();
    private int volumneQuadrtureDegree;
    GenericFunction<double[], double[]> volumeLoadFunction;
    SingleLine2D<Node> volumeQuadratureSegment;
    Segment2DQuadrature volumenBoundarQuadrature = new Segment2DQuadrature();

    public RectangleStrainStablilizeTask() {
        volumeQuadratureSegment = new SingleLine2D<>();
        volumeQuadratureSegment.setStart(new Node(new double[2]));
        volumeQuadratureSegment.setEnd(new Node(new double[2]));
        volumenBoundarQuadrature.setSegment(volumeQuadratureSegment);
    }

    @Override
    public List<MFStrainStabilizeIntegrateDomain> volumeDomainTask() {
        prepareModelAndTask();
        volumenBoundarQuadrature.setDegree(volumneQuadrtureDegree);
        LinkedList<MFStrainStabilizeIntegrateDomain> ssDomains = new LinkedList<>();
        model2DStrainStabilizeTask.setVolumeSpecification(volumeLoadFunction, ssDomains);
        double width = getWidth();
        double height = getHeight();
        int numHor = numAllNodesCols();
        double dWidth = width / numHor;
        int numVer = numAllNodesRows();
        double dHeight = height / numVer;
        double x0 = left;
        double y0 = down;
        for (int i = 0; i < numVer; i++) {
            double d = y0 + dHeight * i;
            double u = i < numVer - 1 ? d + dHeight : up;
            for (int j = 0; j < numHor; j++) {
                double l = x0 + dWidth * j;
                double r = j < numHor - 1 ? l + dWidth : right;
                addToModelStrainStabilizeTask(l, d, r, u);
            }
        }

        return model2DStrainStabilizeTask.volumeDomainTask();
    }

    private void addToModelStrainStabilizeTask(double l, double d, double r, double u) {
        List<MFStrainStabilizeIntegrateDomain> ssDomains = model2DStrainStabilizeTask.getVolumeStrainStabilizeDomains();
        double[] coords = new double[]{l, d, r, d, r, u, l, u};
        double[] startCoord = volumeQuadratureSegment.getStartCoord();
        double[] endCoord = volumeQuadratureSegment.getEndCoord();
        for (int i = 0; i < 4; i++) {
            startCoord[0] = coords[i * 2];
            startCoord[1] = coords[i * 2 + 1];
            int t = (i + 1) % 4;
            endCoord[0] = coords[t * 2];
            endCoord[1] = coords[t * 2 + 1];
        }
        SimpMFStrainStabilizeIntegrateDomain domain = new SimpMFStrainStabilizeIntegrateDomain();
        domain.setLoadFunction(volumeLoadFunction);
        LinkedList<MFDivergenceIntegratePoint> pts = new LinkedList<>();
        for (Segment2DQuadraturePoint sqp : volumenBoundarQuadrature) {
            SimpMFDivergenceIntegratePoint pt = new SimpMFDivergenceIntegratePoint();
            pt.setCoord(sqp.coord);
            pt.setWeight(sqp.weight);
            pt.setUnitOutNormal(sqp.outerNormal);
            pt.setSolidBoundary(findSolidBoundary(startCoord, endCoord));
            pts.add(pt);
        }
        domain.setBoundaryIntegratePoints(pts);
        ssDomains.add(domain);
    }

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            int quadratureDegree) {
        needPrepare = true;
        this.volumneQuadrtureDegree = quadratureDegree;
        this.volumeLoadFunction = volumnForceFunc;
    }

    @Override
    protected AbstractModel2DTask getAbstractModel2DTask() {
        return model2DStrainStabilizeTask;
    }

    private int numAllNodesRows() {
        return (int) Math.ceil(getHeight() / spaceNodesDistance + 1);
    }

    private int numAllNodesCols() {
        return (int) Math.ceil(getWidth() / spaceNodesDistance + 1);
    }

    @Override
    protected Polygon2D genPolygon() {
        checkRectangleParameters();

        double w = getWidth();
        double h = getHeight();
        int numCol = numAllNodesCols();
        int numRow = numAllNodesRows();
        double dw = w / (numCol - 1);
        double dh = h / (numRow - 1);

        double[][] corners = new double[][]{{left, down}, {right, down}, {right, up}, {left, up}};
        double[][] delta = new double[][]{{dw, 0}, {0, dh}, {-dw, 0}, {0, -dh}};
        int[] nums = new int[]{numCol - 1, numRow - 1, numCol - 1, numRow - 1};
        ArrayList<Node> boundaryNodes = new ArrayList<>(2 * numRow + 2 * numCol - 4);
        for (int edge = 0; edge < nums.length; edge++) {
            int num = nums[edge];
            double dx = delta[num][0];
            double dy = delta[num][1];
            double x0 = corners[num][0];
            double y0 = corners[num][1];
            for (int i = 0; i < num; i++) {
                double x = x0 + dx * i;
                double y = y0 + dy * i;
                Node nd = new Node(x, y);
                boundaryNodes.add(nd);
            }
        }
        List<ArrayList<Node>> chains = new ArrayList<>();
        chains.add(boundaryNodes);
        return new Polygon2D(chains);
    }

    @Override
    protected double getBoundarySegmentLengthUpperBound() {
        return spaceNodesDistance;
    }

    private Segment findSolidBoundary(double[] startCoord, double[] endCoord) {
        double err = getBoundarySegmentLengthUpperBound() / 10;
        double sx = startCoord[0];
        double sy = startCoord[1];

        if (Math.abs(sx - left) > err && Math.abs(sx - right) > err && Math.abs(sy - up) > err && Math.abs(sy - down) > err) {
            return null;
        }

        for (Segment seg : model2DStrainStabilizeTask.model.getPolygon()) {
            if (Math2D.distance(seg.getStart().getCoord(), startCoord) < err && Math2D.distance(endCoord, seg.getEnd().getCoord()) < err) {
                return seg;
            }
        }
        throw new IllegalStateException();
    }
}
