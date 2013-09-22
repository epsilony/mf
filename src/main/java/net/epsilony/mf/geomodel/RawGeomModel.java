/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawGeomModel implements GeomModel {

    int dimension;
    List<MFBoundary> boundaries;
    List<MFNode> spaceNodes;

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public List<MFBoundary> getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(List<MFBoundary> boundaries) {
        this.boundaries = boundaries;
    }

    @Override
    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }
}
