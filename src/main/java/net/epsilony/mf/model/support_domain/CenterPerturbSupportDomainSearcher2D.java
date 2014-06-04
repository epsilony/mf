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

import static net.epsilony.tb.analysis.Math2D.isSegmentsIntersecting;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.search.MetricSearcher;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.tb.solid.Node;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class CenterPerturbSupportDomainSearcher2D implements SupportDomainSearcher {

    private static double                          DEFAULT_PERTURB_DISTANCE_RATIO   = 1e-6;                                  // perturb
                                                                                                                              // distance
                                                                                                                              // vs
                                                                                                                              // segment
                                                                                                                              // length
    // The mininum angle of adjacency segments of polygon. If no angle is less
    // than below, PertubtionSearchMethod works well.
    // Note that the angle of a crack tip is nearly 2pi which is very large.
    private static double                          DEFAULT_MIN_ALLOWABLE_ANGLE      = Math.PI / 1800 * 0.95;

    private final double                           perterbDistanceRatio             = DEFAULT_PERTURB_DISTANCE_RATIO;         ;
    private final double                           minVertexDistanceRatio           = DEFAULT_MIN_ALLOWABLE_ANGLE;
    private double[]                               center;
    private MFGeomUnit                             bndOfCenter;
    private double[]                               bndOutNormal;
    private double                                 radius;
    private final OutNormalPositionSegmentSearcher outNormalPositionSegmentSearcher = new OutNormalPositionSegmentSearcher();

    MetricSearcher<? extends MFNode>               allNodesMetricSearcher;
    MetricSearcher<? extends MFLine>               allSegmentsMetricSearcher;

    @Override
    public void search(SupportDomainData outputData) {
        searchAllNodes(outputData.getAllNodesContainer());
        searchSegments(outputData.getSegmentsContainer());

        List<MFLine> segments = outputData.getSegmentsContainer();
        if (null == bndOfCenter && null != bndOutNormal && !segments.isEmpty()) {
            bndOfCenter = outNormalPositionSegmentSearcher.search(center, bndOutNormal, segments);
        }

        MFLine bndSegment = (MFLine) bndOfCenter;

        double[] searchCenter = (null == bndOfCenter) ? center : perturbCenter(center, bndSegment, segments);
        filetVisibleNodeBySegments(searchCenter, outputData);

    }

    public void searchAllNodes(Collection<? super MFNode> allNodesContainer) {
        allNodesMetricSearcher.setCenter(center);
        allNodesMetricSearcher.setRadius(radius);
        allNodesMetricSearcher.search(allNodesContainer);
    }

    public void searchSegments(Collection<? super MFLine> segments) {
        if (allSegmentsMetricSearcher == null) {
            segments.clear();
            return;
        }

        allSegmentsMetricSearcher.setCenter(center);
        allSegmentsMetricSearcher.setRadius(radius);
        allSegmentsMetricSearcher.search(segments);
    }

    private double[] perturbCenter(double[] center, MFLine bndOfCenter, List<MFLine> segs) {
        Node start = bndOfCenter.getStart();
        Node end = bndOfCenter.getEnd();
        double[] hCoord = start.getCoord();
        double[] rCoord = end.getCoord();

        double[] pertCenter = new double[2];
        double dx = rCoord[0] - hCoord[0];
        double dy = rCoord[1] - hCoord[1];

        double startDistRatio = Math2D.distance(hCoord, center) / MFLine2DUtils.chordLength(bndOfCenter);
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

    void checkPerturbCenter(double[] center, double[] perturbedCenter, MFLine bnd, Collection<? extends MFLine> segs) {
        MFLine bndNeighbor = null;
        double[] bndNeighborFurtherPoint = null;
        if (center == bnd.getStart().getCoord()) {
            bndNeighbor = bnd.getPred();
            bndNeighborFurtherPoint = bndNeighbor.getStart().getCoord();
        } else if (center == bnd.getEnd().getCoord()) {
            bndNeighbor = bnd.getSucc();
            bndNeighborFurtherPoint = bndNeighbor.getEnd().getCoord();
        }

        if (null != bndNeighbor && MFLine2DUtils.isPointStrictlyAtChordLeft(bnd, bndNeighborFurtherPoint)) {
            if (!MFLine2DUtils.isPointStrictlyAtChordLeft(bndNeighbor, perturbedCenter)) {
                throw new IllegalStateException("perturbed center over cross neighbor of bnd\n\t" + "center :"
                        + Arrays.toString(center) + "\n\t" + "perturbed center :" + Arrays.toString(perturbedCenter)
                        + "\n\t" + "bnd: " + bnd + "\n\t" + "neighbor of bnd: " + bndNeighbor);
            }
        }

        for (MFLine seg : segs) {
            if (seg == bnd || seg == bndNeighbor) {
                continue;
            }
            if (Math2D.isSegmentsIntersecting(center, perturbedCenter, seg.getStart().getCoord(), seg.getEnd()
                    .getCoord())) {
                throw new IllegalStateException("Center and perturbed center over cross a segment\n\t" + "center: "
                        + Arrays.toString(center) + "\n\tperturbed center" + Arrays.toString(perturbedCenter)
                        + "\n\tseg: " + seg);
            }
        }
    }

    protected void filetVisibleNodeBySegments(double[] distrubedCenter, SupportDomainData supportDomainData) {

        List<MFNode> visibleNodesContainer = supportDomainData.getVisibleNodesContainer();
        visibleNodesContainer.clear();
        visibleNodesContainer.addAll(supportDomainData.getAllNodesContainer());

        Map<MFNode, MFLine> invisibleBlockingMap = supportDomainData.getInvisibleBlockingMap();
        if (null != invisibleBlockingMap) {
            invisibleBlockingMap.clear();
        }
        for (MFLine seg : supportDomainData.getSegmentsContainer()) {
            Iterator<MFNode> rsIter = visibleNodesContainer.iterator();
            Node start = seg.getStart();
            Node end = seg.getEnd();
            double[] hCoord = start.getCoord();
            double[] rCoord = end.getCoord();
            while (rsIter.hasNext()) {
                MFNode nd = rsIter.next();
                if (nd == start || nd == end) {
                    continue;
                }
                if (isSegmentsIntersecting(distrubedCenter, nd.getCoord(), hCoord, rCoord)) {
                    rsIter.remove();
                    if (null != invisibleBlockingMap) {
                        invisibleBlockingMap.put(nd, seg);
                    }
                }
            }
        }
    }

    @Override
    public void setCenter(double[] center) {
        this.center = center;
    }

    @Override
    public void setBoundary(MFGeomUnit bndOfCenter) {
        this.bndOfCenter = bndOfCenter;
    }

    @Override
    public void setUnitOutNormal(double[] bndOutNormal) {
        this.bndOutNormal = bndOutNormal;
    }

    @Override
    public void setRadius(double radius) {
        this.radius = radius;
    }

    public CenterPerturbSupportDomainSearcher2D() {
    }

    public CenterPerturbSupportDomainSearcher2D(MetricSearcher<? extends MFNode> allNodesMetricSearcher,
            MetricSearcher<? extends MFLine> allSegmentsMetricSearcher) {
        this.allNodesMetricSearcher = allNodesMetricSearcher;
        this.allSegmentsMetricSearcher = allSegmentsMetricSearcher;
    }

    public MetricSearcher<? extends MFNode> getAllNodesMetricSearcher() {
        return allNodesMetricSearcher;
    }

    public void setAllNodesMetricSearcher(MetricSearcher<? extends MFNode> allNodesMetricSearcher) {
        this.allNodesMetricSearcher = allNodesMetricSearcher;
    }

    public MetricSearcher<? extends MFLine> getAllSegmentsMetricSearcher() {
        return allSegmentsMetricSearcher;
    }

    public void setAllSegmentsMetricSearcher(MetricSearcher<? extends MFLine> allSegmentsMetricSearcher) {
        this.allSegmentsMetricSearcher = allSegmentsMetricSearcher;
    }

}
