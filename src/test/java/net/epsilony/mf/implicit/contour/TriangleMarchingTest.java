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

import static org.apache.commons.math3.util.FastMath.min;
import static org.apache.commons.math3.util.MathArrays.distance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.epsilony.mf.adapt.Fissionizer;
import net.epsilony.mf.adapt.Fissionizer.FissionRecord;
import net.epsilony.mf.adapt.TriangleCellFissionizer;
import net.epsilony.mf.implicit.level.CircleLvFunction;
import net.epsilony.mf.implicit.level.IntersectionLvFunction;
import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.mf.model.geom.MFLine;
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
public class TriangleMarchingTest {

    private TriangleMarching    marching;
    private TriangleGridFactory factory;
    private TriangleGrid        triangleGrid;
    private Fissionizer         fissionizer;

    @Before
    public void init() {
        TriangleCellFissionizer triangleFissionizer = new TriangleCellFissionizer();
        triangleFissionizer.setCellFactory(() -> new SimpMFCell(3));
        triangleFissionizer.setEdgeFactory(SimpMFEdge::new);
        triangleFissionizer.setNodeFactory(Node::new);

        fissionizer = new Fissionizer();
        fissionizer.setCellFissionizer(triangleFissionizer);

        marching = new TriangleMarching();
        marching.setEdgeFactory(SimpMFEdge::new);
        marching.setNodeFactory(Node::new);
        marching.setFissionizer(fissionizer);
        marching.setZeroPointSolver(marching::simpleSolve);
    }

    @Test
    public void testSingleZeroTriangle() {
        factory = new TriangleGridFactory();
        factory.setEdgeFactory(SimpMFEdge::new);
        factory.setNodeFactory(Node::new);
        factory.setTriangleFactory(() -> new SimpMFCell(3));
        factory.setUpLeft(10, 1);
        factory.setTriangleWidthHeight(0.25, 0.25);

        int[][] numRowsCols = {
                { 1, 2 },
                { 1, 1 },
                { 1, 3 },
                { 2, 2 },
                { 2, 3 },
                { 3, 3 },
                { 3, 4 },
                { 4, 4 },
                { 4, 5 },
                { 5, 5 },
                { 5, 6 },
                { 6, 6 },
                { 6, 7 },
                { 7, 7 },
                { 7, 8 } };
        for (int[] numRowCol : numRowsCols) {
            factory.setNumRowsCols(numRowCol[0], numRowCol[1]);

            triangleGrid = factory.get();
            MFCell[][] gridTriangles = triangleGrid.getTriangles();
            List<MFCell> triangles = Arrays.stream(gridTriangles).flatMap(Arrays::stream).collect(Collectors.toList());

            for (MFCell zeroVertexTriangle : triangles) {
                assertSingleTriangle(triangles, zeroVertexTriangle);
            }
        }

        for (int[] numRowCol : numRowsCols) {
            factory.setNumRowsCols(numRowCol[0], numRowCol[1]);

            triangleGrid = factory.get();
            MFCell[][] gridTriangles = triangleGrid.getTriangles();
            Set<MFCell> triangleSet = Arrays.stream(gridTriangles).flatMap(Arrays::stream).collect(Collectors.toSet());
            for (int row = 0; row < numRowCol[0]; row++) {
                for (int col = 0; col < numRowCol[1]; col++) {
                    if ((row + col) % 2 == 0) {
                        final MFCell fissionTri = gridTriangles[row][col];
                        FissionRecord fissionResult = fissionizer.recursivelyFussion(fissionTri);
                        triangleSet.remove(fissionTri);
                        triangleSet.addAll(fissionResult.getNewCells());
                    }
                }
            }

            for (int row = 0; row < numRowCol[0]; row++) {
                for (int col = 0; col < numRowCol[1]; col++) {
                    if ((row + col) % 2 == 1) {
                        assertSingleTriangle(triangleSet, gridTriangles[row][col]);
                    }
                }
            }
        }

    }

    private void assertSingleTriangle(Collection<MFCell> triangles, final MFCell zeroVertexTriangle) {
        ZeroOnVertexOtherPositive levelFunction = new ZeroOnVertexOtherPositive(zeroVertexTriangle);
        marching.setLevelFunction(levelFunction::level);

        ArrayList<MFEdge> heads = new ArrayList<>(marching.buildContour(triangles));

        Set<double[]> chainCoords = new HashSet<>();
        Set<MFEdge> chains = new HashSet<>();
        Set<MFCell> chainCells = new HashSet<>();
        for (MFEdge head : heads) {
            head.iterator(MFEdge.class).forEachRemaining(edge -> {
                chainCoords.add(edge.getStartCoord());
                if (edge.getSucc() != null) {
                    chainCells.add(edge.getCell());
                    chains.add(edge);
                }

            });
        }

        Set<double[]> expCoords = new HashSet<>();
        Set<MFCell> expCells = new HashSet<>();
        levelFunction.getCell().getVertexEdge(0).iterator(MFEdge.class).forEachRemaining(edge -> {
            if (edge.getOpposite() != null) {
                expCoords.add(edge.getStartCoord());
                expCoords.add(edge.getEndCoord());
            }
        });
        levelFunction.getCell().forEachNeighbor(expCells::add);

        assertEquals(expCoords, chainCoords);
        assertEquals(expCells, chainCells);
        assertEquals(expCells.size(), chains.size());
    }

    public static class ZeroOnVertexOtherPositive {
        private final MFCell cell;

        public double level(double[] coord) {
            for (MFLine line : cell.getVertexEdge(0)) {
                double[] startCoord = line.getStartCoord();
                if (Arrays.equals(startCoord, coord)) {
                    return 0;
                }
            }
            return 1;
        }

        public ZeroOnVertexOtherPositive(MFCell cell) {
            this.cell = cell;
        }

        public MFCell getCell() {
            return cell;
        }

    }

    @Test
    public void testSingleVertex() {
        factory = new TriangleGridFactory();
        factory.setEdgeFactory(SimpMFEdge::new);
        factory.setNodeFactory(Node::new);
        factory.setTriangleFactory(() -> new SimpMFCell(3));
        factory.setUpLeft(10, 1);
        factory.setTriangleWidthHeight(0.25, 0.25);

        int[][] numRowsCols = {
                { 1, 2 },
                { 1, 1 },
                { 1, 3 },
                { 2, 2 },
                { 2, 3 },
                { 3, 3 },
                { 3, 4 },
                { 4, 4 },
                { 4, 5 },
                { 5, 5 },
                { 5, 6 },
                { 6, 6 },
                { 6, 7 },
                { 7, 7 },
                { 7, 8 } };
        for (int[] numRowCol : numRowsCols) {
            factory.setNumRowsCols(numRowCol[0], numRowCol[1]);

            triangleGrid = factory.get();
            MFCell[][] gridTriangles = triangleGrid.getTriangles();
            List<MFCell> triangles = Arrays.stream(gridTriangles).flatMap(Arrays::stream).collect(Collectors.toList());

            Arrays.stream(triangleGrid.getVertesGrid()).flatMap(Arrays::stream).forEach(nd -> {
                assertSingleVertex(triangles, nd.getCoord());
            });
        }
    }

    private void assertSingleVertex(List<MFCell> triangles, double[] coord) {
        marching.setLevelFunction(x -> {
            if (Arrays.equals(coord, x)) {
                return 0;
            } else {
                return 1;
            }
        });

        Collection<MFEdge> buildContour = marching.buildContour(triangles);
        assertEquals(0, buildContour.size());

    }

    @Test
    public void testTwoWholeWithSmallDistance() {
        factory = new TriangleGridFactory();
        factory.setEdgeFactory(SimpMFEdge::new);
        factory.setNodeFactory(Node::new);
        factory.setTriangleFactory(() -> new SimpMFCell(3));
        factory.setUpLeft(10, 1);
        factory.setTriangleWidthHeight(0.25, 0.25);
        factory.setNumRowsCols(25, 25);

        double radius = 0.25 * 1.51;
        triangleGrid = factory.get();
        MFCell[][] gridTriangle = triangleGrid.getTriangles();
        MFCell leftCenterCell = gridTriangle[12][13 - 4];
        MFCell rightCenterCell = gridTriangle[12][13 + 4];
        MFCell[] toBeFissionedFirst = { gridTriangle[13][13] };
        double[] leftCenter = Math2D.pointOnSegment(leftCenterCell.getVertexCoord(0), leftCenterCell.getVertexCoord(1),
                0.5, null);
        double[] rightCenter = Math2D.pointOnSegment(rightCenterCell.getVertexCoord(0),
                rightCenterCell.getVertexCoord(1), 0.5, null);

        CircleLvFunction left = new CircleLvFunction(leftCenter, radius);
        CircleLvFunction right = new CircleLvFunction(rightCenter, radius);
        IntersectionLvFunction levelFunction = new IntersectionLvFunction();
        levelFunction.register(Arrays.asList(left, right));

        BisectionEdgeZeroPointSolver solver = new BisectionEdgeZeroPointSolver();
        solver.setLevelFunction(levelFunction);
        marching.setZeroPointSolver(solver);
        marching.setLevelFunction(levelFunction);
        Set<MFCell> triangleSet = Arrays.stream(gridTriangle).flatMap(Arrays::stream).collect(Collectors.toSet());
        for (MFCell fc : toBeFissionedFirst) {
            triangleSet.remove(fc);
            triangleSet.addAll(fissionizer.recursivelyFussion(fc).getNewCells());
        }
        Collection<MFEdge> contourHeads = marching.buildContour(triangleSet);
        assertEquals(2, contourHeads.size());

        for (MFEdge head : contourHeads) {
            for (MFEdge edge : head.iterable(MFEdge.class)) {
                double len = distance(edge.getStartCoord(), edge.getEndCoord());
                assertTrue(len > 0);
                double leftDistance = distance(edge.getStartCoord(), leftCenter);
                double rightDistance = distance(edge.getStartCoord(), rightCenter);
                double actDistance = min(leftDistance, rightDistance);
                assertEquals(radius, actDistance, 1e-5);
            }
        }
    }

}
