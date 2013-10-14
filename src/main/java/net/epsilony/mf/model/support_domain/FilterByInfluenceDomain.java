/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import java.util.Iterator;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class FilterByInfluenceDomain implements SupportDomainSearcher {

    SupportDomainSearcher upperSearcher;

    public FilterByInfluenceDomain(
            SupportDomainSearcher supportDomainSearcher) {
        this.upperSearcher = supportDomainSearcher;
    }

    @Override
    public SupportDomainData searchSupportDomain() {
        SupportDomainData result = upperSearcher.searchSupportDomain();
        filter(result);
        return result;
    }

    private void filter(SupportDomainData filterAim) {
        Iterator<MFNode> nodesIter = filterAim.allNodes.iterator();
        while (nodesIter.hasNext()) {
            MFNode node = nodesIter.next();
            double rad = node.getInfluenceRadius();
            if (rad <= Math2D.distance(node.getCoord(), getCenter())) {
                nodesIter.remove();
            }
        }
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + "{upper searcher: " + upperSearcher + "}";
    }

    @Override
    public void setCenter(double[] center) {
        upperSearcher.setCenter(center);
    }

    @Override
    public void setBoundary(GeomUnit bndOfCenter) {
        upperSearcher.setBoundary(bndOfCenter);
    }

    @Override
    public void setUnitOutNormal(double[] bndOutNormal) {
        upperSearcher.setUnitOutNormal(bndOutNormal);
    }

    @Override
    public void setRadius(double radius) {
        upperSearcher.setRadius(radius);
    }

    @Override
    public double[] getUnitOutNormal() {
        return upperSearcher.getUnitOutNormal();
    }

    @Override
    public GeomUnit getBoundary() {
        return upperSearcher.getBoundary();
    }

    @Override
    public double[] getCenter() {
        return upperSearcher.getCenter();
    }

    @Override
    public double getRadius() {
        return upperSearcher.getRadius();
    }
}
