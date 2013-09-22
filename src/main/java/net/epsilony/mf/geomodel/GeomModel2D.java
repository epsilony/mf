/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import net.epsilony.tb.solid.GeneralPolygon2D;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeomModel2D {

    public final static int DIMENSION = 2;
    List<MFNode> spaceNodes;   //allNode except polygon.getVertes()
    private GeneralPolygon2D<MFLine, MFNode> polygon;

    public GeneralPolygon2D<MFLine, MFNode> getPolygon() {
        return polygon;
    }

    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    public void setPolygon(GeneralPolygon2D<MFLine, MFNode> polygon) {
        this.polygon = polygon;
    }
}
