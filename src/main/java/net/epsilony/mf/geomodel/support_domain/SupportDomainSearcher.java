/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.support_domain;

import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface SupportDomainSearcher {

    public abstract SupportDomainData searchSupportDomain(double[] center, Segment bndOfCenter, double radius);
}
