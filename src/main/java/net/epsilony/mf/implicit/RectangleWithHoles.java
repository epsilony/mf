/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit;

import net.epsilony.tb.implicit.CircleLevelSet;
import net.epsilony.tb.implicit.TriangleContourCell;
import net.epsilony.tb.implicit.TriangleContourCellFactory;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFLineBnd;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.Line;
import net.epsilony.mf.geomodel.FacetModel;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.SegmentChainsIterator;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.process.integrate.point.SimpMFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.SimpMFIntegratePoint;
import net.epsilony.tb.IntIdentityMap;
import net.epsilony.tb.NeedPreparation;
import net.epsilony.tb.analysis.DifferentiableFunction;
import net.epsilony.tb.analysis.DifferentiableFunctionUtils;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.quadrature.SymmetricTriangleQuadrature;
import net.epsilony.tb.ui.UIUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleWithHoles implements NeedPreparation {

    private int id;
    public static double DEFAULT_MODEL_NODES_EXTENTION = 20;
    public static double DEFAULT_QUADRATURE_DOMAIN_SIZE = 10;
    public static double DEFAULT_SEGMENT_SIZE = 10;
    public static int DEFAULT_QUADRATURE_POWER = 3;
    Rectangle2D rectangle;
    Facet rectanglePolygon;
    double holeRadius, holeDistance;
    int numOfHoleRows;
    int numOfHoleCols;
    List<CircleLevelSet> holes;
    double spacesNodesExtension = DEFAULT_MODEL_NODES_EXTENTION;
    double triangleSize = DEFAULT_QUADRATURE_DOMAIN_SIZE;
    double segmentSize = DEFAULT_SEGMENT_SIZE;
    List<TriangleContourCell> triangles;
    List<MFNode> spaceNodes;
    List<QuadraturePoint> volumeQuadraturePoints;
    List<Segment2DQuadraturePoint> boundaryQuadraturePoints;
    List<Segment> chainsHeads;
    int quadraturePower = DEFAULT_QUADRATURE_POWER;
    DifferentiableFunction levelSetFunction;

    public RectangleWithHoles(Rectangle2D rectangle, double holeRadius, double holeDistance) {
        this.rectangle = UIUtils.tidyRectangle2D(rectangle, null);
        this.holeRadius = holeRadius;
        this.holeDistance = holeDistance;
        double rectSize = Math.min(rectangle.getHeight(), rectangle.getWidth());
        if (rectSize < (holeRadius + holeDistance / 2) * 2) {
            throw new IllegalArgumentException("hole radius or hold distance is too large ("
                    + rectangle + ", holeRadius" + holeRadius + ", holeDistance)");
        }
        genHoles();
        genRectanglePolygon();
        genLevelSetFunction();
    }

    public DifferentiableFunction getLevelSetFunction() {
        return levelSetFunction;
    }

    public void setSegmentSize(double segmentSize) {
        this.segmentSize = segmentSize;
    }

    public void setQuadraturePower(int quadraturePower) {
        this.quadraturePower = quadraturePower;
    }

    public void setSpaceNodesExtension(double spaceNodesExtension) {
        this.spacesNodesExtension = spaceNodesExtension;
    }

    public void setTriangleSize(double triangleSize) {
        if (triangleSize <= 0) {
            throw new IllegalArgumentException("quadrature domain size should be positive");
        }
        this.triangleSize = triangleSize;
    }

    @Override
    public void prepare() {
        genTriangleContourCells();
        genSpaceNodes();
        genVolumeQuadraturePoints();
        genBoundaryQuadraturePoints();
    }

    public FacetModel getModel() {
        FacetModel result = new FacetModel();
        result.setSpaceNodes(spaceNodes);
        return result;
    }

    public MFIntegrateTask getMFQuadratureTask() {
        return new ZeroLevelTask();
    }

    private void genNumOfHoleRows() {
        double d = (holeRadius + holeDistance / 2) * 2;
        numOfHoleRows = (int) Math.floor((rectangle.getHeight() / d - 1) * 2 / Math.sqrt(3) + 1);
    }

    private void genNumOfHoleCols() {
        numOfHoleCols = (int) Math.floor(rectangle.getWidth() / (holeDistance / 2 + holeRadius) / 2);
    }

    private void genHoles() {
        genNumOfHoleRows();
        genNumOfHoleCols();
        final double r = holeRadius + holeDistance / 2;
        final double d = 2 * r;
        final double x0 = rectangle.getX() + r + (rectangle.getWidth() - d * numOfHoleCols) / 2;
        final double y0 = rectangle.getY() + r
                + (rectangle.getHeight() - d * ((numOfHoleRows - 1) * Math.cos(Math.PI / 6) + 1)) / 2;
        final double deltaY = d * Math.cos(Math.PI / 6);
        final double deltaX = d;
        holes = new LinkedList<>();
        for (int i = 0; i < numOfHoleRows; i++) {
            double holeCenterY = y0 + i * deltaY;
            for (int j = 0; j < numOfHoleCols; j++) {
                if (j == numOfHoleCols - 1 && i % 2 != 0) {
                    break;
                }
                double holeCenterX = x0 + deltaX * j;
                if (i % 2 != 0) {
                    holeCenterX += r;
                }
                CircleLevelSet circle = new CircleLevelSet(holeCenterX, holeCenterY, holeRadius);
                circle.setConcrete(false);
                holes.add(circle);
            }
        }
    }

    private void genRectanglePolygon() {
        List<Line> ringsHeads = UIUtils.pathIteratorToSegment2DChains(rectangle.getPathIterator(null));
        rectanglePolygon = Facet.byRingsHeads(ringsHeads);
    }

    public Shape genShape() {
        Area area = new Area(rectangle);
        for (CircleLevelSet cir : holes) {
            area.subtract(new Area(cir.genProfile()));
        }
        return area;
    }

    public void genSegmentChains() {
        chainsHeads = new LinkedList<>();
        Facet rectFraction = rectanglePolygon.fractionize(segmentSize);
        chainsHeads.addAll(rectFraction.getRingsHeads());
        for (CircleLevelSet cir : holes) {
            chainsHeads.add(cir.toArcs(segmentSize));
        }
        SegmentChainsIterator<Segment> iter = new SegmentChainsIterator<>(chainsHeads);
        while (iter.hasNext()) {
            Segment seg = iter.next();
            MFNode newNode = new MFNode(seg.getStart().getCoord());
            seg.setStart(newNode);
        }
    }

    private void genSpaceNodes() {
        spaceNodes = new LinkedList<>();
        for (TriangleContourCell cell : triangles) {
            for (Segment seg : cell) {
                Node node = seg.getStart();
                if (node.getId() <= IntIdentityMap.NULL_INDEX_SUPREMUM) {
                    spaceNodes.add(new MFNode(node.getCoord()));
                    node.setId(Integer.MAX_VALUE);
                }
            }
        }
        for (Node nd : spaceNodes) {
            nd.setId(Integer.MIN_VALUE);
        }
    }

    private void genVolumeQuadraturePoints() {
        volumeQuadraturePoints = new LinkedList<>();
        SymmetricTriangleQuadrature symTriangleQuadrature = new SymmetricTriangleQuadrature();
        symTriangleQuadrature.setDegree(quadraturePower);
        double[] vertes = new double[6];
        for (TriangleContourCell cell : triangles) {
            symTriangleQuadrature.setTriangle(cell);
            for (QuadraturePoint qp : symTriangleQuadrature) {
                volumeQuadraturePoints.add(qp);
            }
        }
    }

    private void genBoundaryQuadraturePoints() {
        genSegmentChains();
        SegmentChainsIterator<Segment> iterator = new SegmentChainsIterator<>(chainsHeads);
        Segment2DQuadrature segment2DQuadrature = new Segment2DQuadrature();
        segment2DQuadrature.setDegree(quadraturePower);
        boundaryQuadraturePoints = new LinkedList<>();
        while (iterator.hasNext()) {
            Segment segment = iterator.next();
            segment2DQuadrature.setSegment(segment);
            for (Segment2DQuadraturePoint qp : segment2DQuadrature) {
                boundaryQuadraturePoints.add(qp);
            }
        }
    }

    private void genTriangleContourCells() {
        Rectangle2D nodesBounds = genNodesBounds();
        TriangleContourCellFactory factory = new TriangleContourCellFactory();
        factory.setRectangle(nodesBounds);
        factory.setEdgeLength(triangleSize);
        triangles = (List) factory.produce();
    }

    private Rectangle2D genNodesBounds() {
        Rectangle2D nodesBounds = new Rectangle2D.Double(
                rectangle.getX() - spacesNodesExtension,
                rectangle.getY() - spacesNodesExtension,
                rectangle.getWidth() + 2 * spacesNodesExtension,
                rectangle.getHeight() + 2 * spacesNodesExtension);
        return nodesBounds;
    }

    public double getTriangleSize() {
        return triangleSize;
    }

    public double getSegmentSize() {
        return segmentSize;
    }

    public Rectangle2D getRectangle() {
        return rectangle;
    }

    public List<CircleLevelSet> getHoles() {
        return holes;
    }

    public List<Segment2DQuadraturePoint> getBoundaryQuadraturePoints() {
        return boundaryQuadraturePoints;
    }

    public List<TriangleContourCell> getTriangles() {
        return triangles;
    }

    private void genLevelSetFunction() {
        List<DifferentiableFunction> functions = new LinkedList<DifferentiableFunction>(holes);
        functions.add(rectanglePolygon.getLevelSetFunction());
        levelSetFunction = DifferentiableFunctionUtils.max(functions);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    class ZeroLevelTask implements MFIntegrateTask {

        @Override
        public List<MFIntegratePoint> volumeTasks() {
            List<MFIntegratePoint> result = new LinkedList<>();
            for (QuadraturePoint qp : volumeQuadraturePoints) {
                SimpMFIntegratePoint taskPoint = new SimpMFIntegratePoint();
                taskPoint.setCoord(qp.coord);
                taskPoint.setWeight(qp.weight);
                taskPoint.setLoad(levelSetFunction.value(qp.coord, null));
                result.add(taskPoint);
            }
            return result;
        }

        @Override
        public List<MFBoundaryIntegratePoint> neumannTasks() {
            return null;
        }

        @Override
        public List<MFBoundaryIntegratePoint> dirichletTasks() {
            List<MFBoundaryIntegratePoint> result = new LinkedList<>();
            double[] value = new double[]{0};
            boolean[] validity = new boolean[]{true};
            for (Segment2DQuadraturePoint qp : boundaryQuadraturePoints) {
                SimpMFBoundaryIntegratePoint pt = new SimpMFBoundaryIntegratePoint();

                pt.setCoord(qp.coord);
                pt.setWeight(qp.weight);
                pt.setLoad(value);
                pt.setLoadValidity(validity);
                //TODO: not very good to create new MFLineBnd here!!! 
                pt.setBoundary(new MFLineBnd((Line) qp.segment));
                pt.setBoundaryParameter(qp.segmentParameter);
                result.add(pt);
            }
            return result;
        }

        @Override
        public int getId() {
            return RectangleWithHoles.this.getId();
        }

        @Override
        public void setId(int id) {
            RectangleWithHoles.this.setId(id);
        }
    }
}
