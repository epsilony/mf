/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import net.epsilony.mf.model.MFBoundary;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.model.search.SphereSearcher;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawSupportDomainSearcher implements SupportDomainSearcher {

    SphereSearcher<MFNode> nodesSearcher;
    SphereSearcher<Segment> segmentSearcher;

    public RawSupportDomainSearcher(SphereSearcher<MFNode> nodesSearcher, SphereSearcher<Segment> segmentSearcher) {
        this.nodesSearcher = nodesSearcher;
        this.segmentSearcher = segmentSearcher;
    }

    public RawSupportDomainSearcher(SphereSearcher<MFNode> nodesSearcher) {
        this(nodesSearcher, null);
    }

    @Override
    public SupportDomainData searchSupportDomain(double[] center, MFBoundary bndOfCenter, double radius) {
        SupportDomainData result = new SupportDomainData();
        result.allNodes = nodesSearcher.searchInSphere(center, radius);
        if (null != segmentSearcher) {
            result.segments = segmentSearcher.searchInSphere(center, radius);
        }
        return result;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + "{" + "nodesSearcher="
                + nodesSearcher
                + ", segmentSearcher=" + segmentSearcher + '}';
    }
}
