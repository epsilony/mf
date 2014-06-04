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
package net.epsilony.mf.opt.integrate;

import static org.apache.commons.math3.util.FastMath.PI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import net.epsilony.mf.implicit.contour.BisectionEdgeZeroPointSolver;
import net.epsilony.mf.implicit.contour.TriangleMarching;
import net.epsilony.mf.implicit.level.CircleLvFunction;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFCell;
import net.epsilony.mf.model.geom.SimpMFEdge;
import net.epsilony.mf.model.geom.util.TriangleGridFactory;
import net.epsilony.mf.model.geom.util.TriangleGridFactory.TriangleGrid;
import net.epsilony.tb.analysis.Math2D;

import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleMarchingIntegralUnitsFactoryTest {

    public static Logger logger = LoggerFactory.getLogger(TriangleMarchingIntegralUnitsFactoryTest.class);

    @Test
    public void testByCircle() {
        TriangleGridFactory cellFactory = new TriangleGridFactory();
        cellFactory.setEdgeFactory(SimpMFEdge::new);
        cellFactory.setNodeFactory(MFNode::new);
        cellFactory.setTriangleFactory(() -> new SimpMFCell(3));
        cellFactory.setUpLeft(-1, 1);
        cellFactory.setTriangleWidthHeight(1, 1);
        cellFactory.setNumRowsCols(20, 40);

        TriangleGrid triangleGrid = cellFactory.get();

        List<MFCell> cells = Arrays.stream(triangleGrid.getTriangles()).flatMap(Arrays::stream)
                .collect(Collectors.toList());

        double[] circleCenter = { 1 + 10, -1 - 10 };
        double[] radius = { 5, 6, 7 };

        ParameterizedCircle levelFunction = new ParameterizedCircle();

        TriangleMarchingIntegralUnitsFactory factory = initFactory(cells, levelFunction);

        // TriangleMarchingIntegralUnitsFactory cachedFactory =
        // initCachedFactory(cells, cachedLevelFunction);

        for (double r : radius) {
            double[] parameters = { circleCenter[0], circleCenter[1], r };
            double expContourLength = 2 * PI * r;
            double contourLenthError = expContourLength * 0.01;
            assertFactory(levelFunction, levelFunction::setParameters, factory, parameters, expContourLength,
                    contourLenthError);
            // assertFactory(cachedLevelFunction, cachedFactory, parameters,
            // expContourLength, contourLenthError);
        }
    }

    private void assertFactory(ToDoubleFunction<double[]> levelFunction,
            Consumer<double[]> levelFunctionParametersSetter, TriangleMarchingIntegralUnitsFactory factory,
            double[] parameters, double expContourLength, double contourLenthError) {
        levelFunctionParametersSetter.accept(parameters);
        factory.generateUnits();
        List<MFLine> boundaryUnits = factory.boundaryUnits();

        double contourLen = 0;
        for (MFLine line : boundaryUnits) {
            contourLen += line.length();
        }
        assertEquals(expContourLength, contourLen, contourLenthError);

        List<double[]> boundaryCoord = boundaryUnits.stream().map(MFLine::getStartCoord).collect(Collectors.toList());
        double expArea = Math2D.area(boundaryCoord);

        List<PolygonIntegrateUnit> volumeUnits = factory.volumeUnits();
        double actArea = 0;
        for (PolygonIntegrateUnit volUnit : volumeUnits) {
            double calcArea = volUnit.calcArea();
            assertTrue(calcArea > 0);
            actArea += calcArea;
        }

        assertEquals(expArea, actArea, 1e-14);
    }

    private TriangleMarchingIntegralUnitsFactory initFactory(List<MFCell> cells,
            ToDoubleFunction<double[]> levelFunction) {

        BisectionEdgeZeroPointSolver zeroPointSolver = new BisectionEdgeZeroPointSolver();
        zeroPointSolver.setLevelFunction(levelFunction);
        TriangleMarching triangleMarching = new TriangleMarching();
        triangleMarching.setEdgeFactory(SimpMFEdge::new);
        triangleMarching.setNodeFactory(MFNode::new);
        triangleMarching.setLevelFunction(levelFunction);
        triangleMarching.setZeroPointSolver(zeroPointSolver);

        TriangleMarchingIntegralUnitsFactory factory = new TriangleMarchingIntegralUnitsFactory();
        factory.setTriangleMarching(triangleMarching);
        factory.setCells(cells);
        factory.setLevelFunction(levelFunction);

        return factory;
    }

    // private TriangleMarchingIntegralUnitsFactory
    // initCachedFactory(List<MFCell> cells,
    // CachedParameterizedToDoubleFunction<double[]> cachedLevelFunction) {
    //
    // BisectionEdgeZeroPointSolver zeroPointSolver = new
    // BisectionEdgeZeroPointSolver();
    // zeroPointSolver.setLevelFunction(cachedLevelFunction.getOriginFunction());
    //
    // TriangleMarching triangleMarching = new TriangleMarching();
    // triangleMarching.setEdgeFactory(SimpMFEdge::new);
    // triangleMarching.setNodeFactory(MFNode::new);
    // triangleMarching.setLevelFunction(cachedLevelFunction);
    // triangleMarching.setZeroPointSolver(zeroPointSolver);
    //
    // TriangleMarchingIntegralUnitsFactory factory = new
    // TriangleMarchingIntegralUnitsFactory();
    // factory.setTriangleMarching(triangleMarching);
    // factory.setCells(cells);
    // factory.setLevelFunction(cachedLevelFunction);
    //
    // return factory;
    // }

    public static class ParameterizedCircle implements ToDoubleFunction<double[]> {

        private double[]               parameters;
        private final CircleLvFunction circleLvFunction = new CircleLvFunction();
        {
            circleLvFunction.setHole(false);
        }

        @Override
        public double applyAsDouble(double[] coord) {
            double[] center = { parameters[0], parameters[1] };
            double radius = parameters[2];
            circleLvFunction.setCenter(center);
            circleLvFunction.setRadius(radius);
            return circleLvFunction.applyAsDouble(coord);
        }

        public void setParameters(double[] parameters) {
            this.parameters = parameters;
        }

    }

    @Test
    public void testBySingleZeroTriangle() {
        TriangleGridFactory cellFactory = new TriangleGridFactory();
        cellFactory.setEdgeFactory(SimpMFEdge::new);
        cellFactory.setNodeFactory(MFNode::new);
        cellFactory.setTriangleFactory(() -> new SimpMFCell(3));
        cellFactory.setUpLeft(-1, 1);
        cellFactory.setTriangleWidthHeight(1, 1);
        cellFactory.setNumRowsCols(20, 40);

        TriangleGrid triangleGrid = cellFactory.get();

        List<MFCell> cells = Arrays.stream(triangleGrid.getTriangles()).flatMap(Arrays::stream)
                .collect(Collectors.toList());

        ZeroOnVertexOtherPositive levelFunction = new ZeroOnVertexOtherPositive(triangleGrid.getTriangles()[10][20]);
        TriangleMarchingIntegralUnitsFactory factory = initFactory(cells, levelFunction);

        assertFactory(levelFunction, levelFunction::setParameters, factory, null, 1 + FastMath.sqrt(1 + 0.25) * 2,
                1e-14);

    }

    public static class ZeroOnVertexOtherPositive implements ToDoubleFunction<double[]> {
        private final MFCell cell;

        @Override
        public double applyAsDouble(double[] coord) {
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

        public void setParameters(double[] parameters) {

        }

    }

}
