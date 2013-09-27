/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.influence;

import java.io.Serializable;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tb.IntIdentity;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface InfluenceRadiusCalculator extends IntIdentity, Serializable {

    double calcInflucenceRadius(double[] coord, GeomUnit bnd);

    void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher);
}
