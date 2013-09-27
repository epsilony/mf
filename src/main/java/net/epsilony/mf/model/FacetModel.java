/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class FacetModel extends RawAnalysisModel {

    public final static int DIMENSION = 2;

    public FacetModel() {
        dimension = DIMENSION;
    }

    public static boolean checkPolygon(Facet polygon) {
        for (Segment seg : polygon) {
            if (!(seg instanceof Line) || !(seg.getStart() instanceof MFNode)) {
                return false;
            }
        }
        return true;
    }

    public Facet getFacet() {
        return (Facet) getGeomRoot();
    }

    public void setFacet(Facet facet) {
        super.setGeomRoot(facet);
    }

    @Override
    public void setDimension(int dim) {
        if (dim != DIMENSION) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Facet getGeomRoot() {
        return (Facet) super.getGeomRoot();
    }

    @Override
    public void setGeomRoot(GeomUnit geomRoot) {
        setFacet((Facet) geomRoot);
    }
}
