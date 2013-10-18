/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.mf.model.subdomain.MFSubdomain;
import java.util.List;
import net.epsilony.mf.model.subdomain.MFSubdomainType;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface AnalysisModel {

    public PhysicalModel getPhysicalModel();

    public PhysicalModel getFractionizedModel();

    public List<MFNode> getSpaceNodes();

    List<MFSubdomain> getSubdomains(MFSubdomainType key);
}
