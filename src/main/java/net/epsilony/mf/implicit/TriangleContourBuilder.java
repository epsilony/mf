/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.adaptive.AdaptiveCellEdge;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Line2D;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.GenericFunction;
import net.epsilony.tb.IntIdentityMap;
import net.epsilony.tb.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourBuilder {

    public static double DEFAULT_CONTOUR_LEVEL = 0;
    protected List<TriangleContourCell> cells;
    protected double contourLevel = DEFAULT_CONTOUR_LEVEL;
    protected GenericFunction<double[], double[]> levelSetFunction;
    protected List<Line2D> contourHeads;
    protected LinkedList<TriangleContourCell> openRingHeadCells;
    protected LinkedList<Line2D> openRingHeadSegments;
    protected Iterator<TriangleContourCell> cellsIterator;
    IntIdentityMap<Node, double[]> nodesValuesMap = new IntIdentityMap<>();

    public void setCells(List<TriangleContourCell> cells) {
        this.cells = cells;
    }

    public void setContourLevel(double contourLevel) {
        this.contourLevel = contourLevel;
    }

    public void setLevelSetFunction(GenericFunction<double[], double[]> levelSetFunction) {
        this.levelSetFunction = levelSetFunction;
    }

    public void genContour() {
        prepareGenContour();
        while (true) {
            TriangleContourCell headCell = nextHeadCell();
            if (null == headCell) {
                break;
            }
            genContourFromHeadCell(headCell);
        }
    }

    public List<Line2D> getContourHeads() {
        return contourHeads;
    }

    private void prepareGenContour() {
        for (TriangleContourCell cell : cells) {
            cell.setVisited(false);
            AdaptiveCellEdge[] edges = cell.getEdges();
            for (AdaptiveCellEdge edge : edges) {
                edge.getStart().setId(-1);
            }
        }

        int nodesNum = 0;
        for (TriangleContourCell cell : cells) {
            AdaptiveCellEdge[] edges = cell.getEdges();
            for (AdaptiveCellEdge edge : edges) {
                Node start = edge.getStart();
                if (start.getId() > -1) {
                    continue;
                }
                start.setId(nodesNum++);
            }
        }

        nodesValuesMap.clear();
        nodesValuesMap.appendNullValues(nodesNum);

        contourHeads = new LinkedList<>();
        openRingHeadCells = new LinkedList<>();
        openRingHeadSegments = new LinkedList<>();
        cellsIterator = cells.iterator();
    }

    private TriangleContourCell nextHeadCell() {
        TriangleContourCell result = null;
        while (cellsIterator.hasNext()) {
            TriangleContourCell cell = cellsIterator.next();
            if (cell.isVisited()) {
                continue;
            }
            setupFunctionData(cell);

            Line2D sourceEdge = cell.getContourSourceEdge();
            if (sourceEdge == null) {
                cell.setVisited(true);
                continue;
            }
            result = cell;
            break;
        }
        return result;
    }

    private void genContourFromHeadCell(TriangleContourCell headCell) {
        headCell.setVisited(true);
        Line2D chainHead = new Line2D(genContourNode(headCell.getContourSourceEdge()));
        contourHeads.add(chainHead);

        openRingHeadCells.add(headCell);
        openRingHeadSegments.add(chainHead);
        TriangleContourCell contourCell = headCell;

        Line2D segment = chainHead;
        while (true) {
            TriangleContourCell nextContourCell = contourCell.nextContourCell();
            if (null == nextContourCell) {
                Line2D newSucc = new Line2D(genContourNode(contourCell.getContourDestinationEdge()));
                Segment2DUtils.link(segment, newSucc);
                break;
            } else {
                contourCell = nextContourCell;
            }

            if (contourCell == headCell) {
                Segment2DUtils.link(segment, chainHead);
                openRingHeadCells.remove(headCell);
                openRingHeadSegments.remove(chainHead);
                break;
            }

            if (contourCell.isVisited()) {
                boolean merged = tryMergeWithOpenRingHeads(contourCell, segment);
                if (merged) {
                    break;
                }
                throw new IllegalStateException();
            }

            contourCell.setVisited(true);
            setupFunctionData(contourCell);

            Line2D newSucc = new Line2D(genContourNode(contourCell.getContourSourceEdge()));
            Segment2DUtils.link(segment, newSucc);
            segment = newSucc;

        }
    }

    private void setupFunctionData(TriangleContourCell cell) {
        AdaptiveCellEdge[] edges = cell.getEdges();
        for (int i = 0; i < edges.length; i++) {
            Node nd = cell.getNode(i);
            double[] nodeValue = nodesValuesMap.get(nd);
            if (null == nodeValue) {
                nodesValuesMap.put(nd, levelSetFunction.value(edges[i].getStart().getCoord(), null));
            }
        }
        cell.updateStatus(contourLevel, nodesValuesMap);
    }

    private Node genContourNode(Line2D contourSourceEdge) {
        double[] startCoord = contourSourceEdge.getStart().getCoord();
        double[] endCoord = contourSourceEdge.getEnd().getCoord();
        double startValue = nodesValuesMap.get(contourSourceEdge.getStart())[0];
        double endValue = nodesValuesMap.get(contourSourceEdge.getEnd())[0];
        double t = startValue / (startValue - endValue);
        double[] resultCoord = Math2D.pointOnSegment(startCoord, endCoord, t, null);
        return new Node(resultCoord);
    }

    private boolean tryMergeWithOpenRingHeads(TriangleContourCell contourCell, Line2D segment) {
        Iterator<TriangleContourCell> openHeadCellIter = openRingHeadCells.descendingIterator();
        Iterator<Line2D> openHeadSegIter = openRingHeadSegments.descendingIterator();
        boolean findAndRemove = false;
        while (openHeadCellIter.hasNext()) {
            TriangleContourCell cell = openHeadCellIter.next();
            Line2D openRingHead = openHeadSegIter.next();
            if (cell == contourCell) {
                openHeadCellIter.remove();
                openHeadSegIter.remove();
                Segment2DUtils.link(segment, openRingHead);

                contourHeads.remove(openRingHead);

                findAndRemove = true;
                break;
            }
        }
        return findAndRemove;
    }

    public List<TriangleContourCell> getCells() {
        return cells;
    }

    public double getContourLevel() {
        return contourLevel;
    }

    public GenericFunction<double[], double[]> getLevelSetFunction() {
        return levelSetFunction;
    }

    public IntIdentityMap<Node, double[]> getNodesValuesMap() {
        return nodesValuesMap;
    }
}
