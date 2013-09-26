/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawGeomModel implements AnalysisModel {

    int dimension;
    List<? extends MFBoundary> boundaries;
    List<MFNode> spaceNodes;
    List<MFLoad> volumeLoads;

    public void set(AnalysisModel model) {
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
    public List<MFLoad> getVolumeLoads() {
        return volumeLoads;
    }

    public void setVolumeLoads(List<MFLoad> volumeLoads) {
        this.volumeLoads = volumeLoads;
    }

    @Override
    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }
}
