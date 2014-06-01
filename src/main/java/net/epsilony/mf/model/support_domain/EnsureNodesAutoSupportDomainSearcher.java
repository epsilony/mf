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
import java.util.Comparator;
import java.util.List;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFGeomUnit;
import net.epsilony.mf.util.ArrayListCache;
import net.epsilony.tb.analysis.Math2D;

import com.google.common.collect.Ordering;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class EnsureNodesAutoSupportDomainSearcher implements AutoSupportDomainSearcher {

    public final static double DEFAULT_RESULT_ENLARGE_RATIO = 1.1;
    public final static double DEFAULT_SEARCH_RADIUS_EXPEND_RATIO = 1 / 0.618;
    public final static double DEFAULT_SEARCH_RADIUS_UPPER_BOUND = 10000;

    private NodesNumRadiusEstimator radiusEstimator;
    private SupportDomainSearcher supportDomainSearcher;
    private int nodesNumLowerBound;

    private double resultEnlargeRatio = DEFAULT_RESULT_ENLARGE_RATIO;
    private double searchRadiusExpendRatio = DEFAULT_SEARCH_RADIUS_EXPEND_RATIO;
    private double searchRadiusUpperBound = DEFAULT_SEARCH_RADIUS_UPPER_BOUND;
    private double[] center;
    private MFGeomUnit bndOfCenter;
    private double[] bndOutNormal;

    public EnsureNodesAutoSupportDomainSearcher(NodesNumRadiusEstimator radiusEstimator,
            SupportDomainSearcher supportDomainSearcher, int nodesNumLowerBound) {
        this.radiusEstimator = radiusEstimator;
        this.supportDomainSearcher = supportDomainSearcher;
        setNodesNumLowerBound(nodesNumLowerBound);
    }

    public EnsureNodesAutoSupportDomainSearcher() {
    }

    public double radius() {
        return resultRadius;
    }

    @Override
    public void search(SupportDomainData outputData) {
        radiusEstimator.setCenter(center);

        supportDomainSearcher.setCenter(center);
        supportDomainSearcher.setBoundary(bndOfCenter);
        supportDomainSearcher.setUnitOutNormal(bndOutNormal);

        double radius = radiusEstimator.estimate();

        if (radius <= 0) {
            throw new IllegalStateException("init radius is not positive :" + radius);
        }
        resultRadius = -1;
        do {
            supportDomainSearcher.setRadius(radius);
            supportDomainSearcher.search(outputData);
            List<MFNode> visibleNodesContainer = outputData.getVisibleNodesContainer();
            if (visibleNodesContainer.size() >= nodesNumLowerBound) {
                resultRadius = calcShortestRadiusWithEnoughNodes(visibleNodesContainer) * resultEnlargeRatio;
                break;
            }
            radius *= searchRadiusExpendRatio;
        } while (radius <= searchRadiusUpperBound);

        if (resultRadius <= 0) {
            throw new IllegalStateException("cannot find enough " + nodesNumLowerBound
                    + " nodes at upper radius search bound " + searchRadiusUpperBound);
        }

        if (resultRadius == radius) {
            return;
        } else if (resultRadius < radius) {
            filterSortedByRadius(resultRadius, outputData.getVisibleNodesContainer());
        } else {
            supportDomainSearcher.setRadius(resultRadius);
            supportDomainSearcher.search(outputData);
        }

        radiusEstimator.feedBack(resultRadius, outputData.getVisibleNodesContainer().size());
    }

    private final Ordering<MFNode> ordering = Ordering.from(new DistanceComparator());

    private double calcShortestRadiusWithEnoughNodes(List<MFNode> cadidateNodes) {
        List<MFNode> leastOf = ordering.leastOf(cadidateNodes, nodesNumLowerBound);
        return Math2D.distance(center, leastOf.get(leastOf.size() - 1).getCoord());
    }

    private ArrayListCache<MFNode> arrayListCache = new ArrayListCache<>();
    private double resultRadius;

    private void filterSortedByRadius(double radius, List<MFNode> sortedNodes) {
        ArrayList<MFNode> tempNodes = arrayListCache.get(sortedNodes.size());
        tempNodes.clear();
        double d = radius * radius;
        for (MFNode node : sortedNodes) {
            if (Math2D.distanceSquare(node.getCoord(), center) <= d) {
                tempNodes.add(node);
            }
        }
        sortedNodes.clear();
        sortedNodes.addAll(tempNodes);
    }

    private class DistanceComparator implements Comparator<MFNode> {

        @Override
        public int compare(MFNode node1, MFNode node2) {
            double dis1 = Math2D.distanceSquare(center, node1.getCoord());
            double dis2 = Math2D.distanceSquare(center, node2.getCoord());
            if (dis1 < dis2) {
                return -1;
            } else if (dis1 > dis2) {
                return 1;
            } else {
                return 0;
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

    public void setRadiusEstimator(NodesNumRadiusEstimator radiusEstimator) {
        this.radiusEstimator = radiusEstimator;
    }

    public void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
        this.supportDomainSearcher = supportDomainSearcher;
    }

    public int getNodesNumLowerBound() {
        return nodesNumLowerBound;
    }

    public void setNodesNumLowerBound(int nodesNumLowerBound) {
        if (nodesNumLowerBound < 1) {
            throw new IllegalArgumentException("nodes num lower bound should >= 1, not " + nodesNumLowerBound);
        }
        this.nodesNumLowerBound = nodesNumLowerBound;
    }

    public double getResultEnlargeRatio() {
        return resultEnlargeRatio;
    }

    public void setResultEnlargeRatio(double resultEnlargeRatio) {
        if (resultEnlargeRatio < 1) {
            throw new IllegalArgumentException("result enlarge ratio should be >= 1, not " + resultEnlargeRatio);
        }
        this.resultEnlargeRatio = resultEnlargeRatio;
    }

    public double getSearchRadiusExpendRatio() {
        return searchRadiusExpendRatio;
    }

    public void setSearchRadiusExpendRatio(double searchRadiusExpendRatio) {
        if (searchRadiusExpendRatio <= 1) {
            throw new IllegalArgumentException("search radius expending ratio should be > 1, not "
                    + searchRadiusExpendRatio);
        }
        this.searchRadiusExpendRatio = searchRadiusExpendRatio;
    }

    public double getSearchRadiusUpperBound() {
        return searchRadiusUpperBound;
    }

    public void setSearchRadiusExpendUpperBound(double searchRadiusUpperBound) {
        if (searchRadiusUpperBound <= 0) {
            throw new IllegalArgumentException("search radius expend upper bound should be > 0, not "
                    + searchRadiusUpperBound);
        }
        this.searchRadiusUpperBound = searchRadiusUpperBound;
    }

}
