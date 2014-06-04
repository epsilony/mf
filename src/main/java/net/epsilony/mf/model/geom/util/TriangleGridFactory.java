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
package net.epsilony.mf.model.geom.util;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.mf.model.geom.SimpMFCell;
import net.epsilony.mf.model.geom.SimpMFEdge;
import net.epsilony.mf.model.geom.util.TriangleGridFactory.TriangleGrid;
import net.epsilony.tb.solid.Node;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleGridFactory implements Supplier<TriangleGrid> {
    private double                             triangleWidth = 0, triangleHeight = 0;
    private int                                numRows       = 0;
    private int                                numCols       = 0;
    private double                             left, up;
    private Function<double[], ? extends Node> nodeFactory;
    private Supplier<? extends MFEdge>         edgeFactory;
    private Supplier<? extends MFCell>         triangleFactory;

    public static class TriangleGrid {
        private final double     left, up;
        private final double     triangleWidth, triangleHeight;
        private final MFCell[][] triangles;
        private final Node[][]   vertesGrid;

        public TriangleGrid(double left, double up, double triangleWidth, double triangleHeight, MFCell[][] triangles,
                Node[][] vertesGrid) {
            this.left = left;
            this.up = up;
            this.triangleWidth = triangleWidth;
            this.triangleHeight = triangleHeight;
            this.triangles = triangles;
            this.vertesGrid = vertesGrid;
        }

        public double getLeft() {
            return left;
        }

        public double getUp() {
            return up;
        }

        public MFCell[][] getTriangles() {
            return triangles;
        }

        public Node[][] getVertesGrid() {
            return vertesGrid;
        }

        public double getTriangleWidth() {
            return triangleWidth;
        }

        public double getTriangleHeight() {
            return triangleHeight;
        }

        public int getTriangleRowsNum() {
            return triangles.length;
        }

        public int getTriangleColsNum() {
            return triangles[0].length;
        }

    }

    @Override
    public TriangleGrid get() {
        if (triangleHeight <= 0 || triangleWidth <= 0 || numCols < 1 || numRows < 1) {
            throw new IllegalStateException();
        }
        Node[][] nodes = calcVertes();
        MFCell[][] triangles = calcTriangles(nodes);
        linkInnerEdges(triangles);
        return new TriangleGrid(left, up, triangleWidth, triangleHeight, triangles, nodes);
    }

    public MFCell[][] calcTriangles(Node[][] nodes) {
        MFCell[][] result = new MFCell[numRows][numCols];

        ArrayList<Node> inputNodes = Lists.newArrayList(null, null, null);
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int m = (row + col) % 2;
                inputNodes.set(0, nodes[row + m][col / 2 + 1 - m]);
                inputNodes.set(1, nodes[row + m][col / 2 + m]);
                inputNodes.set(2, nodes[row + 1 - m][(col + 1) / 2]);
                result[row][col] = createTriangle(inputNodes);
            }
        }
        return result;
    }

    private MFCell createTriangle(ArrayList<Node> inputNodes) {
        MFCell triangle = triangleFactory.get();
        if (triangle.vertesSize() != 3) {
            throw new IllegalStateException();
        }
        for (int i = 0; i < triangle.vertesSize(); i++) {
            MFEdge edge = edgeFactory.get();
            edge.setStart(inputNodes.get(i));
            triangle.setVertexEdge(i, edge);
        }
        triangle.connectEdges();
        return triangle;
    }

    private void linkInnerEdges(MFCell[][] cells) {
        for (int row = 0; row < cells.length; row++) {
            MFCell[] cellRow = cells[row];
            for (int col = 1; col < cellRow.length; col++) {
                int m = (row + col - 1) % 2;
                linkEdge(cellRow[col - 1], cellRow[col], 2 - m);
            }
        }

        for (int row = 0; row < cells.length - 1; row++) {
            int m = row % 2;
            for (int col = 1 - m; col < cells[row].length; col += 2) {
                linkEdge(cells[row][col], cells[row + 1][col], 0);
            }
        }
    }

    private void linkEdge(MFCell a, MFCell b, int i) {
        a.getVertexEdge(i).connectOpposite(b.getVertexEdge(i));
    }

    private Node[][] calcVertes() {
        Node[][] result = new Node[vertesRowSize()][];
        for (int row = 0; row < result.length; row++) {
            Node[] rowCoords = new Node[vertesColSize(row)];
            result[row] = rowCoords;
            for (int col = 0; col < rowCoords.length; col++) {
                rowCoords[col] = nodeFactory.apply(calcVertesCoord(row, col));
            }
        }
        return result;
    }

    private int vertesColSize(int row) {
        return numCols / 2 + 2 - row % 2;
    }

    private int vertesRowSize() {
        return numRows + 1;
    }

    private double[] calcVertesCoord(int row, int col) {
        double x = left + triangleWidth / 2 * (row % 2) + col * triangleWidth;
        double y = up - row * triangleHeight;
        return new double[] { x, y };
    }

    public Function<double[], ? extends Node> getNodeFactory() {
        return nodeFactory;
    }

    public void setNodeFactory(Function<double[], ? extends Node> nodeFactory) {
        Objects.requireNonNull(nodeFactory);
        this.nodeFactory = nodeFactory;
    }

    public Supplier<? extends MFCell> getTriangleFactory() {
        return triangleFactory;
    }

    public void setTriangleFactory(Supplier<? extends MFCell> triangleFactory) {
        this.triangleFactory = triangleFactory;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumRowsCols(int triangleNumRows, int triangleNumCols) {
        if (triangleNumRows < 1 || triangleNumCols < 1) {
            throw new IllegalArgumentException();
        }
        this.numRows = triangleNumRows;
        this.numCols = triangleNumCols;
    }

    public double getLeft() {
        return left;
    }

    public double getUp() {
        return up;
    }

    public void setUpLeft(double up, double left) {
        this.left = left;
        this.up = up;
    }

    public double getTriangleWidth() {
        return triangleWidth;
    }

    public double getTriangleHeight() {
        return triangleHeight;
    }

    public void setTriangleWidthHeight(double triangleWidth, double triangleHeight) {
        if (triangleWidth <= 0 || triangleHeight <= 0) {
            throw new IllegalArgumentException();
        }
        this.triangleWidth = triangleWidth;
        this.triangleHeight = triangleHeight;
    }

    public Supplier<? extends MFEdge> getEdgeFactory() {
        return edgeFactory;
    }

    public void setEdgeFactory(Supplier<? extends MFEdge> edgeFactory) {
        Objects.requireNonNull(edgeFactory);
        this.edgeFactory = edgeFactory;
    }

    public int neighborsNum(int row, int col) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            throw new IllegalArgumentException();
        }
        int res;
        if (numRows == 1) {
            if (col == 0 || col == numCols - 1) {
                return 1;
            } else {
                return 2;
            }
        }
        if (row == 0) {
            if (col == 0) {
                res = 1;
            } else if (col == numCols - 1) {
                res = numCols % 2 == 0 ? 2 : 1;
            } else {
                res = col % 2 == 0 ? 2 : 3;
            }
        } else if (row == numRows - 1) {
            if (col == 0) {
                res = numRows % 2 == 0 ? 1 : 2;
            } else if (col == numCols - 1) {
                res = (numRows + numCols) % 2 == 0 ? 2 : 1;
            } else {
                res = (row + col) % 2 == 0 ? 3 : 2;
            }
        } else if (col == 0 || col == numCols - 1) {
            res = 2;
        } else {
            res = 3;
        }
        return res;
    }

    public static TriangleGridFactory commonInstance() {
        TriangleGridFactory result = new TriangleGridFactory();
        result.setEdgeFactory(SimpMFEdge::new);
        result.setNodeFactory(MFNode::new);
        result.setTriangleFactory(() -> new SimpMFCell(3));
        return result;
    }
}
