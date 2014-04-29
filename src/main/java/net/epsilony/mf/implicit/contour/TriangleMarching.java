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
package net.epsilony.mf.implicit.contour;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.adapt.Fissionizer;
import net.epsilony.mf.adapt.Fissionizer.FissionRecord;
import net.epsilony.mf.model.cell.MFCell;
import net.epsilony.mf.model.cell.MFEdge;
import net.epsilony.mf.model.cell.MFLine;
import net.epsilony.mf.model.cell.util.MFLineIterator;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleMarching {

    private Collection<? extends MFCell> triangles;
    private Fissionizer fissionizer;
    private final Map<MFCell, MFEdge> headMap = new LinkedHashMap<>();
    private final Set<MFCell> intersectingTriangles = new LinkedHashSet<>();
    private ToDoubleFunction<double[]> levelFunction;
    private Function<MFEdge, double[]> zeroPointSolver;
    private Supplier<? extends MFEdge> edgeFactory;
    private Function<double[], ? extends Node> nodeFactory;
    private List<MFEdge> result;

    public static enum TriangleType {
    FISSION, NOT_INTERSECTING, INTERSECTING, ERROR;

    }

    public List<MFEdge> buildContour(Collection<? extends MFCell> triangles) {
        checkTriangles(triangles);
        this.triangles = triangles;

        preProcess();
        buildContourChains();
        removeZeroLengthEdges();
        return result;
    }

    private void removeZeroLengthEdges() {
        result = new ArrayList<>(headMap.size());
        for (MFEdge head : headMap.values()) {
            head = removeZeroLengthInChainEdges(head);
            if (head != null) {
                result.add(head);
            }
        }
    }

    private final MFLineIterator<MFEdge> edgeIterator = new MFLineIterator<MFEdge>(MFEdge.class);

    private MFEdge removeZeroLengthInChainEdges(MFEdge head) {
        edgeIterator.setup(head);
        MFEdge nonZero = null;
        while (edgeIterator.hasNext()) {
            MFEdge edge = edgeIterator.next();
            if (edge.getSucc() == null) {
                break;
            }
            if (!isCoordEquals(edge.getStartCoord(), edge.getEndCoord())) {
                nonZero = edge;
                break;
            }
        }
        if (nonZero == null) {
            return null;
        }
        if (nonZero != head) {
            nonZero.connectPred(head.getPred());
            head = nonZero;
        }

        MFEdge start = (MFEdge) head.getSucc();
        MFEdge zeroEdge = null;
        do {
            edgeIterator.setup(start, head);
            zeroEdge = null;
            while (edgeIterator.hasNext()) {
                MFEdge edge = edgeIterator.next();
                if (edge.getSucc() == null) {
                    break;
                }
                if (isCoordEquals(edge.getStartCoord(), edge.getEndCoord())) {
                    zeroEdge = edge;
                    break;
                }
            }
            if (null != zeroEdge) {
                final MFLine succ = zeroEdge.getSucc();
                zeroEdge.getPred().connectSucc(succ);
                start = (MFEdge) succ;
            }
        } while (zeroEdge != null);
        if (head.getSucc() != null && head.getSucc() != head) {
            return head;
        } else {
            return null;
        }
    }

    private boolean isCoordEquals(double[] startCoord, double[] endCoord) {
        return startCoord == endCoord;
    }

    private void checkTriangles(Collection<? extends MFCell> triangles) {
        for (MFCell triangle : triangles) {
            if (triangle.vertesSize() != 3) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void preProcess() {
        intersectingTriangles.clear();

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Collection<MFCell> mayNeedFissions = (Collection) triangles;
        do {
            Set<MFCell> newMayNeedFissions = new LinkedHashSet<>();
            for (MFCell triangle : mayNeedFissions) {
                switch (classify(triangle)) {
                case ERROR:
                    throw new IllegalStateException();
                case FISSION:
                    FissionRecord fissionRecord = fissionizer.recursivelyFussion(triangle);
                    if (!fissionRecord.isSuccess()) {
                        throw new IllegalArgumentException();
                    }
                    newMayNeedFissions.addAll(fissionRecord.getNewCells());
                    fissionRecord.getNewCells().forEach(cell -> {
                        cell.forEachNeighbor(newMayNeedFissions::add);
                    });
                    break;
                case INTERSECTING:
                    intersectingTriangles.add(triangle);
                    break;
                case NOT_INTERSECTING:
                    break;
                default:
                    throw new IllegalStateException();
                }
            }
            mayNeedFissions = newMayNeedFissions;
        } while (!mayNeedFissions.isEmpty());
    }

    public TriangleType classify(MFCell triangle) {
        int interContourEdgesNum = 0;
        int vertexId = 1;
        MFEdge sameSideInterContourEdge = null;
        boolean triangleInterContour = true;
        Node vertexNode = triangle.getVertex(vertexId);
        lineIterator.setup(triangle.getVertexEdge(0));
        while (lineIterator.hasNext()) {
            MFEdge edge = lineIterator.next();
            if (edge.getStart() == vertexNode) {
                sameSideInterContourEdge = null;
                vertexId++;
                if (vertexId < 3) {
                    vertexNode = triangle.getVertex(vertexId);
                }
            }
            if (isIntersectingContour(edge)) {
                interContourEdgesNum++;
                if (sameSideInterContourEdge == null) {
                    sameSideInterContourEdge = edge;
                } else {
                    triangleInterContour = false;
                }
            }
        }

        if (interContourEdgesNum % 2 != 0) {
            return TriangleType.ERROR;
        } else if (interContourEdgesNum == 0) {
            return TriangleType.NOT_INTERSECTING;
        } else if (interContourEdgesNum == 2 && triangleInterContour) {
            return TriangleType.INTERSECTING;
        } else {
            return TriangleType.FISSION;
        }
    }

    private void buildContourChains() {
        headMap.clear();

        List<MFCell> copy = new ArrayList<>(intersectingTriangles);

        for (MFCell cell : copy) {
            boolean removed = intersectingTriangles.remove(cell);
            if (removed) {
                buildChain(cell);
            }
        }

    }

    private void buildChain(MFCell firstCell) {
        MFEdge startEdge = searchStartEdge(firstCell);
        double[] start = solve(startEdge);

        MFEdge head = newEdge(start);
        head.setCell(firstCell);
        MFEdge tail = head;
        headMap.put(startEdge.getCell(), head);

        do {
            MFEdge endEdge = searchEndEdge(startEdge);
            double[] end = solve(endEdge);

            MFEdge newEdge = newEdge(start);
            tail.connectSucc(newEdge);
            tail = newEdge;
            newEdge.setCell(startEdge.getCell());

            start = end;

            startEdge = endEdge.getOpposite();

            if (startEdge == null) {
                if (head != null) {
                    newEdge = newEdge(start);
                    tail.connectSucc(newEdge);
                    tail = newEdge;
                }
                break;
            }
            if (null != head && startEdge.getCell() == firstCell) {
                tail.connectSucc(head);
                break;
            }

            boolean removed = intersectingTriangles.remove(startEdge.getCell());
            if (!removed) {
                MFEdge otherHead = headMap.remove(startEdge.getCell());
                tail.connectSucc(otherHead);
                break;
            }
        } while (true);
    }

    private double[] solve(MFEdge edge) {
        double[] startCoord = edge.getStartCoord();
        double[] endCoord = edge.getEndCoord();

        double startLevel = getLevelValue(startCoord);
        if (startLevel == 0) {
            return startCoord;
        }

        double endLevel = getLevelValue(endCoord);
        if (endLevel == 0) {
            return endCoord;
        }

        return zeroPointSolver.apply(edge);
    }

    private final MFLineIterator<MFEdge> lineIterator = new MFLineIterator<MFEdge>(MFEdge.class);

    private MFEdge searchStartEdge(MFCell cell) {
        lineIterator.setup(cell.getVertexEdge(0));
        while (lineIterator.hasNext()) {
            MFEdge edge = lineIterator.next();
            if (isIntersectingContour(edge) && getLevelValue(edge.getStartCoord()) <= 0) {
                return edge;
            }
        }
        return null;
    }

    private boolean isIntersectingContour(MFEdge edge) {
        double startValue = getLevelValue(edge.getStartCoord());
        double endValue = getLevelValue(edge.getEndCoord());
        return isIntersectingContour(startValue, endValue);
    }

    private boolean isIntersectingContour(double startValue, double endValue) {
        return startValue <= 0 && endValue > 0 || startValue > 0 && endValue <= 0;
    }

    private double getLevelValue(double[] coord) {
        return levelFunction.applyAsDouble(coord);
    }

    private MFEdge searchEndEdge(MFEdge startEdge) {
        lineIterator.setup(startEdge.getSucc());
        while (lineIterator.hasNext()) {
            MFEdge edge = lineIterator.next();
            if (isIntersectingContour(edge) && getLevelValue(edge.getEndCoord()) <= 0) {
                return edge;
            }
        }
        return null;
    }

    /**
     * an helper function for reference as zeroPointSolver
     */
    public double[] simpleSolve(MFEdge edge) {
        double[] startCoord = edge.getStartCoord();
        double[] endCoord = edge.getEndCoord();

        double startLevel = getLevelValue(startCoord);
        if (startLevel == 0) {
            return startCoord;
        }

        double endLevel = getLevelValue(endCoord);
        if (endLevel == 0) {
            return endCoord;
        }

        double t = -startLevel / (endLevel - startLevel);
        return Math2D.pointOnSegment(startCoord, endCoord, t, null);
    }

    private MFEdge newEdge(double[] startCoord) {
        MFEdge edge = edgeFactory.get();
        edge.setStart(nodeFactory.apply(startCoord));
        return edge;
    }

    public void setFissionizer(Fissionizer fissionizer) {
        this.fissionizer = fissionizer;
    }

    public void setLevelFunction(ToDoubleFunction<double[]> levelFunction) {
        this.levelFunction = levelFunction;
    }

    public void setZeroPointSolver(Function<MFEdge, double[]> zeroPointSolver) {
        this.zeroPointSolver = zeroPointSolver;
    }

    public void setEdgeFactory(Supplier<? extends MFEdge> edgeFactory) {
        this.edgeFactory = edgeFactory;
    }

    public void setNodeFactory(Function<double[], ? extends Node> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

}
