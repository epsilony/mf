/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Facet;
import static net.epsilony.mf.model.MFRectangleEdge.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Rectangle2DModel implements AnalysisModel {

    FacetModel model;
    RectangleGM rectangleGM;
    protected double nodesDistanceUpperBound;
    protected boolean needPrepare = true;

    protected void prepare() {
        if (!needPrepare) {
            return;
        }
        Facet facet = genFacet();
        ArrayList<MFNode> spaceNodes = genSpaceNodes();
        model = new FacetModel();
        model.setFacet(GeomModel2DUtils.clonePolygonWithMFNode(facet));
        model.setSpaceNodes(spaceNodes);
        needPrepare = false;
    }

    protected Facet genFacet() {

        double[][] corners = new double[4][];
        for (MFRectangleEdge edge : MFRectangleEdge.values()) {
            corners[edge.ordinal()] = rectangleGM.getBoundary(edge).getLine().getStartCoord();
        }
        List<MFNode> vertes = new LinkedList();
        for (int i = 0; i < 4; i++) {
            double[] lineStart = corners[i];
            double[] lineEnd = corners[(i + 1) % 4];
            double length = Math.ceil(Math2D.distance(lineStart, lineEnd));
            int numOfVerts = (int) Math.ceil(length / nodesDistanceUpperBound);
            double vertDistance = length / numOfVerts;
            double[] delta = Math2D.normalize(Math2D.subs(lineEnd, lineStart, null), null);
            Math2D.scale(delta, vertDistance, delta);
            double[] pos = Arrays.copyOf(lineStart, 2);
            for (int j = 0; j < numOfVerts; j++) {
                vertes.add(new MFNode(pos));
                pos = Math2D.adds(pos, delta, null);
            }
        }
        return Facet.byNodesChains(Arrays.asList(vertes));
    }

    protected ArrayList<MFNode> genSpaceNodes() {
        double w = rectangleGM.getWidth();
        double h = rectangleGM.getHeight();
        int numCol = (int) Math.ceil(w / nodesDistanceUpperBound) - 1;
        int numRow = (int) Math.ceil(h / nodesDistanceUpperBound) - 1;
        double dw = w / (numCol + 1);
        double dh = h / (numRow + 1);
        double x0 = rectangleGM.getEdgePosition(LEFT) + dw;
        double y0 = rectangleGM.getEdgePosition(DOWN) + dh;
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
    public List<? extends MFBoundary> getBoundaries() {
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

    public void setVolumeLoads(List<MFLoad> volumeLoads) {
        model.setVolumeLoads(volumeLoads);
    }

    @Override
    public List<MFLoad> getVolumeLoads() {
        return model.getVolumeLoads();
    }

    public double getNodesDistanceUpperBound() {
        return nodesDistanceUpperBound;
    }

    public void setNodesDistanceUpperBound(double nodesDistanceUpperBound) {
        this.nodesDistanceUpperBound = nodesDistanceUpperBound;
        needPrepare = true;
    }

    public RectangleGM getRectangleGM() {
        return rectangleGM;
    }

    public void setRectangleGM(RectangleGM rectangleGM) {
        this.rectangleGM = rectangleGM;
    }
}
