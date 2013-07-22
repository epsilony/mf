/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.influence;

import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface InfluenceRadiusCalculator {

    double calcInflucenceRadius(MFNode node, Segment seg);

    SupportDomainSearcher getSupportDomainSearcher();

    void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher);
}
