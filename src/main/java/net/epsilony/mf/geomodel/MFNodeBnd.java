/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodeBnd extends AbstractMFBoundary implements MFBoundary {

    MFNode node;

    public MFNode getNode() {
        return node;
    }

    public void setNode(MFNode node) {
        this.node = node;
    }

    public MFNodeBnd(MFNode node) {
        this.node = node;
    }

    public MFNodeBnd() {
    }

    @Override
    public MFNode getGeomUnit() {
        return node;
    }

    @Override
    public void setGeomUnit(GeomUnit geomUnit) {
        node = getGeomUnit();
    }
}
