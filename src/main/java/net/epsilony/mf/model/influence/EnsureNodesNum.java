/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.influence;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.support_domain.SupportDomainData;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class EnsureNodesNum implements InfluenceRadiusCalculator {

    private int id;
    private double initSearchRad;
    private double resultEnlargeRatio = DEFAULT_RESULT_ENLARGE_RATIO;
    private double searchRadiusExpendRatio = DEFAULT_SEARCH_RADIUS_EXPEND_RATIO;
    private double searchRadiusExpendUpperBound = DEFAULT_SEARCH_RADIUS_EXPEND_UPPER_BOUND;
    private int nodesNumLowerBound;
    private boolean onlyCountSpaceNodes;
    private boolean adaptiveInitSearchRad;
    public final static double DEFAULT_RESULT_ENLARGE_RATIO = 1.1;
    public final static double DEFAULT_SEARCH_RADIUS_EXPEND_RATIO = 1 / 0.618;
    public final static double DEFAULT_SEARCH_RADIUS_EXPEND_UPPER_BOUND = 100;
    public final static boolean DEFAULT_ONLY_COUNT_SPACE_NODES = false;
    public final static boolean DEFAULT_ADAPTIVE_INIT_SEARCH_RAD = true;
    private SupportDomainSearcher supportDomainSearcher;

    public void setAdaptiveInitSearchRad(boolean adaptiveInitSearchRad) {
        this.adaptiveInitSearchRad = adaptiveInitSearchRad;
    }

    @Override
    public void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
        this.supportDomainSearcher = supportDomainSearcher;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public double getInitSearchRad() {
        return initSearchRad;
    }

    public double getResultEnlargeRatio() {
        return resultEnlargeRatio;
    }

    public double getSearchRadiusExpendRatio() {
        return searchRadiusExpendRatio;
    }

    public double getSearchRadiusExpendUpperBound() {
        return searchRadiusExpendUpperBound;
    }

    public int getNodesNumLowerBound() {
        return nodesNumLowerBound;
    }

    public boolean isOnlyCountSpaceNodes() {
        return onlyCountSpaceNodes;
    }

    public boolean isAdaptiveInitSearchRad() {
        return adaptiveInitSearchRad;
    }

    public EnsureNodesNum(
            double initSearchRad,
            int nodesNumLowerBound,
            boolean onlyCountSpaceNodes,
            boolean adaptiveInitSearchRad) {
        if (initSearchRad < 0) {
            throw new IllegalArgumentException("initSearchRad should be nonnegtive!");
        }
        this.initSearchRad = initSearchRad;

        if (nodesNumLowerBound < 1) {
            throw new IllegalArgumentException("nodesNumLowerBound should be greater than 0");
        }
        this.nodesNumLowerBound = nodesNumLowerBound;

        this.onlyCountSpaceNodes = onlyCountSpaceNodes;
        this.adaptiveInitSearchRad = adaptiveInitSearchRad;
    }

    public EnsureNodesNum(double initRad, int nodesNumLowerBound) {
        this(initRad, nodesNumLowerBound, DEFAULT_ONLY_COUNT_SPACE_NODES, DEFAULT_ADAPTIVE_INIT_SEARCH_RAD);
    }

    public EnsureNodesNum() {
        this(0, 1);
    }

    public void setInitSearchRad(double initSearchRad) {
        if (initSearchRad <= 0) {
            throw new IllegalArgumentException("initSearchRad should be positive!");
        }
        this.initSearchRad = initSearchRad;
    }

    public void setResultEnlargeRatio(double resultEnlargeRatio) {
        if (resultEnlargeRatio < 1) {
            throw new IllegalArgumentException("resultEnlargeRatio should not be less than 1");
        }
        this.resultEnlargeRatio = resultEnlargeRatio;
    }

    public void setSearchRadiusExpendRatio(double searchRadiusExpendRatio) {
        if (searchRadiusExpendRatio <= 1) {
            throw new IllegalArgumentException("searchRadiusExpendRatio should be greater than 1");
        }
        this.searchRadiusExpendRatio = searchRadiusExpendRatio;
    }

    public void setSearchRadiusExpendUpperBound(double searchRadiusExpendUpperBound) {
        this.searchRadiusExpendUpperBound = searchRadiusExpendUpperBound;
    }

    public void setNodesNumLowerBound(int nodesNumLowerBound) {
        if (nodesNumLowerBound < 1) {
            throw new IllegalArgumentException("nodesNumLowerBound should be greater than 0");
        }
        this.nodesNumLowerBound = nodesNumLowerBound;
    }

    public void setOnlyCountSpaceNodes(boolean onlyCountSpaceNodes) {
        this.onlyCountSpaceNodes = onlyCountSpaceNodes;
    }

    @Override
    public double calcInflucenceRadius(double[] coord, GeomUnit bnd) {
        double searchRad = initSearchRad;
        do {
            SupportDomainData searchResult = supportDomainSearcher.searchSupportDomain(coord, bnd, searchRad);
            if (searchResult.visibleNodes.size() >= nodesNumLowerBound) {
                List<MFNode> cadidateNodes = onlyCountSpaceNodes
                        ? filterNodesOnSegments(searchResult.visibleNodes) : searchResult.visibleNodes;
                if (cadidateNodes.size() >= nodesNumLowerBound) {
                    double result = shortestRadiusWithEnoughNodes(coord, cadidateNodes) * resultEnlargeRatio;
                    if (adaptiveInitSearchRad) {
                        initSearchRad = result;
                    }
                    return result;
                }
            }
            searchRad *= searchRadiusExpendRatio;
        } while (searchRad / initSearchRad < searchRadiusExpendUpperBound);
        throw new IllegalStateException("Can find a suitable radius!");
    }

    private List<MFNode> filterNodesOnSegments(List<MFNode> nodes) {
        Iterator<MFNode> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            MFNode node = iterator.next();
            if (node.getParent() != null && node.getParent() instanceof Segment) {
                iterator.remove();
            }
        }
        return nodes;
    }
    private final DistanceComparator distanceComparator = new DistanceComparator();

    private double shortestRadiusWithEnoughNodes(double[] center, List<MFNode> cadidateNodes) {
        distanceComparator.setCenter(center);
        Collections.sort(cadidateNodes, distanceComparator);
        MFNode nd = cadidateNodes.get(nodesNumLowerBound - 1);
        return Math2D.distance(center, nd.getCoord());
    }

    private static class DistanceComparator implements Comparator<MFNode> {

        double[] center;

        @Override
        public int compare(MFNode node1, MFNode node2) {
            double dis1 = Math2D.distance(center, node1.getCoord());
            double dis2 = Math2D.distance(center, node2.getCoord());
            if (dis1 < dis2) {
                return -1;
            } else if (dis1 > dis2) {
                return 1;
            } else {
                return 0;
            }
        }

        public void setCenter(double[] center) {
            this.center = center;
        }
    }

    @Override
    public String toString() {
        return "EnsureNodesNum{" + "initSearchRad=" + initSearchRad + ", resultEnlargeRatio=" + resultEnlargeRatio + ", searchRadiusExpendRatio=" + searchRadiusExpendRatio + ", searchRadiusExpendUpperBound=" + searchRadiusExpendUpperBound + ", nodesNumLowerBound=" + nodesNumLowerBound + ", onlyCountSpaceNodes=" + onlyCountSpaceNodes + ", adaptiveInitSearchRad=" + adaptiveInitSearchRad + '}';
    }
}
