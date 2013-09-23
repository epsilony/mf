/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodeBnd implements MFBoundary {
    int id;
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
}
