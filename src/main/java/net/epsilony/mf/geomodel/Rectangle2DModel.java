/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Polygon2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Rectangle2DModel implements GeomModel {

    Polygon2DModel model = new Polygon2DModel();
    protected double down;
    protected double left;
    protected double right;
    protected double spaceNodesDistanceUpperBnd;
    protected double up;
    protected boolean needPrepare = true;

    protected void prepare() {
        if (!needPrepare) {
            return;
        }
        Polygon2D polygon = genPolygon();
        ArrayList<MFNode> spaceNodes = genSpaceNodes();
        model = new Polygon2DModel();
        model.setPolygon(GeomModel2DUtils.clonePolygonWithMFNode(polygon));
        model.setSpaceNodes(spaceNodes);
        needPrepare = false;
    }

    protected Polygon2D genPolygon() {

        checkRectangleParameters();
        double[][] corners = new double[][]{{left, down}, {right, down}, {right, up}, {left, up}};
        List<MFNode> vertes = new LinkedList();
        for (int i = 0; i < 4; i++) {
            double[] lineStart = corners[i];
            double[] lineEnd = corners[(i + 1) % 4];
            double length = Math.ceil(Math2D.distance(lineStart, lineEnd));
            int numOfVerts = (int) Math.ceil(length / spaceNodesDistanceUpperBnd);
            double vertDistance = length / numOfVerts;
            double[] delta = Math2D.normalize(Math2D.subs(lineEnd, lineStart, null), null);
            Math2D.scale(delta, vertDistance, delta);
            double[] pos = Arrays.copyOf(lineStart, 2);
            for (int j = 0; j < numOfVerts; j++) {
                vertes.add(new MFNode(pos));
                pos = Math2D.adds(pos, delta, null);
            }
        }
        return new Polygon2D(Arrays.asList(vertes));
    }

    protected void checkRectangleParameters() {
        if (left >= right) {
            throw new IllegalArgumentException(String.format("left (%f) should be less then right (%f)", left, right));
        }
        if (down >= up) {
            throw new IllegalArgumentException(String.format("down (%f) should be less then up (%f)", down, up));
        }
    }

    protected ArrayList<MFNode> genSpaceNodes() {
        double w = getWidth();
        double h = getHeight();
        int numCol = (int) Math.ceil(w / spaceNodesDistanceUpperBnd) - 1;
        int numRow = (int) Math.ceil(h / spaceNodesDistanceUpperBnd) - 1;
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

    @Override
    public List<MFNode> getSpaceNodes() {
        prepare();
        return model.getSpaceNodes();
    }

    @Override
    public List<MFLineBnd> getBoundaries() {
        prepare();
        return model.getBoundaries();
    }

    @Override
    public void setDimension(int dim) {
        model.setDimension(dim);
    }

    @Override
    public int getDimension() {
        return model.getDimension();
    }

    public double getDown() {
        return down;
    }

    public void setDown(double down) {
        this.down = down;
        needPrepare = true;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
        needPrepare = true;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
        needPrepare = true;
    }

    public double getSpaceNodesDistance() {
        return spaceNodesDistanceUpperBnd;
    }

    public void setSpaceNodesDistance(double spaceNodesDistance) {
        this.spaceNodesDistanceUpperBnd = spaceNodesDistance;
        needPrepare = true;
    }

    public double getUp() {
        return up;
    }

    public void setUp(double up) {
        this.up = up;
        needPrepare = true;
    }

    public double getWidth() {
        return right - left;
    }

    public double getHeight() {
        return up - down;
    }
}
