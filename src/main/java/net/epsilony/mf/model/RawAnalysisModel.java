/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.mf.model.subdomain.MFSubdomain;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawAnalysisModel implements AnalysisModel {

    List<MFNode> spaceNodes;
    PhysicalModel physicalModel;
    PhysicalModel fractionizedModel;
    List<List<MFSubdomain>> subdomainLists = Arrays.asList(null, null, null, null);

    @Override
    public PhysicalModel getPhysicalModel() {
        return physicalModel;
    }

    public void setPhysicalModel(PhysicalModel physicalModel) {
        this.physicalModel = physicalModel;
    }

    @Override
    public PhysicalModel getFractionizedModel() {
        return fractionizedModel;
    }

    public void setFractionizedModel(PhysicalModel fractionizedModel) {
        this.fractionizedModel = fractionizedModel;
    }

    @Override
    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    @Override
    public List<MFSubdomain> getSubdomains(int dimension) {
        return subdomainLists.get(dimension);
    }

    public void setSubdomains(int dimension, List<MFSubdomain> subdomains) {
        subdomainLists.set(dimension, subdomains);
    }
}
