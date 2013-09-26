/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawPhysicalModel implements PhysicalModel {

    List<? extends MFBoundary> boundaries;
    int dimension;
    List<MFLoad> volumeLoads;

    @Override
    public List<? extends MFBoundary> getBoundaries() {
        return boundaries;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public List<MFLoad> getVolumeLoads() {
        return volumeLoads;
    }

    public void setBoundaries(List<? extends MFBoundary> boundaries) {
        this.boundaries = boundaries;
    }

    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setVolumeLoads(List<MFLoad> volumeLoads) {
        this.volumeLoads = volumeLoads;
    }
}
