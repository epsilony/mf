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

import static org.apache.commons.math3.util.FastMath.ceil;

import java.util.function.Function;
import java.util.function.Supplier;

import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.model.geom.MFEdge;
import net.epsilony.mf.model.geom.util.RectangleTriangleCoverFactory.TriangleCover;
import net.epsilony.mf.model.geom.util.TriangleGridFactory.TriangleGrid;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RectangleTriangleCoverFactory implements Supplier<TriangleCover> {

    private MFRectangle               rectangle;
    private final TriangleGridFactory gridFactory = new TriangleGridFactory();

    public static class TriangleCover {

        public TriangleCover(MFRectangle rectangle, double triangleWidth, double triangleHeight, MFCell[][] triangles,
                Node[][] nodes) {
            this.rectangle = rectangle;
            this.triangleWidth = triangleWidth;
            this.triangleHeight = triangleHeight;
            this.triangles = triangles;
            this.vertesGrid = nodes;
        }

        public MFRectangle getRectangle() {
            return rectangle;
        }

        public double getTriangleWidth() {
            return triangleWidth;
        }

        public double getTriangleHeight() {
            return triangleHeight;
        }

        public MFCell[][] getTriangles() {
            return triangles;
        }

        public Node[][] getVertesGrid() {
            return vertesGrid;
        }

        private final MFRectangle rectangle;
        private final double      triangleWidth, triangleHeight;
        private final MFCell[][]  triangles;
        private final Node[][]    vertesGrid;

    }

    @Override
    public TriangleCover get() {
        checkSizes();
        gridFactory.setNumRowsCols(triangleNumRows(), triangleNumCols());
        gridFactory.setUpLeft(gridUp(), gridLeft());
        TriangleGrid triangleGrid = gridFactory.get();
        return new TriangleCover(rectangle, getTriangleWidth(), getTriangleHeight(), triangleGrid.getTriangles(),
                triangleGrid.getVertesGrid());
    }

    private double gridLeft() {
        return rectangle.getLeft() - getTriangleWidth();
    }

    private double gridUp() {
        return rectangle.getUp() + getTriangleHeight() / 2;
    }

    private int triangleNumRows() {
        double triangleHeight = getTriangleHeight();
        return 1 + (int) ceil((rectangle.getHeight() - triangleHeight / 2) / triangleHeight);
    }

    private int triangleNumCols() {
        double triangleWidth = getTriangleWidth();
        return (int) ceil(rectangle.getWidth() / (triangleWidth / 2)) + 3;
    }

    private void checkSizes() {
        double triangleWidth = getTriangleWidth();
        double triangleHeight = getTriangleHeight();
        if (rectangle.getWidth() < triangleWidth / 2 || rectangle.getHeight() < triangleHeight / 2) {
            throw new IllegalStateException();
        }
    }

    public MFRectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(MFRectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Function<double[], ? extends Node> getNodeFactory() {
        return gridFactory.getNodeFactory();
    }

    public void setNodeFactory(Function<double[], ? extends Node> nodeFactory) {
        gridFactory.setNodeFactory(nodeFactory);
    }

    public Supplier<? extends MFCell> getTriangleFactory() {
        return gridFactory.getTriangleFactory();
    }

    public void setTriangleFactory(Supplier<? extends MFCell> triangleFactory) {
        gridFactory.setTriangleFactory(triangleFactory);
    }

    public int getNumRows() {
        return gridFactory.getNumRows();
    }

    public int getNumCols() {
        return gridFactory.getNumCols();
    }

    public void setNumRowsCols(int triangleNumRows, int triangleNumCols) {
        gridFactory.setNumRowsCols(triangleNumRows, triangleNumCols);
    }

    public double getTriangleWidth() {
        return gridFactory.getTriangleWidth();
    }

    public double getTriangleHeight() {
        return gridFactory.getTriangleHeight();
    }

    public void setTriangleWidthHeight(double triangleWidth, double triangleHeight) {
        gridFactory.setTriangleWidthHeight(triangleWidth, triangleHeight);
    }

    public Supplier<? extends MFEdge> getEdgeFactory() {
        return gridFactory.getEdgeFactory();
    }

    public void setEdgeFactory(Supplier<? extends MFEdge> edgeFactory) {
        gridFactory.setEdgeFactory(edgeFactory);
    }

}
