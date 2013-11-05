/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.EnumMap;
import net.epsilony.mf.model.subdomain.MFSubdomain;
import java.util.List;
import net.epsilony.mf.process.MFProcessType;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawAnalysisModel implements AnalysisModel {

    List<MFNode> spaceNodes;
    PhysicalModel physicalModel;
    PhysicalModel fractionizedModel;
    EnumMap<MFProcessType, List<MFSubdomain>> subdomains = new EnumMap<>(MFProcessType.class);

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
    public List<MFSubdomain> getSubdomains(MFProcessType key) {
        return subdomains.get(key);
    }

    public void setSubdomains(MFProcessType key, List<MFSubdomain> subdomains) {
        this.subdomains.put(key, subdomains);
    }
}
