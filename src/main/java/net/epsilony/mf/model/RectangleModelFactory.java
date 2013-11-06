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

package net.epsilony.mf.model;

import net.epsilony.mf.model.subdomain.SubLineDomain;
import net.epsilony.mf.model.subdomain.MFSubdomain;
import net.epsilony.mf.model.load.MFLoad;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.model.search.LRTreeSegmentChordIntersectingSphereSearcher;
import net.epsilony.mf.model.search.SphereSearcher;
import net.epsilony.mf.model.subdomain.PolygonSubdomain;
import net.epsilony.mf.model.subdomain.GeomUnitSubdomain;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.tb.Factory;
import net.epsilony.tb.RudeFactory;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.solid.SegmentIterable;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleModelFactory implements Factory<RawAnalysisModel> {

    private static final int DIMENSION = 2;
    double fractionSizeCap;
    boolean genSpaceNodes = true;
    boolean genSubdomains2D = true;
    RectanglePhM rectangleModel;
    RawAnalysisModel analysisModel;
    double edgeNodesDisturbRatio = 0;
    double spaceNodesDisturbRatio = 0;
    boolean oneToOneSegmentSubdomain = false;
    Random disturbRand;
    private double searchRadius;
    private SphereSearcher<Segment> fractionizedSegmentsSearcher;

    @Override
    public RawAnalysisModel produce() {
        initAnalysisModel();
        genFractionizeFacetAndLoads();
        genSpaceNodes();
        disturbEdgeNodes();
        disturbSpaceNodes();
        initFractionizedFacetSearcher();
        genSubdomains1D();
        genSubdomains2D();
        return analysisModel;
    }

    private void initAnalysisModel() {
        analysisModel = new RawAnalysisModel();
        analysisModel.setPhysicalModel(rectangleModel);
        FacetModel facetModel = new FacetModel();
        facetModel.setDimension(DIMENSION);
        facetModel.setLoadMap(new HashMap());
        analysisModel.setFractionizedModel(facetModel);
    }

    private void genFractionizeFacetAndLoads() {
        Facet rect = (Facet) rectangleModel.getGeomRoot();
        Map<GeomUnit, MFLoad> rectLoadMap = rectangleModel.getLoadMap();
        Facet facet = GeomModel2DUtils.clonePolygonWithMFNode(rect);
        FacetModel factionizedModel = (FacetModel) analysisModel.getFractionizedModel();
        factionizedModel.setFacet(facet);
        Iterator<Segment> iterator = facet.iterator();
        int i = 0;
        int horizontalFractionNum = getHorizontalFractionNum();
        int verticalFractionNum = getVerticalFractionNum();
        for (Segment rectSeg : rect) {
            MFLoad load = rectLoadMap.get(rectSeg);
            Line toFraction = (Line) iterator.next();
            Segment formerSucc = toFraction.getSucc();
            toFraction.fractionize(i % 2 == 0 ? horizontalFractionNum : verticalFractionNum, new RudeFactory<>(MFNode.class));
            for (Segment seg : new SegmentIterable<Segment>(toFraction)) {
                if (seg == formerSucc) {
                    break;
                }
                if (load != null) {
                    analysisModel.getFractionizedModel().getLoadMap().put(seg, load);
                }
            }
            i++;
        }

        factionizedModel.setVolumeLoad(rectangleModel.getVolumeLoad());
    }

    private void genSpaceNodes() {
        if (!genSpaceNodes) {
            return;
        }
        int horizontalFractionNum = getHorizontalFractionNum();
        int verticalFractionNum = getVerticalFractionNum();
        double deltaY = rectangleModel.getHeight() / verticalFractionNum;
        double deltaX = rectangleModel.getWidth() / horizontalFractionNum;
        double left = rectangleModel.getEdgePosition(MFRectangleEdge.LEFT);
        double down = rectangleModel.getEdgePosition(MFRectangleEdge.DOWN);
        ArrayList<MFNode> spaceNodes = new ArrayList<>((horizontalFractionNum - 1) * (verticalFractionNum - 1));
        for (int row = 1; row < verticalFractionNum; row++) {
            double y = down + row * deltaY;
            for (int col = 1; col < horizontalFractionNum; col++) {
                double x = left + col * deltaX;
                MFNode node = new MFNode(new double[]{x, y});
                spaceNodes.add(node);
            }
        }
        analysisModel.setSpaceNodes(spaceNodes);
    }

    private void disturbEdgeNodes() {
        if (edgeNodesDisturbRatio == 0) {
            return;
        }
        Facet facet = (Facet) analysisModel.getFractionizedModel().getGeomRoot();
        LinkedList<double[]> newCoords = new LinkedList<>();
        for (Segment seg : facet) {
            double[] startCoord = seg.getStart().getCoord();
            double x = startCoord[0];
            double y = startCoord[1];
            boolean xDisturb = x != rectangleModel.getEdgePosition(MFRectangleEdge.LEFT) && x != rectangleModel.getEdgePosition(MFRectangleEdge.RIGHT);
            boolean yDisturb = y != rectangleModel.getEdgePosition(MFRectangleEdge.DOWN) && y != rectangleModel.getEdgePosition(MFRectangleEdge.UP);
            if (xDisturb && yDisturb) {
                throw new IllegalStateException("rectangle facet has been modified unproperly, cannot disturb the nodes");
            }
            double[] newCoord;
            if (xDisturb) {
                newCoord = new double[]{startCoord[0] + genRandEdgeDisturb(seg, 0), startCoord[1]};
            } else if (yDisturb) {
                newCoord = new double[]{startCoord[0], startCoord[1] += genRandEdgeDisturb(seg, 1)};
            } else {
                newCoord = startCoord;
            }
            newCoords.add(newCoord);
        }
        Iterator<double[]> iterator = newCoords.iterator();
        for (Segment seg : facet) {
            seg.getStart().setCoord(iterator.next());
        }
    }

    private double genRandEdgeDisturb(Segment seg, int index) {
        double rd = genRandDouble();
        double range1 = (seg.getPred().getStart().getCoord()[index] + seg.getStart().getCoord()[index]) / 2;
        double range2 = (seg.getStart().getCoord()[index] + seg.getEnd().getCoord()[index]) / 2;
        return range1 * (1 - rd) + range2 * rd;
    }

    private double genRandDouble() {
        if (null == disturbRand) {
            disturbRand = new Random();
        }
        double rd = disturbRand.nextDouble();
        if (rd == 0) {// avoid very rare two neighbor nodes coincding
            rd = 0.5;
        }
        return rd;
    }

    private void disturbSpaceNodes() {
        if (spaceNodesDisturbRatio == 0) {
            return;
        }
        int horizontalFractionNum = getHorizontalFractionNum();
        int verticalFractionNum = getVerticalFractionNum();
        double deltaY = rectangleModel.getHeight() / verticalFractionNum;
        double deltaX = rectangleModel.getWidth() / horizontalFractionNum;
        double[] deltas = new double[]{deltaX, deltaY};

        for (MFNode node : analysisModel.getSpaceNodes()) {
            double[] coord = node.getCoord();
            for (int i = 0; i < DIMENSION; i++) {
                coord[i] += (genRandDouble() - 0.5) * deltas[i];
            }
        }
    }

    private void genSubdomains1D() {
        if (oneToOneSegmentSubdomain) {
            genOneToOneSegmentSubdomains();
        } else {
            genRegularSegmentSubdomains();
        }
    }

    private void genOneToOneSegmentSubdomains() {
        FacetModel facetModel = (FacetModel) analysisModel.getFractionizedModel();
        Map<GeomUnit, MFLoad> loadMap = facetModel.getLoadMap();
        LinkedList<GeomUnitSubdomain> dirichletSubdomains = new LinkedList<>();
        LinkedList<GeomUnitSubdomain> neumannSubdomains = new LinkedList<>();
        for (Map.Entry<GeomUnit, MFLoad> entry : loadMap.entrySet()) {
            GeomUnit key = entry.getKey();
            if (!(key instanceof Segment)) {
                continue;
            }
            Segment seg = (Segment) key;
            GeomUnitSubdomain segSubdomain = new GeomUnitSubdomain();
            segSubdomain.setGeomUnit(seg);

            SegmentLoad segLoad = (SegmentLoad) entry.getValue();
            if (segLoad.isDirichlet()) {
                dirichletSubdomains.add(segSubdomain);
            } else {
                neumannSubdomains.add(segSubdomain);
            }
        }
        analysisModel.setSubdomains(MFProcessType.DIRICHLET, (List) dirichletSubdomains);
        analysisModel.setSubdomains(MFProcessType.NEUMANN, (List) neumannSubdomains);
    }

    private void initFractionizedFacetSearcher() {
        FacetModel facetModel = (FacetModel) analysisModel.getFractionizedModel();
        List<Segment> segments = facetModel.getFacet().getSegments();

        double minSegChordLen = Double.POSITIVE_INFINITY;
        for (Segment seg : segments) {
            double chordLength = Segment2DUtils.chordLength(seg);
            if (chordLength < minSegChordLen) {
                minSegChordLen = chordLength;
            }
        }
        searchRadius = minSegChordLen / 10;
        fractionizedSegmentsSearcher = new LRTreeSegmentChordIntersectingSphereSearcher();
        fractionizedSegmentsSearcher.setAll(segments);
    }

    private void genRegularSegmentSubdomains() {
        if (!checkWhetherOneRectangleEdgeHasOnlyOneLoad()) {
            throw new UnsupportedOperationException("only support situations that one rectangle edge has only one load");
        }

        Map<GeomUnit, MFLoad> loadMap = analysisModel.getFractionizedModel().getLoadMap();
        LinkedList<SubLineDomain> dirichletSubdomains = new LinkedList<>();
        LinkedList<SubLineDomain> neumannSubdomains = new LinkedList<>();

        int horizontalFractionNum = getHorizontalFractionNum();
        int verticalFractionNum = getVerticalFractionNum();
        double deltaY = rectangleModel.getHeight() / verticalFractionNum;
        double deltaX = rectangleModel.getWidth() / horizontalFractionNum;

        double[][] rectangleVertexCoords = rectangleModel.getVertexCoords();
        double[][] deltaXYs = new double[][]{{deltaX, 0}, {0, deltaY}, {-deltaX, 0}, {0, -deltaY}};
        double[] parameterResult = new double[2];
        for (int i = 0; i < 4; i++) {
            double[] edgeStart = rectangleVertexCoords[i];
            double[] edgeEnd = rectangleVertexCoords[(i + 1) % 4];
            double[] deltaXY = deltaXYs[i];
            int subdomainNum = i % 2 == 0 ? horizontalFractionNum : verticalFractionNum;
            for (int j = 0; j < subdomainNum; j++) {
                double[] start = Math2D.adds(edgeStart, 1, deltaXY, j, null);
                double[] end = j + 1 < subdomainNum ? Math2D.adds(start, deltaXY, null) : edgeEnd;

                Line[] startEndLines = searchLinesAndParameter(start, end, parameterResult);
                if (null == startEndLines) {
                    throw new IllegalStateException();
                }
                MFLoad load = loadMap.get(startEndLines[0]);
                if (null == load) {
                    continue;
                }

                SubLineDomain subLineDomain = new SubLineDomain();
                subLineDomain.setStartSegment(startEndLines[0]);
                subLineDomain.setStartParameter(parameterResult[0]);
                subLineDomain.setEndSegment(startEndLines[1]);
                subLineDomain.setEndParameter(parameterResult[1]);

                SegmentLoad segLoad = (SegmentLoad) load;
                if (segLoad.isDirichlet()) {
                    dirichletSubdomains.add(subLineDomain);
                } else {
                    neumannSubdomains.add(subLineDomain);
                }
            }
        }
        analysisModel.setSubdomains(MFProcessType.DIRICHLET, (List) dirichletSubdomains);
        analysisModel.setSubdomains(MFProcessType.NEUMANN, (List) neumannSubdomains);
    }

    private boolean checkWhetherOneRectangleEdgeHasOnlyOneLoad() {
        Facet facet = (Facet) analysisModel.getFractionizedModel().getGeomRoot();
        MFLoad[] edgeLoads = new MFLoad[4];
        boolean[] edgeLoadsSetted = new boolean[4];
        Map<GeomUnit, MFLoad> loadMap = analysisModel.getFractionizedModel().getLoadMap();
        for (Segment seg : facet) {
            MFRectangleEdge edge = rectangleModel.getEdge((Line) seg);
            if (null == edge) {
                throw new IllegalStateException();
            }
            MFLoad load = loadMap.get(seg);
            if (edgeLoadsSetted[edge.ordinal()]) {
                if (edgeLoads[edge.ordinal()] != load) {
                    return false;
                }
            } else {
                edgeLoads[edge.ordinal()] = load;
                edgeLoadsSetted[edge.ordinal()] = true;
            }
        }
        return true;
    }

    private Line[] searchLinesAndParameter(double[] start, double[] end, double[] parameterResults) {
        double length = Math2D.distance(start, end);
        if (length == 0) {
            throw new IllegalArgumentException();
        }

        List<Segment> inStartSphere = fractionizedSegmentsSearcher.searchInSphere(start, searchRadius);
        if (inStartSphere == null || inStartSphere.isEmpty()) {
            return null;
        }
        List<Segment> inEndSphere = fractionizedSegmentsSearcher.searchInSphere(end, searchRadius);
        if (inEndSphere == null || inEndSphere.isEmpty()) {
            return null;
        }
//        final double DISTANCE_ERROR = MFConstants.DEFAULT_DISTANCE_ERROR; // not useful for retangle 
        final double DIRECT_ERROR = 1 - 0.001;
        double[] vec = Math2D.subs(end, start, null);
        Math2D.normalize(vec, vec);

        Line[] result = new Line[2];

        for (Segment seg : inStartSphere) {
            double[] segVec = Segment2DUtils.chordVector(seg, null);
            Math2D.normalize(segVec, segVec);
            double inner = Math2D.dot(segVec, vec);
            if (inner < DIRECT_ERROR) {
                continue;
            }

            double[] segStart = seg.getStart().getCoord();
            double startToSegStartSq = Math2D.distanceSquare(segStart, start);

            if (startToSegStartSq == 0) {
                parameterResults[0] = 0;
                result[0] = (Line) seg;
                break;
            }
            double[] segEnd = seg.getEnd().getCoord();
            if (Math2D.distanceSquare(segEnd, start) == 0) {
                parameterResults[0] = 0;
                result[0] = (Line) seg.getSucc();
                break;
            }

            double parameter = FastMath.sqrt(startToSegStartSq) / Math2D.distance(segStart, segEnd);
            if (parameter >= 0 && parameter < 1) {
                parameterResults[0] = parameter;
                result[0] = (Line) seg;
                break;
            }
        }

        for (Segment seg : inEndSphere) {
            double[] segVec = Segment2DUtils.chordVector(seg, null);
            Math2D.normalize(segVec, segVec);
            double inner = Math2D.dot(segVec, vec);
            if (inner < DIRECT_ERROR) {
                continue;
            }

            double[] segStart = seg.getStart().getCoord();
            double endToSegStartSq = Math2D.distanceSquare(segStart, end);

            if (endToSegStartSq == 0) {
                parameterResults[1] = 1;
                result[1] = (Line) seg.getPred();
                break;
            }
            double[] segEnd = seg.getEnd().getCoord();
            if (Math2D.distanceSquare(segEnd, end) == 0) {
                parameterResults[1] = 1;
                result[1] = (Line) seg;
                break;
            }

            double parameter = FastMath.sqrt(endToSegStartSq) / Math2D.distance(segStart, segEnd);
            if (parameter > 0 && parameter <= 1) {
                parameterResults[1] = parameter;
                result[1] = (Line) seg;
                break;
            }
        }
        return result;
    }

    private void genSubdomains2D() {
        if (!genSubdomains2D) {
            return;
        }
        int horizontalFractionNum = getHorizontalFractionNum();
        int verticalFractionNum = getVerticalFractionNum();
        double deltaY = rectangleModel.getHeight() / verticalFractionNum;
        double deltaX = rectangleModel.getWidth() / horizontalFractionNum;
        double left = rectangleModel.getEdgePosition(MFRectangleEdge.LEFT);
        double down = rectangleModel.getEdgePosition(MFRectangleEdge.DOWN);
        double up = rectangleModel.getEdgePosition(MFRectangleEdge.UP);
        double right = rectangleModel.getEdgePosition(MFRectangleEdge.RIGHT);

        double[][][] coords = new double[verticalFractionNum + 1][horizontalFractionNum + 1][];
        for (int row = 0; row < verticalFractionNum + 1; row++) {
            double y = down + deltaY * row;
            if (row == 0) {
                y = down;
            } else if (row == verticalFractionNum) {
                y = up;
            }
            for (int col = 0; col < horizontalFractionNum + 1; col++) {
                double x = left + deltaX * col;
                if (col == 0) {
                    x = left;
                } else if (col == horizontalFractionNum) {
                    x = right;
                }
                coords[row][col] = new double[]{x, y};
            }
        }

        ArrayList<MFSubdomain> subdomains = new ArrayList<>(verticalFractionNum * horizontalFractionNum);
        double[] parameterResult = new double[2];
        for (int row = 0; row < verticalFractionNum; row++) {
            for (int col = 0; col < horizontalFractionNum; col++) {
                PolygonSubdomain quad = new PolygonSubdomain(4);
                quad.setVertexCoord(0, coords[row][col]);
                quad.setVertexCoord(1, coords[row][col + 1]);
                quad.setVertexCoord(2, coords[row + 1][col + 1]);
                quad.setVertexCoord(3, coords[row + 1][col]);

                for (int vertexId = 0; vertexId < 4; vertexId++) {
                    double[] start = quad.getVertexCoord(vertexId);
                    double[] end = quad.getVertexCoord((vertexId + 1) % 4);
                    Segment[] segs = searchLinesAndParameter(start, end, parameterResult);
                    if (null == segs) {
                        continue;
                    }
                    quad.setVertexLine(vertexId, (Line) segs[0]);
                    quad.setVertexLineParameter(vertexId, parameterResult[0]);
                }
                subdomains.add(quad);
            }
        }

        analysisModel.setSubdomains(MFProcessType.VOLUME, subdomains);
    }

    public boolean isGenSpaceNodes() {
        return genSpaceNodes;
    }

    public void setGenSpaceNodes(boolean genSpaceNodes) {
        this.genSpaceNodes = genSpaceNodes;
    }

    public boolean isGenSubdomains() {
        return genSubdomains2D;
    }

    public void setGenSubdomains2D(boolean genSubdomains2D) {
        this.genSubdomains2D = genSubdomains2D;
    }

    public double getFractionSizeCap() {
        return fractionSizeCap;
    }

    public void setFractionSizeCap(double fractionSizeCap) {
        this.fractionSizeCap = fractionSizeCap;
    }

    public void setRectangleModel(RectanglePhM rectangleModel) {
        this.rectangleModel = rectangleModel;
    }

    public int getVerticalFractionNum() {
        if (fractionSizeCap <= 0) {
            throw new IllegalStateException("fractionSizeCap value illegal " + fractionSizeCap);
        }
        return (int) Math.ceil(rectangleModel.getHeight() / fractionSizeCap);
    }

    public int getHorizontalFractionNum() {
        if (fractionSizeCap <= 0) {
            throw new IllegalStateException("fractionSizeCap value illegal " + fractionSizeCap);
        }
        return (int) Math.ceil(rectangleModel.getWidth() / fractionSizeCap);
    }

    public double getEdgeNodesDisturbRatio() {
        return edgeNodesDisturbRatio;
    }

    public void setEdgeNodesDisturbRatio(double edgeNodesDisturbRatio) {
        if (edgeNodesDisturbRatio < 0 || edgeNodesDisturbRatio >= 1) {
            throw new IllegalArgumentException();
        }
        this.edgeNodesDisturbRatio = edgeNodesDisturbRatio;
    }

    public double getSpaceNodesDisturbRatio() {
        return spaceNodesDisturbRatio;
    }

    public void setSpaceNodesDisturbRatio(double spaceNodesDisturbRatio) {
        if (spaceNodesDisturbRatio < 0 || spaceNodesDisturbRatio >= 1) {
            throw new IllegalArgumentException();
        }
        this.spaceNodesDisturbRatio = spaceNodesDisturbRatio;
    }

    public boolean isOneToOneSegmentSubdomain() {
        return oneToOneSegmentSubdomain;
    }

    public void setOneToOneSegmentSubdomain(boolean oneToOneSegmentSubdomain) {
        this.oneToOneSegmentSubdomain = oneToOneSegmentSubdomain;
    }

    public Random getDisturbRand() {
        return disturbRand;
    }

    public void setDisturbRand(Random disturbRand) {
        this.disturbRand = disturbRand;
    }
}
