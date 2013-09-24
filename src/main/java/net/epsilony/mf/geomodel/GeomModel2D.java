/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import net.epsilony.tb.solid.Polygon2D;
import java.util.List;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeomModel2D implements GeomModel {

    public final static int DIMENSION = 2;
    List<MFNode> spaceNodes;   //allNode except polygon.getVertes()
    private Polygon2D polygon;

    public static boolean checkPolygon(Polygon2D polygon) {
        for (Segment seg : polygon) {
            if (!(seg instanceof Line) || !(seg.getStart() instanceof MFNode)) {
                return false;
            }
        }
        return true;
    }

    public Polygon2D getPolygon() {
        return polygon;
    }

    @Override
    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    public void setPolygon(Polygon2D polygon) {
        if (!checkPolygon(polygon)) {
            throw new IllegalArgumentException();
        }
        this.polygon = polygon;
    }

    @Override
    public List<MFLineBnd> getBoundaries() {
        return MFLineBnd.wraps(polygon.getSegments());
    }

    @Override
    public void setDimension(int dim) {
        if (dim != DIMENSION) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int getDimension() {
        return DIMENSION;
    }
}
