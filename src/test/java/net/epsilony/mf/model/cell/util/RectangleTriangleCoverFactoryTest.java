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

import static org.apache.commons.math3.util.FastMath.max;
import static org.apache.commons.math3.util.FastMath.min;
import static org.apache.commons.math3.util.MathArrays.distance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.cell.MFCell;
import net.epsilony.mf.model.cell.MFEdge;
import net.epsilony.mf.model.cell.SimpMFCell;
import net.epsilony.mf.model.cell.SimpMFEdge;
import net.epsilony.mf.model.cell.util.RectangleTriangleCoverFactory.TriangleCover;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Node;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RectangleTriangleCoverFactoryTest {

    MFRectangle rectangle;
    double triangleWidth, triangleHeight;
    private RectangleTriangleCoverFactory factory;
    private List<MFCell> cells;
    private TriangleCover triangleCover;

    @Test
    public void test() {
        initSample();
        triangleCover = factory.get();
        MFCell[][] mfCells = triangleCover.getTriangles();
        cells = Arrays.stream(mfCells).flatMap(Arrays::stream).collect(Collectors.toList());

        assertTrue(cells.size() > 1);

        checkTriangleSizes();

        checkCellEdgeConnection();

        checkBoundaryCellIsNotAbused();

    }

    private void checkTriangleSizes() {
        for (MFCell cell : cells) {
            double[] v0 = cell.getVertexCoord(0);
            double[] v1 = cell.getVertexCoord(1);
            double[] v2 = cell.getVertexCoord(2);

            assertEquals(v0[1], v1[1], 0);
            assertEquals(triangleWidth, distance(v0, v1), triangleWidth * 1e-14);
            assertEquals(triangleHeight, Math2D.area(new double[][] { v0, v1, v2 }) * 2 / triangleHeight,
                    triangleHeight * 1e-14);
            assertEquals(0.5, Math2D.projectionParameter(v0, v1, v2), 1e-14);
        }
    }

    private void checkBoundaryCellIsNotAbused() {
        final Predicate<? super MFCell> bndPrd = cell -> {
            boolean bnd = false;
            for (MFEdge edge : cell.getVertexEdge(0).iterable(MFEdge.class)) {
                if (edge.getOpposite() == null) {
                    bnd = true;
                    break;
                }
            }
            return bnd;
        };

        List<MFCell> bndCells = cells.stream().filter(bndPrd).collect(Collectors.toList());

        for (MFCell cell : bndCells) {
            double left = Double.POSITIVE_INFINITY, right = Double.NEGATIVE_INFINITY, up = Double.NEGATIVE_INFINITY, down = Double.POSITIVE_INFINITY;
            for (MFEdge edge : cell.getVertexEdge(0).iterable(MFEdge.class)) {
                double[] vertexCoord = edge.getStartCoord();
                double x = vertexCoord[0];
                double y = vertexCoord[1];
                left = min(x, left);
                right = max(x, right);
                up = max(y, up);
                down = min(y, down);
            }
            assertTrue(rectangle.getLeft() - left <= triangleWidth);
            assertTrue(right - rectangle.getRight() <= triangleWidth);
            assertTrue(up - rectangle.getUp() <= triangleHeight / 2);
            assertTrue(rectangle.getDown() - down <= triangleHeight);
        }
    }

    private void checkCellEdgeConnection() {
        cells.forEach(cell -> {
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
        });
    }

    private void initSample() {
        rectangle = new MFRectangle();
        rectangle.setDrul(new double[] { 1, 5, 3, -1 });
        triangleWidth = 0.25;
        triangleHeight = 0.25;

        factory = new RectangleTriangleCoverFactory();
        factory.setRectangle(rectangle);
        factory.setNodeFactory(Node::new);
        factory.setTriangleFactory(() -> new SimpMFCell(3));
        factory.setEdgeFactory(SimpMFEdge::new);
        factory.setTriangleWidthHeight(triangleWidth, triangleHeight);

        triangleCover = factory.get();
    }

}
