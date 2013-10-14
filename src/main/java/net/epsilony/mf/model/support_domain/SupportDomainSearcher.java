/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface SupportDomainSearcher {

    void setCenter(double[] center);

    void setBoundary(GeomUnit bndOfCenter);

    void setUnitOutNormal(double[] bndOutNormal);

    void setRadius(double radius);

    SupportDomainData searchSupportDomain();

    double[] getUnitOutNormal();

    GeomUnit getBoundary();

    double[] getCenter();

    double getRadius();
}
