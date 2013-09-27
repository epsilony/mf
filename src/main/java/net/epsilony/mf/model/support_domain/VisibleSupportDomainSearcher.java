/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import java.util.Iterator;
import java.util.LinkedList;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;
import static net.epsilony.tb.analysis.Math2D.cross;
import static net.epsilony.tb.analysis.Math2D.isSegmentsIntersecting;
import net.epsilony.tb.pair.PairPack;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Node;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Deprecated
public class VisibleSupportDomainSearcher implements SupportDomainSearcher {

    public static final boolean DEFAULT_IGNORE_INVISIBLE_NODES_INFORMATION = true;
    SupportDomainSearcher supportDomainSearcher;
    boolean ignoreInvisibleNodesInformation;

    public VisibleSupportDomainSearcher(
            SupportDomainSearcher supportDomainSearcher,
            boolean ignoreInvisibleNodesInformation) {
        this.supportDomainSearcher = supportDomainSearcher;
        this.ignoreInvisibleNodesInformation = ignoreInvisibleNodesInformation;
    }

    public VisibleSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
        this(supportDomainSearcher, DEFAULT_IGNORE_INVISIBLE_NODES_INFORMATION);
    }

    @Override
    public SupportDomainData searchSupportDomain(double[] center, GeomUnit bndOfCenter, double radius) {
        SupportDomainData result = supportDomainSearcher.searchSupportDomain(center, bndOfCenter, radius);
        prepairResult(result);
        if (result.segments == null || result.segments.isEmpty()) {
            result.visibleNodes.addAll(result.allNodes);
            return result;
        }

        filetAllNodesToVisibleNodesByBndOfCenter(bndOfCenter, result);

        filetVisibleNodeBySegments(center, bndOfCenter, result);
        return result;
    }

    protected void prepairResult(SupportDomainData result) {
        result.visibleNodes = new LinkedList<>();
        if (!ignoreInvisibleNodesInformation) {
            result.invisibleNodesAndBlockingSegments = new LinkedList<>();
        }
    }

    protected void filetVisibleNodeBySegments(double[] center, GeomUnit bndOfCenter, SupportDomainData result) {
        Segment bndLine = bndOfCenter == null ? null : (Segment) bndOfCenter;
        for (Segment seg : result.segments) {
            if (seg == bndLine) {
                continue;
            }
            Iterator<MFNode> rsIter = result.visibleNodes.iterator();
            Node start = seg.getStart();
            Node end = seg.getEnd();
            double[] hCoord = start.getCoord();
            double[] rCoord = end.getCoord();
            while (rsIter.hasNext()) {
                MFNode nd = rsIter.next();
                if (nd == start || nd == end) {
                    continue;
                }
                if (isSegmentsIntersecting(center, nd.getCoord(), hCoord, rCoord)) {
                    rsIter.remove();
                    if (!isIgnoreInvisibleNodesInformation()) {
                        result.invisibleNodesAndBlockingSegments.add(new PairPack<>(nd, seg));
                    }
                }
            }
        }
    }

    protected void filetAllNodesToVisibleNodesByBndOfCenter(GeomUnit bndOfCenter, SupportDomainData result) {

        if (null == bndOfCenter) {
            result.visibleNodes.addAll(result.allNodes);
        } else {
            Segment line = (Segment) bndOfCenter;
            double[] hc = line.getStart().getCoord();
            double[] rc = line.getEnd().getCoord();
            double dx = rc[0] - hc[0];
            double dy = rc[1] - hc[1];
            Iterator<MFNode> rsIter = result.allNodes.iterator();
            while (rsIter.hasNext()) {
                MFNode nd = rsIter.next();
                double[] nc = nd.getCoord();
                if (cross(dx, dy, nc[0] - hc[0], nc[1] - hc[1]) < 0) {
                    if (!isIgnoreInvisibleNodesInformation()) {
                        result.invisibleNodesAndBlockingSegments.add(new PairPack<>(nd, line));
                    }
                } else {
                    result.visibleNodes.add(nd);
                }
            }
        }
    }

    public boolean isIgnoreInvisibleNodesInformation() {
        return ignoreInvisibleNodesInformation;
    }

    public void setIgnoreInvisibleNodesInformation(boolean ignoreInvisibleNodesInformation) {
        this.ignoreInvisibleNodesInformation = ignoreInvisibleNodesInformation;
    }
}
