/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawAnalysisModel extends RawPhysicalModel implements AnalysisModel {

    List<MFNode> spaceNodes;

    public void set(AnalysisModel model) {
        dimension = model.getDimension();
        boundaries = model.getBoundaries();
        spaceNodes = model.getSpaceNodes();
    }

    @Override
    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }
}
