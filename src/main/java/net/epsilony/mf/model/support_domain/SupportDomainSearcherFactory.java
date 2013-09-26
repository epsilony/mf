/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import net.epsilony.mf.model.MFBoundary;
import net.epsilony.mf.model.MFLineBnd;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.model.search.LRTreeNodesSphereSearcher;
import net.epsilony.mf.model.search.LRTreeSegmentChordIntersectingSphereSearcher;
import net.epsilony.mf.model.search.SphereSearcher;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.SegmentChainsIterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SupportDomainSearcherFactory implements Factory<SupportDomainSearcher> {

    public static final boolean DEFAULT_USE_CENTER_PERTURB = true;
    public static final boolean DEFAULT_IGNORGE_INVISIBLE_NODES_INFORMATION = true;
    public static final boolean DEFAULT_FILTER_BY_INFLUENCE_RADIUS = false;
    SphereSearcher<MFNode> nodesSearcher = new LRTreeNodesSphereSearcher<>();
    SphereSearcher<Segment> segmentsSearcher = new LRTreeSegmentChordIntersectingSphereSearcher();
    boolean useCenterPerturb = DEFAULT_USE_CENTER_PERTURB;
    boolean ignoreInvisibleNodesInformation = DEFAULT_IGNORGE_INVISIBLE_NODES_INFORMATION;
    boolean filterByInfluenceRad = DEFAULT_FILTER_BY_INFLUENCE_RADIUS;
    private Collection<? extends MFNode> nodes;
    private Collection<Segment> bndSegments;
    //TODO: generalize interface

    public void setBoundarySegments(Collection<? extends MFBoundary> boundaries) {
        bndSegments = new ArrayList<>(boundaries.size());
        for (MFBoundary bnd : boundaries) {
            bndSegments.add(((MFLineBnd) bnd).getLine());
        }
    }

    public boolean isIgnoreInvisibleNodesInformation() {
        return ignoreInvisibleNodesInformation;
    }

    public void setIgnoreInvisibleNodesInformation(boolean ignoreInvisibleNodesInformation) {
        this.ignoreInvisibleNodesInformation = ignoreInvisibleNodesInformation;
    }

    public SupportDomainSearcherFactory() {
    }

    public SupportDomainSearcherFactory(
            boolean filterByInfluenceRad,
            SphereSearcher<MFNode> nodesSearcher,
            SphereSearcher<Segment> segmentsSearcher,
            boolean useCenterPerturb) {
        this.filterByInfluenceRad = filterByInfluenceRad;
        this.nodesSearcher = nodesSearcher;
        this.segmentsSearcher = segmentsSearcher;
        this.useCenterPerturb = DEFAULT_USE_CENTER_PERTURB;
    }

    @Override
    public SupportDomainSearcher produce() {
        nodesSearcher.setAll(nodes);
        SphereSearcher<Segment> realSegmentsSearcher = null;
        if (null != bndSegments && segmentsSearcher != null) {
            segmentsSearcher.setAll(bndSegments);
            realSegmentsSearcher = segmentsSearcher;
        }
        SupportDomainSearcher result = new RawSupportDomainSearcher(nodesSearcher, realSegmentsSearcher);
        if (filterByInfluenceRad) {
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

    public void setAllMFNodes(Collection<? extends MFNode> nodes) {
        this.nodes = nodes;
    }

    public void setBoundarySegmentsChainsHeads(Collection<? extends Segment> chainsHeads) {
        if (null != chainsHeads && segmentsSearcher != null) {
            SegmentChainsIterator<Segment> iter = new SegmentChainsIterator<>(chainsHeads);
            LinkedList<Segment> segments = new LinkedList<>();
            while (iter.hasNext()) {
                segments.add(iter.next());
            }
            bndSegments = segments;
        }
    }

    public void setUseCenterPerturb(boolean useCenterPerturb) {
        this.useCenterPerturb = useCenterPerturb;
    }

    public boolean isFilterByInfluenceRad() {
        return filterByInfluenceRad;
    }

    public void setFilterByInfluenceRad(boolean filterByInflucenceRad) {
        this.filterByInfluenceRad = filterByInflucenceRad;
    }
}