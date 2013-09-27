/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface SupportDomainSearcher {

    SupportDomainData searchSupportDomain(double[] center, GeomUnit bndOfCenter, double radius);
}
