/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.mf.model.load.MFLoad;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static net.epsilony.mf.model.MFRectangleEdge.*;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectanglePhM implements PhysicalModel {

    private static final EnumMap<MFRectangleEdge, MFRectangleEdge[]> EDGE_START_COORD = new EnumMap<>(MFRectangleEdge.class);
    private static final int DIMENSION = 2;

    static {
        EDGE_START_COORD.put(DOWN, new MFRectangleEdge[]{LEFT, DOWN});
        EDGE_START_COORD.put(RIGHT, new MFRectangleEdge[]{RIGHT, DOWN});
        EDGE_START_COORD.put(UP, new MFRectangleEdge[]{RIGHT, UP});
        EDGE_START_COORD.put(LEFT, new MFRectangleEdge[]{LEFT, UP});
    }
    EnumMap<MFRectangleEdge, Double> edgePosition = new EnumMap<>(MFRectangleEdge.class);
    EnumMap<MFRectangleEdge, Line> edgeLine = new EnumMap<>(MFRectangleEdge.class);
    RawPhysicalModel rawPhysicalModel;
    Map<GeomUnit, MFLoad> loadMap = new HashMap<>();
    MFLoad volumeLoad;

    public RectanglePhM() {
        initInnerModel();
    }

    public double[][] getVertexCoords() {
        double[][] result = new double[4][];
        int index = 0;
        for (MFRectangleEdge edge : MFRectangleEdge.values()) {
            MFRectangleEdge[] startEdges = EDGE_START_COORD.get(edge);
            double[] coord = new double[2];
            for (int i = 0; i < startEdges.length; i++) {
                coord[i] = edgePosition.get(startEdges[i]);
            }
            result[index++] = coord;
        }
        return result;
    }

    private void setupLineStart(MFRectangleEdge edge) {
        MFRectangleEdge[] startEdges = EDGE_START_COORD.get(edge);
        Line line = edgeLine.get(edge);
        double[] startCoord = line.getStartCoord();
        for (int i = 0; i < startEdges.length; i++) {
            startCoord[i] = edgePosition.get(startEdges[i]);
        }
    }
    boolean needPrepare = true;

    private void prepare() {
        if (!needPrepare) {
            return;
        }

        checkRectangleParameters();

        for (MFRectangleEdge edge : MFRectangleEdge.values()) {
            setupLineStart(edge);
        }

        needPrepare = false;
    }

    private void initInnerModel() {
        ArrayList<MFNode> nodes = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            nodes.add(new MFNode(new double[DIMENSION]));
        }
        Facet facet = Facet.byNodesChains(Arrays.asList(nodes));

        Iterator<Segment> bndIter = facet.iterator();
        for (MFRectangleEdge edge : values()) {
            edgeLine.put(edge, (Line) bndIter.next());
        }

        rawPhysicalModel = new FacetModel();
        rawPhysicalModel.setDimension(DIMENSION);
        rawPhysicalModel.setGeomRoot(facet);
        loadMap.put(facet, volumeLoad);
        rawPhysicalModel.setLoadMap(loadMap);
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
    public int getDimension() {
        return rawPhysicalModel.getDimension();
    }

    @Override
    public Map<GeomUnit, MFLoad> getLoadMap() {
        return rawPhysicalModel.getLoadMap();
    }

    public void setEdgeLoad(MFRectangleEdge edge, MFLoad load) {
        loadMap.put(edgeLine.get(edge), load);
    }

    public void setVolumeLoad(MFLoad load) {
        this.volumeLoad = load;
    }

    @Override
    public GeomUnit getGeomRoot() {
        prepare();
        return rawPhysicalModel.getGeomRoot();
    }

    @Override
    public void setDimension(int dim) {
        if (dim != DIMENSION) {
            throw new IllegalArgumentException("only supports 2d, not " + dim);
        }
    }

    public RawPhysicalModel getRawPhysicalModel() {
        return rawPhysicalModel;
    }

    public MFRectangleEdge getEdge(Line line) {
        double[] startCoord = line.getStartCoord();
        double[] endCoord = line.getEndCoord();
        if (startCoord[0] == endCoord[0]) {
            if (startCoord[1] == endCoord[1]) {
                throw new IllegalArgumentException();
            }
            if (startCoord[0] == getEdgePosition(LEFT)) {
                return LEFT;
            } else if (startCoord[0] == getEdgePosition(RIGHT)) {
                return RIGHT;
            } else {
                return null;
            }
        } else if (startCoord[1] == endCoord[1]) {
            if (startCoord[1] == getEdgePosition(DOWN)) {
                return DOWN;
            } else if (startCoord[1] == getEdgePosition(UP)) {
                return UP;
            } else {
                return null;
            }
        }
        return null;
    }
}
