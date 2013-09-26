/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import static net.epsilony.mf.model.MFRectangleEdge.*;
import net.epsilony.tb.solid.Facet;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectanglePhM implements PhysicalModel {

    EnumMap<MFRectangleEdge, Double> edgePosition = new EnumMap<>(MFRectangleEdge.class);
    EnumMap<MFRectangleEdge, MFLineBnd> edgeBnd = new EnumMap<>(MFRectangleEdge.class);
    private static final EnumMap<MFRectangleEdge, MFRectangleEdge[]> EDGE_START_COORD = new EnumMap<>(MFRectangleEdge.class);

    static {
        EDGE_START_COORD.put(DOWN, new MFRectangleEdge[]{LEFT, DOWN});
        EDGE_START_COORD.put(RIGHT, new MFRectangleEdge[]{RIGHT, DOWN});
        EDGE_START_COORD.put(UP, new MFRectangleEdge[]{RIGHT, UP});
        EDGE_START_COORD.put(LEFT, new MFRectangleEdge[]{LEFT, UP});
    }

    private void setupBndStart(MFRectangleEdge edge) {
        MFRectangleEdge[] startEdges = EDGE_START_COORD.get(edge);
        MFLineBnd boundary = edgeBnd.get(edge);
        double[] startCoord = boundary.getLine().getStart().getCoord();
        for (int i = 0; i < startEdges.length; i++) {
            startCoord[i] = edgePosition.get(startEdges[i]);
        }
    }
    FacetModel facetModel = new FacetModel();
    boolean needPrepare = true;

    private void prepare() {
        if (!needPrepare) {
            return;
        }

        if (edgeBnd.isEmpty()) {
            initBnds();
        }

        checkRectangleParameters();

        for (MFRectangleEdge edge : MFRectangleEdge.values()) {
            setupBndStart(edge);
        }

        needPrepare = false;
    }

    private void initBnds() {
        ArrayList<MFNode> nodes = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            nodes.add(new MFNode(new double[getDimension()]));
        }
        Facet facet = Facet.byNodesChains(Arrays.asList(nodes));
        facetModel.setFacet(facet);
        List<? extends MFBoundary> boundaries = facetModel.getBoundaries();
        Iterator<? extends MFBoundary> bndIter = boundaries.iterator();
        for (MFRectangleEdge edge : values()) {
            edgeBnd.put(edge, (MFLineBnd) bndIter.next());
        }
    }

    public double getEdgePosition(MFRectangleEdge edge) {
        return edgePosition.get(edge);
    }

    public void setEdgePosition(MFRectangleEdge edge, double position) {
        needPrepare = true;
        edgePosition.put(edge, position);
    }

    public boolean isAvialable() {
        if (getEdgePosition(LEFT) >= getEdgePosition(RIGHT) || getEdgePosition(DOWN) >= getEdgePosition(UP)) {
            return false;
        }
        return true;
    }

    protected void checkRectangleParameters() {
        if (getEdgePosition(LEFT) >= getEdgePosition(RIGHT)) {
            throw new IllegalArgumentException(String.format("left (%f) should be less then right (%f)", getEdgePosition(LEFT), getEdgePosition(RIGHT)));
        }
        if (getEdgePosition(DOWN) >= getEdgePosition(UP)) {
            throw new IllegalArgumentException(String.format("down (%f) should be less then up (%f)", getEdgePosition(DOWN), getEdgePosition(UP)));
        }
    }

    public double getWidth() {
        return getEdgePosition(RIGHT) - getEdgePosition(LEFT);
    }

    public double getHeight() {
        return getEdgePosition(UP) - getEdgePosition(DOWN);
    }

    @Override
    public List<? extends MFBoundary> getBoundaries() {
        prepare();
        return facetModel.getBoundaries();
    }

    @Override
    public void setDimension(int dim) {
        facetModel.setDimension(dim);
    }

    @Override
    public int getDimension() {
        return facetModel.getDimension();
    }

    public MFLineBnd getBoundary(MFRectangleEdge edge) {
        prepare();
        return edgeBnd.get(edge);
    }

    public void setVolumeLoads(List<MFLoad> volumeLoads) {
        facetModel.setVolumeLoads(volumeLoads);
    }

    @Override
    public List<MFLoad> getVolumeLoads() {
        return facetModel.getVolumeLoads();
    }
}
