/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.epsilony.mf.model.MFBoundary;
import net.epsilony.mf.model.MFLineBnd;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class CenterPerturbVisibleSupportDomainSearcher extends VisibleSupportDomainSearcher {

    private double perterbDistanceRatio;
    private double minVertexDistanceRatio;
    private static double DEFAULT_PERTURB_DISTANCE_RATIO = 1e-6;  //perturb distance vs segment length
    // The mininum angle of adjacency segments of polygon. If no angle is less
    // than below, PertubtionSearchMethod works well.
    // Note that the angle of a crack tip is nearly 2pi which is very large.
    private static double DEFAULT_ALLOWABLE_ANGLE = Math.PI / 1800 * 0.95;

    public CenterPerturbVisibleSupportDomainSearcher(
            SupportDomainSearcher supportDomainSearcher,
            boolean ignoreInvisibleNodesInformation) {
        super(supportDomainSearcher, ignoreInvisibleNodesInformation);
        perterbDistanceRatio = DEFAULT_PERTURB_DISTANCE_RATIO;
        double minAngle = DEFAULT_ALLOWABLE_ANGLE;
        minVertexDistanceRatio = perterbDistanceRatio / Math.tan(minAngle);
    }

    public CenterPerturbVisibleSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
        this(supportDomainSearcher, DEFAULT_IGNORE_INVISIBLE_NODES_INFORMATION);
    }

    @Override
    public SupportDomainData searchSupportDomain(double[] center, MFBoundary bndOfCenter, double radius) {
        SupportDomainData searchResult = supportDomainSearcher.searchSupportDomain(center, bndOfCenter, radius);
        prepairResult(searchResult);
        if (null == searchResult.segments || searchResult.segments.isEmpty()) {
            searchResult.visibleNodes.addAll(searchResult.allNodes);
            return searchResult;
        }
        double[] searchCenter = (null == bndOfCenter)
                ? center : perturbCenter(center, ((MFLineBnd) bndOfCenter).getLine(), searchResult.segments);
        filetAllNodesToVisibleNodesByBndOfCenter(null, searchResult);
        filetVisibleNodeBySegments(searchCenter, null, searchResult);
        return searchResult;
    }

    private double[] perturbCenter(double[] center, Segment bndOfCenter, List<Segment> segs) {
        Node start = bndOfCenter.getStart();
        Node end = bndOfCenter.getEnd();
        double[] hCoord = start.getCoord();
        double[] rCoord = end.getCoord();

        double[] pertCenter = new double[2];
        double dx = rCoord[0] - hCoord[0];
        double dy = rCoord[1] - hCoord[1];

        double startDistRatio = Math2D.distance(hCoord, center) / Segment2DUtils.chordLength(bndOfCenter);
        double[] pertOri = center;
        if (startDistRatio <= minVertexDistanceRatio) {
            pertOri = Math2D.pointOnSegment(hCoord, rCoord, minVertexDistanceRatio, null);
        } else if (startDistRatio >= 1 - minVertexDistanceRatio) {
            pertOri = Math2D.pointOnSegment(hCoord, rCoord, 1 - minVertexDistanceRatio, null);
        }

        pertCenter[0] = -dy * perterbDistanceRatio + pertOri[0];
        pertCenter[1] = dx * perterbDistanceRatio + pertOri[1];
        checkPerturbCenter(center, pertCenter, bndOfCenter, segs);
        return pertCenter;
    }

    void checkPerturbCenter(
            double[] center,
            double[] perturbedCenter,
            Segment bnd,
            Collection<? extends Segment> segs) {
        Segment bndNeighbor = null;
        double[] bndNeighborFurtherPoint = null;
        if (center == bnd.getStart().getCoord()) {
            bndNeighbor = bnd.getPred();
            bndNeighborFurtherPoint = bndNeighbor.getStart().getCoord();
        } else if (center == bnd.getEnd().getCoord()) {
            bndNeighbor = bnd.getSucc();
            bndNeighborFurtherPoint = bndNeighbor.getEnd().getCoord();
        }

        if (null != bndNeighbor && Segment2DUtils.isPointStrictlyAtChordLeft(bnd, bndNeighborFurtherPoint)) {
            if (!Segment2DUtils.isPointStrictlyAtChordLeft(bndNeighbor, perturbedCenter)) {
                throw new IllegalStateException("perturbed center over cross neighbor of bnd\n\t"
                        + "center :" + Arrays.toString(center) + "\n\t"
                        + "perturbed center :" + Arrays.toString(perturbedCenter) + "\n\t"
                        + "bnd: " + bnd + "\n\t"
                        + "neighbor of bnd: " + bndNeighbor);
            }
        }

        for (Segment seg : segs) {
            if (seg == bnd || seg == bndNeighbor) {
                continue;
            }
            if (Math2D.isSegmentsIntersecting(center, perturbedCenter, seg.getStart().getCoord(), seg.getEnd().getCoord())) {
                throw new IllegalStateException("Center and perturbed center over cross a segment\n\t"
                        + "center: " + Arrays.toString(center) + "\n\tperturbed center"
                        + Arrays.toString(perturbedCenter) + "\n\tseg: " + seg);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MiscellaneousUtils.simpleToString(this));
        sb.append(String.format("{perterb ratio: %f, min vertes distance ration: %f, upper searcher:",
                perterbDistanceRatio,
                minVertexDistanceRatio));
        sb.append(supportDomainSearcher);
        sb.append("}");
        return sb.toString();
    }
}
