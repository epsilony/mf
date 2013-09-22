/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.geomodel;

import java.util.List;

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
