/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.model.support_domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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

    public void setBoundarySegments(Collection<? extends Segment> boundaries) {
        if (null == boundaries) {
            bndSegments = null;
            return;
        }
        bndSegments = new ArrayList<>(boundaries.size());
        for (Segment bnd : boundaries) {
            bndSegments.add(bnd);
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
