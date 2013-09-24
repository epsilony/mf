/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.influence;

import java.io.Serializable;
import net.epsilony.mf.geomodel.MFBoundary;
import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcher;
import net.epsilony.tb.IntIdentity;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface InfluenceRadiusCalculator extends IntIdentity, Serializable {

    double calcInflucenceRadius(double[] coord, MFBoundary bnd);

    void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher);
}
