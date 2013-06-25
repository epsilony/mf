/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.influence;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface InfluenceRadiusCalculator {

    double calcInflucenceRadius(MFNode node, Segment seg);

    SupportDomainSearcher getSupportDomainSearcher();

    void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher);
}
