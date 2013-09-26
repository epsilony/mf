/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.tb.solid.Facet;
import java.util.List;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class FacetModel implements AnalysisModel {

    public final static int DIMENSION = 2;
    private Facet facet;
    RawGeomModel model = new RawGeomModel();
    boolean needPrepare = true;

    public static boolean checkPolygon(Facet polygon) {
        for (Segment seg : polygon) {
            if (!(seg instanceof Line) || !(seg.getStart() instanceof MFNode)) {
                return false;
            }
        }
        return true;
    }

    public Facet getFacet() {
        return facet;
    }

    @Override
    public List<? extends MFBoundary> getBoundaries() {
        prepare();
        return model.getBoundaries();
    }

    public void setVolumeLoads(List<MFLoad> volumeLoads) {
        model.setVolumeLoads(volumeLoads);
    }

    @Override
    public List<MFLoad> getVolumeLoads() {
        return model.getVolumeLoads();
    }

    @Override
    public List<MFNode> getSpaceNodes() {
        return model.getSpaceNodes();
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        model.setSpaceNodes(spaceNodes);
    }

    public void setFacet(Facet facet) {
        if (!checkPolygon(facet)) {
            throw new IllegalArgumentException();
        }
        needPrepare = true;
        this.facet = facet;
    }

    private void prepare() {
        if (!needPrepare) {
            return;
        }
        model.setDimension(DIMENSION);
        model.setBoundaries((List) MFLineBnd.wraps(facet.getSegments()));
        needPrepare = false;
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
