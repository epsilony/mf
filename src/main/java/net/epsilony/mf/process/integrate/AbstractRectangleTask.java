/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import java.util.ArrayList;
import java.util.List;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.geomodel.GeomModel2DUtils;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.solid.Polygon2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractRectangleTask {

    private int id;
    protected double down;
    protected double left;
    protected GeomModel2D model;
    protected double right;
    protected double spaceNodesDistance;
    protected double up;
    protected boolean needPrepare = true;

    public void addBoundaryConditionOnEdge(String edge, GenericFunction<double[], double[]> value, GenericFunction<double[], boolean[]> diriMark) {
        edge = edge.toLowerCase();
        double l;
        double d;
        double r;
        double u;
        double t = getBoundarySegmentLengthUpperBound() / 10;
        switch (edge) {
            case "l":
            case "left":
                l = left - t;
                d = down;
                r = left + t;
                u = up;
                break;
            case "d":
            case "down":
                l = left;
                r = right;
                d = down - t;
                u = down + t;
                break;
            case "r":
            case "right":
                l = right - t;
                r = right + t;
                d = down;
                u = up;
                break;
            case "u":
            case "up":
                l = left;
                r = right;
                d = up - t;
                u = up + t;
                break;
            default:
                throw new IllegalArgumentException("The edge should only be one of \n" + "[ \"lelf\", \"l\", \"d\", \"down\", \"r\", \"right\", \"u\", \"up\" ]\n" + "but get :" + edge);
        }
        double[] from = new double[]{l, d};
        double[] to = new double[]{r, u};
        AbstractModel2DTask modelTask = getAbstractModel2DTask();
        if (diriMark != null) {
            modelTask.addDirichletBoundaryCondition(from, to, value, diriMark);
        } else {
            modelTask.addNeumannBoundaryCondition(from, to, value);
        }
        needPrepare = true;
    }

    protected abstract double getBoundarySegmentLengthUpperBound();

    protected void checkRectangleParameters() {
        if (left >= right) {
            throw new IllegalArgumentException(String.format("left (%f) should be less then right (%f)", left, right));
        }
        if (down >= up) {
            throw new IllegalArgumentException(String.format("down (%f) should be less then up (%f)", down, up));
        }
    }

    public List<MFBoundaryIntegratePoint> dirichletTasks() {
        needPrepare = true;
        return getAbstractModel2DTask().dirichletTasks();
    }

    protected ArrayList<MFNode> genSpaceNodes() {
        double w = getWidth();
        double h = getHeight();
        int numCol = (int) Math.ceil(w / spaceNodesDistance) - 1;
        int numRow = (int) Math.ceil(h / spaceNodesDistance) - 1;
        double dw = w / (numCol + 1);
        double dh = h / (numRow + 1);
        double x0 = left + dw;
        double y0 = down + dh;
        ArrayList<MFNode> spaceNodes = new ArrayList<>(numCol * numRow);
        for (int i = 0; i < numRow; i++) {
            double y = y0 + dw * i;
            for (int j = 0; j < numCol; j++) {
                double x = x0 + dh * j;
                spaceNodes.add(new MFNode(x, y));
            }
        }
        return spaceNodes;
    }

    public double getDown() {
        return down;
    }

    public double getHeight() {
        return up - down;
    }

    public double getLeft() {
        return left;
    }

    public GeomModel2D getModel() {
        prepareModelAndTask();
        return model;
    }

    public double getRight() {
        return right;
    }

    public double getUp() {
        return up;
    }

    public double getWidth() {
        return right - left;
    }

    public List<MFBoundaryIntegratePoint> neumannTasks() {
        prepareModelAndTask();
        return getAbstractModel2DTask().neumannTasks();
    }

    protected void prepareModelAndTask() {
        if (!needPrepare) {
            return;
        }
        Polygon2D polygon = genPolygon();
        ArrayList<MFNode> spaceNodes = genSpaceNodes();
        model = new GeomModel2D();
        model.setPolygon(GeomModel2DUtils.clonePolygonWithMFNode(polygon));
        model.setSpaceNodes(spaceNodes);
        getAbstractModel2DTask().setModel(model);
        needPrepare = false;
    }

    abstract protected Polygon2D genPolygon();

    public void setDown(double down) {
        needPrepare = true;
        this.down = down;
    }

    public void setLeft(double left) {
        needPrepare = true;
        this.left = left;
    }

    public void setRight(double right) {
        needPrepare = true;
        this.right = right;
    }

    public void setSegmentQuadratureDegree(int segQuadDegree) {
        needPrepare = true;
        getAbstractModel2DTask().setSegmentQuadratureDegree(segQuadDegree);
    }

    public void setSpaceNodesDistance(double spaceNodesDistance) {
        needPrepare = true;
        this.spaceNodesDistance = spaceNodesDistance;
    }

    public void setUp(double up) {
        needPrepare = true;
        this.up = up;
    }

    protected abstract AbstractModel2DTask getAbstractModel2DTask();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
