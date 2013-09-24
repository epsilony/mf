/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.support_domain;

import java.util.Iterator;
import net.epsilony.mf.geomodel.MFBoundary;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.MiscellaneousUtils;

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
    public SupportDomainData searchSupportDomain(double[] center, MFBoundary bndOfCenter, double radius) {
        SupportDomainData result = upperSearcher.searchSupportDomain(center, bndOfCenter, radius);
        filter(center, result);
        return result;
    }

    private void filter(double[] center, SupportDomainData filterAim) {
        Iterator<MFNode> nodesIter = filterAim.allNodes.iterator();
        while (nodesIter.hasNext()) {
            MFNode node = nodesIter.next();
            double rad = node.getInfluenceRadius();
            if (rad <= Math2D.distance(node.getCoord(), center)) {
                nodesIter.remove();
            }
        }
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + "{upper searcher: " + upperSearcher + "}";
    }
}
