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
package net.epsilony.mf.adapt;

import static org.apache.commons.math3.util.MathArrays.distance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

import org.junit.Before;
import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleCellFissionizerTest {

    double                      triangleWidth;
    double                      triangleHeight;
    double                      triangleGridLeft;
    double                      triangleGridUp;
    private TriangleGrid        triangleGrid;
    private int                 numRows;
    private int                 numCols;
    TriangleCellFissionizer     cellFissionizer;
    private TriangleGridFactory gridFactory;

    @Before
    public void initCellFissionizer() {
        cellFissionizer = new TriangleCellFissionizer();
        cellFissionizer.setCellFactory(() -> new SimpMFCell(3));
        cellFissionizer.setEdgeFactory(SimpMFEdge::new);
        cellFissionizer.setNodeFactory(Node::new);
    }

    @Test
    public void test() {
        triangleWidth = 0.25;
        triangleHeight = 0.25;
        triangleGridLeft = 1;
        triangleGridUp = 10;
        int[] sampleRows = { 1, 2, 3, 4 };
        int[] sampleCols = { 2, 3, 4 };
        for (int r : sampleRows) {
            for (int c : sampleCols) {
                numRows = r;
                numCols = c;
                doTest();
            }
        }
    }

    private void doTest() {
        for (int i = 0; i < numRows * numCols; i++) {
            createTriangleGrid();
            MFCell[][] gridTriangles = triangleGrid.getTriangles();
            List<MFCell> triangles = Arrays.stream(gridTriangles).flatMap(Arrays::stream).collect(Collectors.toList());
            assertTrue(triangles.size() > 0);
            doTestForSingleTriangle(triangles, i, 0.5);
        }

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                createTriangleGrid();
                MFCell[][] gridTriangles = triangleGrid.getTriangles();

                doTestForLastTriangle(gridTriangles, i, j, 0.5);
            }
        }
    }

    private void doTestForLastTriangle(MFCell[][] gridTriangles, int row, int col, double scaleRatio) {
        MFCell lastFission = gridTriangles[row][col];
        Set<MFCell> triangleSet = new HashSet<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                MFCell tri = gridTriangles[i][j];
                if (tri == lastFission) {
                    continue;
                }
                FissionResult res = fission(triangleSet, tri);
                assertFissionResultFullNeighbours(i, j, res);
            }
        }

        FissionResult result = fission(triangleSet, lastFission);
        assertNotNull(result.getFissioned());

        assertFissionResultFullNeighbours(row, col, result);
        result.getNewCells().forEach(cell -> {
            assertCellScale(triangleWidth * scaleRatio, triangleHeight * scaleRatio, cell);
        });

        triangleSet.forEach(this::assertCellConnections);

        MFCell start = result.getNewCells().get(0);
        Set<MFCell> visitedSet = new HashSet<>();
        visitByNeighbor(start, visitedSet);

        assertEquals(triangleSet, visitedSet);

    }

    private void assertFissionResultFullNeighbours(int row, int col, FissionResult result) {
        List<MFCell> fullNeighbourNewCells;
        if (3 == gridFactory.neighborsNum(row, col)) {
            fullNeighbourNewCells = result.getNewCells();
        } else {
            MFCell center = result.getNewCells().get(3);
            fullNeighbourNewCells = Arrays.asList(center);
        }
        for (MFCell cell : fullNeighbourNewCells) {
            int[] num = new int[1];
            cell.forEachNeighbor(cl -> num[0]++);
            assertEquals(3, num[0]);
        }
    }

    private FissionResult fission(Set<MFCell> trianglesSet, MFCell triangleToFission) {
        Set<MFCell> oriNeibours = new HashSet<>();
        triangleToFission.forEachNeighbor(oriNeibours::add);

        cellFissionizer.setCell(triangleToFission);
        cellFissionizer.fission();
        FissionResult[] results = new FissionResult[1];
        cellFissionizer.record(res -> results[0] = res);

        FissionResult result = results[0];
        assertNotNull(result.getFissioned());
        assertEquals(triangleToFission, result.getFissioned());

        trianglesSet.remove(triangleToFission);
        trianglesSet.addAll(result.getNewCells());

        assertNeighbours(oriNeibours, result);
        return result;
    }

    private void doTestForSingleTriangle(List<MFCell> triangles, int index, double scaleRatio) {
        MFCell triangle = triangles.get(index);
        Set<MFCell> triangleSet = new HashSet<>(triangles);

        FissionResult result = fission(triangleSet, triangle);
        assertNotNull(result.getFissioned());

        MFCell center = result.getNewCells().get(3);
        int[] num = new int[1];
        center.forEachNeighbor(cl -> num[0]++);
        assertEquals(3, num[0]);

        result.getNewCells().forEach(cell -> {
            assertCellScale(triangleWidth * scaleRatio, triangleHeight * scaleRatio, cell);
        });

        triangleSet.forEach(this::assertCellConnections);

        MFCell start = result.getNewCells().get(0);
        Set<MFCell> visitedSet = new HashSet<>();
        visitByNeighbor(start, visitedSet);

        assertEquals(triangleSet, visitedSet);
    }

    private void visitByNeighbor(MFCell start, Set<MFCell> visitedSet) {
        if (visitedSet.contains(start)) {
            return;
        }
        visitedSet.add(start);
        start.forEachNeighbor(nb -> visitByNeighbor(nb, visitedSet));
    }

    private void createTriangleGrid() {
        gridFactory = new TriangleGridFactory();
        gridFactory.setEdgeFactory(SimpMFEdge::new);
        gridFactory.setNodeFactory(Node::new);
        gridFactory.setTriangleFactory(() -> new SimpMFCell(3));
        gridFactory.setTriangleWidthHeight(triangleWidth, triangleHeight);
        gridFactory.setUpLeft(triangleGridUp, triangleGridLeft);
        gridFactory.setNumRowsCols(numRows, numCols);
        triangleGrid = gridFactory.get();
    }

    private void assertCellConnections(MFCell cell) {
        for (MFEdge edge : cell.getVertexEdge(0).iterable(MFEdge.class)) {
            assertTrue(edge.getSucc().getPred() == edge);
            assertTrue(edge.getPred().getSucc() == edge);
            assertTrue(edge.getCell() == cell);
            if (edge.getOpposite() != null) {
                assertTrue(edge.getOpposite().getOpposite() == edge);
                assertTrue(edge.getOpposite().getStart() == edge.getEnd());
                assertTrue(edge.getOpposite().getEnd() == edge.getStart());
            }

            MFEdge succ = (MFEdge) edge.getSucc();
            if (succ.getOpposite() != null && edge.getOpposite() != null) {
                assertTrue(succ.getOpposite().getCell() != edge.getOpposite().getCell());
            }
        }
    }

    private void assertCellScale(double expWidth, double expHeight, MFCell cell) {
        double[] v0c = cell.getVertexCoord(0);
        double[] v1c = cell.getVertexCoord(1);
        double[] v2c = cell.getVertexCoord(2);

        double width = distance(v1c, v0c);
        double height = Math2D.area(new double[][] { v0c, v1c, v2c }) * 2 / width;
        assertEquals(expWidth, width, expWidth * 1e-12);
        assertEquals(expHeight, height, expHeight * 1e-12);

        double projectionParameter = Math2D.projectionParameter(v0c, v1c, v2c);
        assertEquals(0.5, projectionParameter, 1e-12);
    }

    private void assertNeighbours(Set<MFCell> oriNeighbours, FissionResult fissionResult) {
        List<MFCell> newCells = fissionResult.getNewCells();
        Set<MFCell> fissionedNeighbours = new HashSet<>();
        newCells.forEach(cell -> cell.forEachNeighbor(fissionedNeighbours::add));
        fissionedNeighbours.removeAll(newCells);
        assertEquals(oriNeighbours, fissionedNeighbours);

        Set<MFCell> expCenterNeighbour = new HashSet<>(newCells.subList(0, newCells.size() - 1));
        Set<MFCell> actCenterNeighbour = new HashSet<>();
        newCells.get(3).forEachNeighbor(actCenterNeighbour::add);
        assertEquals(expCenterNeighbour, actCenterNeighbour);
    }

}
