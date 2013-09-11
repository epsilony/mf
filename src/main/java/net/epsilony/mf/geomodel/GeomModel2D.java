/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeomModel2D {

    public final static int DIMENSION = 2;
    ArrayList<MFNode> allNodes;
    ArrayList<MFNode> spaceNodes;   //allNode except polygon.getVertes()
    private Polygon2D<MFNode> polygon;

    public Polygon2D<MFNode> getPolygon() {
        return polygon;
    }

    public ArrayList<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public ArrayList<MFNode> getAllNodes() {
        return allNodes;
    }

    public GeomModel2D(Polygon2D<MFNode> polygon, List<MFNode> spaceNodes) {
        this.polygon = polygon;
        allNodes = new ArrayList<>();
        if (null != this.polygon) {
            LinkedList<MFNode> segNds = new LinkedList<>();
            for (Line2D<MFNode> seg : this.polygon) {
                segNds.add(seg.getStart());
            }
            allNodes.addAll(segNds);
        }
        this.spaceNodes = new ArrayList<>(spaceNodes);
        allNodes.addAll(spaceNodes);
    }
}
