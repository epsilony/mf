/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.mf.model.subdomain.SegmentSubdomain;
import net.epsilony.mf.model.subdomain.MFSubdomain;
import net.epsilony.mf.model.load.MFLoad;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.epsilony.mf.model.subdomain.PolygonSubdomain;
import net.epsilony.tb.Factory;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.RudeFactory;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.SegmentIterable;

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
    boolean oneToOneSegmentSubdomain = true;
    Random disturbRand;

    @Override
    public RawAnalysisModel produce() {
        initAnalysisModel();
        genFractionizeFacetAndLoads();
        genSpaceNodes();
        disturbEdgeNodes();
        disturbSpaceNodes();
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
            double[] newCoord = null;;
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
        LinkedList<SegmentSubdomain> segSubdomains = new LinkedList<>();
        for (Map.Entry<GeomUnit, MFLoad> entry : loadMap.entrySet()) {
            GeomUnit key = entry.getKey();
            if (!(key instanceof Segment)) {
                continue;
            }
            Segment seg = (Segment) key;
            SegmentSubdomain segSubdomain = new SegmentSubdomain();
            segSubdomain.setStartParameter(0);
            segSubdomain.setEndParameter(1);
            segSubdomain.setStartSegment(seg);
            segSubdomains.add(segSubdomain);
        }
        analysisModel.setSubdomains(1, (List) segSubdomains);
    }

    private void genRegularSegmentSubdomains() {
        FacetModel facetModel = (FacetModel) analysisModel.getFractionizedModel();
        Map<GeomUnit, MFLoad> loadMap = facetModel.getLoadMap();
        LinkedList<SegmentSubdomain> segSubdomains = new LinkedList<>();
        int horizontalFractionNum = getHorizontalFractionNum();
        int verticalFractionNum = getVerticalFractionNum();
        double deltaY = rectangleModel.getHeight() / verticalFractionNum;
        double deltaX = rectangleModel.getWidth() / horizontalFractionNum;
        throw new UnsupportedOperationException();
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

        PolygonSubdomain[][] quads = new PolygonSubdomain[verticalFractionNum][horizontalFractionNum];

        ArrayList<Segment> facetSegs = new ArrayList((horizontalFractionNum + verticalFractionNum) * 2);

        FacetModel factionizedModel = (FacetModel) analysisModel.getFractionizedModel();
        for (Segment seg : factionizedModel.getFacet()) {
            facetSegs.add(seg);
        }

        for (int row = 0; row < verticalFractionNum; row++) {
            for (int col = 0; col < horizontalFractionNum; col++) {
                PolygonSubdomain quad = new PolygonSubdomain(4);
                quad.setVertexCoord(0, coords[row][col]);
                quad.setVertexCoord(1, coords[row][col + 1]);
                quad.setVertexCoord(2, coords[row + 1][col + 1]);
                quad.setVertexCoord(3, coords[row + 1][col]);

                Segment downSeg = row == 0 ? facetSegs.get(col) : null;
                Segment rightSeg = col == horizontalFractionNum - 1 ? facetSegs.get(horizontalFractionNum + row) : null;
                Segment upSeg = row == horizontalFractionNum - 1 ? facetSegs.get(2 * horizontalFractionNum + verticalFractionNum - col - 1) : null;
                Segment leftSeg = col == 0 ? facetSegs.get(2 * (horizontalFractionNum + verticalFractionNum) - row - 1) : null;

                quad.setVertexLine(0, (Line) downSeg);
                quad.setVertexLine(1, (Line) rightSeg);
                quad.setVertexLine(2, (Line) upSeg);
                quad.setVertexLine(3, (Line) leftSeg);

                quads[row][col] = quad;
            }
        }
        ArrayList<MFSubdomain> subdomains = new ArrayList<>(verticalFractionNum * horizontalFractionNum);
        MiscellaneousUtils.addToList(quads, subdomains);
        analysisModel.setSubdomains(DIMENSION, subdomains);
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
