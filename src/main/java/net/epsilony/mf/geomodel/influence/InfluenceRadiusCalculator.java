/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.influence;

import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface InfluenceRadiusCalculator {

    double calcInflucenceRadius(double[] coord, Segment seg);

    void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher);
}
