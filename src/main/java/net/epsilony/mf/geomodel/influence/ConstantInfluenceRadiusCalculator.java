/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.influence;

import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ConstantInfluenceRadiusCalculator implements InfluenceRadiusCalculator {

    double rad;

    @Override
    public double calcInflucenceRadius(double[] coord, Segment seg) {
        return rad;
    }

    public ConstantInfluenceRadiusCalculator(double rad) {
        this.rad = rad;
    }

    @Override
    public void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
    }
}
