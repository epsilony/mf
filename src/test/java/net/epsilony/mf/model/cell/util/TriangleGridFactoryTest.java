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
package net.epsilony.mf.model.cell.util;

import static org.apache.commons.math3.util.MathArrays.distance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.mf.model.geom.SimpMFCell;
import net.epsilony.mf.model.geom.SimpMFEdge;
import net.epsilony.mf.model.geom.util.TriangleGridFactory;
import net.epsilony.mf.model.geom.util.TriangleGridFactory.TriangleGrid;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Node;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleGridFactoryTest {

    private double triangleHeight;
    private double triangleWidth;
    private int left;
    private int up;
    private TriangleGrid triangleGrid;
    private List<MFCell> cells;
    private TriangleGridFactory factory;

    @Test
    public void test() {
        triangleWidth = 0.25;
        triangleHeight = 0.25;
        up = 10;
        left = 1;

        int[] rowSizes = { 1, 2, 3, 4 };
        int[] colSizes = { 1, 2, 3, 4 };
        for (int rowSize : rowSizes) {
            for (int colSize : colSizes) {
                doTest(rowSize, colSize);
            }
        }
    }

    private void doTest(int numRows, int numCols) {
        init(numRows, numCols);

        MFCell[][] triangles = triangleGrid.getTriangles();
        assertEquals(numRows, triangles.length);
        for (MFCell[] row : triangles) {
            assertEquals(numCols, row.length);
        }

        cells = Arrays.stream(triangles).flatMap(Arrays::stream).collect(Collectors.toList());
        cells.forEach(this::checkTriangleSize);
        cells.forEach(this::checkCellEdgeConnection);

        if (numCols >= 2) {
            MFCell start = cells.get(0);
            Set<MFCell> visited = new HashSet<>();
            visitAllConnected(start, visited);
            assertEquals(new HashSet<>(cells), visited);
        }

        for (int r = 0; numCols >= 2 && numRows >= 2 && r < triangles.length; r++) {
            MFCell[] row = triangles[r];
            for (int c = 0; c < row.length; c++) {
                MFCell cell = row[c];
                int[] num = new int[1];
                cell.forEachNeighbor(nb -> num[0]++);
                assertEquals(factory.neighborsNum(r, c), num[0]);
            }
        }
    }

    private void visitAllConnected(MFCell start, Set<MFCell> visited) {
        visited.add(start);
        for (MFEdge edge : start.getVertexEdge(0).iterable(MFEdge.class)) {
            if (null != edge.getOpposite()) {
                final MFCell cell = edge.getOpposite().getCell();
                if (visited.contains(cell)) {
                    continue;
                }
                visitAllConnected(cell, visited);
            }
        }
    }

    private void checkTriangleSize(MFCell cell) {
        double[] v0 = cell.getVertexCoord(0);
        double[] v1 = cell.getVertexCoord(1);
        double[] v2 = cell.getVertexCoord(2);

        assertEquals(v0[1], v1[1], 0);
        assertEquals(triangleWidth, distance(v0, v1), triangleWidth * 1e-14);
        assertEquals(triangleHeight, Math2D.area(new double[][] { v0, v1, v2 }) * 2 / triangleHeight,
                triangleHeight * 1e-14);
        assertEquals(0.5, Math2D.projectionParameter(v0, v1, v2), 1e-14);
    }

    private void checkCellEdgeConnection(MFCell cell) {
        int edgeSize = 0;
        for (MFEdge edge : cell.getVertexEdge(0).iterable(MFEdge.class)) {
            edgeSize++;
            assertTrue(edge.getCell() == cell);
            if (edge.getOpposite() != null) {
                assertTrue(edge.getStart() == edge.getOpposite().getEnd());
                assertTrue(edge.getEnd() == edge.getOpposite().getStart());
                assertTrue(edge == edge.getOpposite().getOpposite());
            }
        }
        assertEquals(3, edgeSize);
    }

    private void init(int rowSize, int colSize) {
        factory = new TriangleGridFactory();
        factory.setNumRowsCols(rowSize, colSize);
        factory.setNodeFactory(Node::new);
        factory.setTriangleFactory(() -> new SimpMFCell(3));
        factory.setEdgeFactory(SimpMFEdge::new);
        factory.setUpLeft(up, left);
        factory.setTriangleWidthHeight(triangleWidth, triangleHeight);
        triangleGrid = factory.get();
    }
}
