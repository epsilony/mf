/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.mf.model.load.MFLoad;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
    boolean genSubdomains = true;
    RectanglePhM rectangleModel;
    RawAnalysisModel analysisModel;

    @Override
    public RawAnalysisModel produce() {
        initFacetModel();
        genFractionizeFacetAndLoads();
        genSpaceNodes();
        genSubdomains();
        return analysisModel;
    }

    private void initFacetModel() {
        analysisModel = new RawAnalysisModel();
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

    private void genSubdomains() {
        if (!genSubdomains) {
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

        QuadrangleSubdomain[][] quads = new QuadrangleSubdomain[verticalFractionNum][horizontalFractionNum];

        ArrayList<Segment> facetSegs = new ArrayList((horizontalFractionNum + verticalFractionNum) * 2);

        FacetModel factionizedModel = (FacetModel) analysisModel.getFractionizedModel();
        for (Segment seg : factionizedModel.getFacet()) {
            facetSegs.add(seg);
        }

        for (int row = 0; row < verticalFractionNum; row++) {
            for (int col = 0; col < horizontalFractionNum; col++) {
                QuadrangleSubdomain quad = new QuadrangleSubdomain();
                quad.setVertex(0, coords[row][col]);
                quad.setVertex(1, coords[row][col + 1]);
                quad.setVertex(2, coords[row + 1][col + 1]);
                quad.setVertex(3, coords[row + 1][col]);

                Segment downSeg = row == 0 ? facetSegs.get(col) : null;
                Segment rightSeg = col == horizontalFractionNum - 1 ? facetSegs.get(horizontalFractionNum + row) : null;
                Segment upSeg = row == horizontalFractionNum - 1 ? facetSegs.get(2 * horizontalFractionNum + verticalFractionNum - col - 1) : null;
                Segment leftSeg = col == 0 ? facetSegs.get(2 * (horizontalFractionNum + verticalFractionNum) - row - 1) : null;

                quad.setSegment(0, downSeg);
                quad.setSegment(1, rightSeg);
                quad.setSegment(2, upSeg);
                quad.setSegment(3, leftSeg);

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
        return genSubdomains;
    }

    public void setGenSubdomains(boolean genSubdomains) {
        this.genSubdomains = genSubdomains;
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
}
