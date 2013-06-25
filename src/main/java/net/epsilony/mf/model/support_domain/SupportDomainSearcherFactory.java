/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.model.search.LRTreeNodesSphereSearcher;
import net.epsilony.mf.model.search.LRTreeSegmentChordIntersectingSphereSearcher;
import net.epsilony.mf.model.search.SphereSearcher;
import net.epsilony.tb.Factory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SupportDomainSearcherFactory implements Factory<SupportDomainSearcher> {

    public static final boolean DEFAULT_USE_CENTER_PERTURB = true;
    public static final boolean DEFAULT_IGNORGE_INVISIBLE_NODES_INFORMATION = true;
    SphereSearcher<MFNode> nodesSearcher;
    SphereSearcher<Segment> segmentsSearcher;
    boolean useCenterPerturb = DEFAULT_USE_CENTER_PERTURB;
    boolean ignoreInvisibleNodesInformation = DEFAULT_IGNORGE_INVISIBLE_NODES_INFORMATION;
    boolean filterByInflucenceRad;

    public boolean isIgnoreInvisibleNodesInformation() {
        return ignoreInvisibleNodesInformation;
    }

    public void setIgnoreInvisibleNodesInformation(boolean ignoreInvisibleNodesInformation) {
        this.ignoreInvisibleNodesInformation = ignoreInvisibleNodesInformation;
    }

    public SupportDomainSearcherFactory() {
        filterByInflucenceRad = false;
        nodesSearcher = new LRTreeNodesSphereSearcher<>();
        segmentsSearcher = new LRTreeSegmentChordIntersectingSphereSearcher();
    }

    public SupportDomainSearcherFactory(
            SphereSearcher<MFNode> nodesSearcher,
            SphereSearcher<Segment> segmentsSearcher) {
        this(false, nodesSearcher, segmentsSearcher);
    }

    public SupportDomainSearcherFactory(
            boolean filterByInfluenceRad,
            SphereSearcher<MFNode> nodesSearcher,
            SphereSearcher<Segment> segmentsSearcher) {
        this(filterByInfluenceRad, nodesSearcher, segmentsSearcher, DEFAULT_USE_CENTER_PERTURB);
    }

    public SupportDomainSearcherFactory(
            boolean filterByInfluenceRad,
            SphereSearcher<MFNode> nodesSearcher,
            SphereSearcher<Segment> segmentsSearcher,
            boolean useCenterPerturb) {
        this.filterByInflucenceRad = filterByInfluenceRad;
        this.nodesSearcher = nodesSearcher;
        this.segmentsSearcher = segmentsSearcher;
        this.useCenterPerturb = DEFAULT_USE_CENTER_PERTURB;
    }

    @Override
    public SupportDomainSearcher produce() {
        SupportDomainSearcher result = new RawSupportDomainSearcher(nodesSearcher, segmentsSearcher);
        if (filterByInflucenceRad) {
            result = new FilterByInfluenceDomain(result);
        }
        if (useCenterPerturb) {
            result = new CenterPerturbVisibleSupportDomainSearcher(result, ignoreInvisibleNodesInformation);
        } else {
            result = new VisibleSupportDomainSearcher(result, ignoreInvisibleNodesInformation);
        }
        return result;
    }

    public void setNodesSearcher(SphereSearcher<MFNode> nodesSearcher) {
        this.nodesSearcher = nodesSearcher;
    }

    public void setSegmentsSearcher(SphereSearcher<Segment> segmentsSearcher) {
        this.segmentsSearcher = segmentsSearcher;
    }

    public SphereSearcher<MFNode> getNodesSearcher() {
        return nodesSearcher;
    }

    public SphereSearcher<Segment> getSegmentsSearcher() {
        return segmentsSearcher;
    }

    public void setUseCenterPerturb(boolean useCenterPerturb) {
        this.useCenterPerturb = useCenterPerturb;
    }

    public boolean isFilterByInflucenceRad() {
        return filterByInflucenceRad;
    }

    public void setFilterByInflucenceRad(boolean filterByInflucenceRad) {
        this.filterByInflucenceRad = filterByInflucenceRad;
    }
}
