/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import net.epsilony.mf.model.MFBoundary;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface SupportDomainSearcher {

    SupportDomainData searchSupportDomain(double[] center, MFBoundary bndOfCenter, double radius);
}
