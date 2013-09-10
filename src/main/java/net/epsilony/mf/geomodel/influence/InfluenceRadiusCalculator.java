/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.influence;

import java.io.Serializable;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcher;
import net.epsilony.tb.IntIdentity;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface InfluenceRadiusCalculator extends IntIdentity, Serializable {

    double calcInflucenceRadius(double[] coord, Segment seg);

    void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher);
}
