/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.support_domain;

import net.epsilony.mf.geomodel.MFBoundary;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface SupportDomainSearcher {

    SupportDomainData searchSupportDomain(double[] center, MFBoundary bndOfCenter, double radius);
}
