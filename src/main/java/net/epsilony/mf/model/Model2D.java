/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Model2D {

    public final static int DIMENSION = 2;
    ArrayList<MFNode> allNodes;
    ArrayList<MFNode> spaceNodes;   //allNode except polygon.getVertes()
    private Polygon2D polygon;

    public Polygon2D getPolygon() {
        return polygon;
    }

    public ArrayList<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public ArrayList<MFNode> getAllNodes() {
        return allNodes;
    }

    public Model2D(Polygon2D polygon, List<MFNode> spaceNodes) {
        this.polygon = polygon;
        this.spaceNodes = new ArrayList<>(spaceNodes);
        allNodes = new ArrayList<>(spaceNodes);
        if (null != this.polygon) {
            LinkedList<MFNode> segNds = new LinkedList<>();
            for (Line2D seg : this.polygon) {
                segNds.add((MFNode) seg.getStart());
            }
            allNodes.addAll(segNds);
        }
        int id = 0;
        for (Node nd : allNodes) {
            nd.setId(id);
            id++;
        }
    }
}
