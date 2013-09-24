/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawGeomModel implements GeomModel {

    int dimension;
    List<? extends MFBoundary> boundaries;
    List<MFNode> spaceNodes;

    public void set(GeomModel model) {
        dimension = model.getDimension();
        boundaries = model.getBoundaries();
        spaceNodes = model.getSpaceNodes();
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public List<? extends MFBoundary> getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(List<? extends MFBoundary> boundaries) {
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
