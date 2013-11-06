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

import java.util.Iterator;
import java.util.LinkedList;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Segment;
import static net.epsilony.tb.analysis.Math2D.cross;
import static net.epsilony.tb.analysis.Math2D.isSegmentsIntersecting;
import net.epsilony.tb.pair.PairPack;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2DUtils;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
class VisibleSupportDomainSearcher implements SupportDomainSearcher {

    public static final boolean DEFAULT_IGNORE_INVISIBLE_NODES_INFORMATION = true;
    public static final double DEFAULT_MAX_CENTER_BND_DISTANCE = 1e-6;
    public static final double DEFAULT_MIN_BND_OUTNORMAL_COSINE = FastMath.cos(FastMath.PI / 3600);
    public static final double DEFAULT_UNITY_TOL = 1e-12;
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
    public SupportDomainData searchSupportDomain() {
        SupportDomainData result = supportDomainSearcher.searchSupportDomain();
        prepairResult(result);

        if (result.segments == null || result.segments.isEmpty()) {
            result.visibleNodes.addAll(result.allNodes);
            return result;
        }

        if (null == getBoundary() && null != getUnitOutNormal()) {
            searchBndByCenterAndOutNormal(result);
        }

        filetAllNodesToVisibleNodesByBndOfCenter(getBoundary(), result);

        filetVisibleNodeBySegments(getCenter(), getBoundary(), result);
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

    protected void searchBndByCenterAndOutNormal(SupportDomainData result) {
        double[] bndOutNormal = getUnitOutNormal();
        checkUnity(bndOutNormal);
        if (null == bndOutNormal) {
            setBoundary(null);
            return;
        }
        double[] center = getCenter();
        for (Segment segment : result.segments) {
            if (Segment2DUtils.distanceToChord(segment, getCenter()) > DEFAULT_MAX_CENTER_BND_DISTANCE) {
                continue;
            }
            double par = Math2D.projectionParameter(segment.getStart().getCoord(), segment.getEnd().getCoord(), center);
            if (par > 1 || par < 0) {
                continue;
            }
            double[] chordUnitOutNormal = Segment2DUtils.chordUnitOutNormal(segment, null);
            double dot = Math2D.dot(bndOutNormal, chordUnitOutNormal);
            if (dot > DEFAULT_MIN_BND_OUTNORMAL_COSINE) {
                setBoundary(segment);
            }
        }
    }

    private void checkUnity(double[] outNorm) {
        double x = outNorm[0];
        double y = outNorm[1];
        if (FastMath.abs(x * x + y * y - 1) > DEFAULT_UNITY_TOL) {
            throw new IllegalStateException();
        }
    }

    public boolean isIgnoreInvisibleNodesInformation() {
        return ignoreInvisibleNodesInformation;
    }

    public void setIgnoreInvisibleNodesInformation(boolean ignoreInvisibleNodesInformation) {
        this.ignoreInvisibleNodesInformation = ignoreInvisibleNodesInformation;
    }

    @Override
    public void setCenter(double[] center) {
        supportDomainSearcher.setCenter(center);
    }

    @Override
    public void setBoundary(GeomUnit bndOfCenter) {
        supportDomainSearcher.setBoundary(bndOfCenter);
    }

    @Override
    public void setUnitOutNormal(double[] bndOutNormal) {
        supportDomainSearcher.setUnitOutNormal(bndOutNormal);
    }

    @Override
    public void setRadius(double radius) {
        supportDomainSearcher.setRadius(radius);
    }

    @Override
    public double[] getUnitOutNormal() {
        return supportDomainSearcher.getUnitOutNormal();
    }

    @Override
    public GeomUnit getBoundary() {
        return supportDomainSearcher.getBoundary();
    }

    @Override
    public double[] getCenter() {
        return supportDomainSearcher.getCenter();
    }

    @Override
    public double getRadius() {
        return supportDomainSearcher.getRadius();
    }
}
