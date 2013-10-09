/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.mf.model.subdomain.MFSubdomain;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFNodeSubdomain implements MFSubdomain{
    MFNode node;

    public MFNode getNode() {
        return node;
    }

    public void setNode(MFNode node) {
        this.node = node;
    }

    public MFNodeSubdomain() {
    }

    public MFNodeSubdomain(MFNode node) {
        this.node = node;
    }
}
