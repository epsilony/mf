/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawAnalysisModel extends RawPhysicalModel implements AnalysisModel {

    List<MFNode> spaceNodes;
    List<MFSubdomain> subdomains;

    @Override
    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    @Override
    public List<MFSubdomain> getSubdomains() {
        return subdomains;
    }

    public void setSubdomains(List<MFSubdomain> subdomains) {
        this.subdomains = subdomains;
    }
}
