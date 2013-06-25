/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.influence;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ConstantInfluenceRadiusCalculator implements InfluenceRadiusCalculator {

    double rad;

    @Override
    public double calcInflucenceRadius(MFNode node, Segment seg) {
        node.setInfluenceRadius(rad);
        return rad;
    }

    public ConstantInfluenceRadiusCalculator(double rad) {
        this.rad = rad;
    }

    @Override
    public SupportDomainSearcher getSupportDomainSearcher() {
        return null;
    }

    @Override
    public void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
    }
}
