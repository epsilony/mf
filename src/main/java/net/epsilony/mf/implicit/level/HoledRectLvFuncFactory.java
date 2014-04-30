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
package net.epsilony.mf.implicit.level;

import static org.apache.commons.math3.util.FastMath.floor;
import static org.apache.commons.math3.util.FastMath.sqrt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.model.MFHole;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.geom.util.MFFacetFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public abstract class HoledRectLvFuncFactory implements Supplier<ToDoubleFunction<double[]>> {

    protected MFRectangle rectangle;

    abstract public Collection<? extends MFHole> getHoles();

    private Predicate<MFHole> holeFilter;

    public void setHoleFilter(Predicate<MFHole> holeFilter) {
        this.holeFilter = holeFilter;
    }

    @Override
    public ToDoubleFunction<double[]> get() {
        final MFFacet facet = new MFFacetFactory(SimpMFLine::new, MFNode::new).produceBySingleChain(rectangle
                .vertesCoords());
        IntersectionLvFunction result = new IntersectionLvFunction();
        result.register(facet::distanceFunction);
        for (MFHole h : getHoles()) {
            if (null != holeFilter) {
                if (!holeFilter.test(h)) {
                    continue;
                }
            }
            result.register(h.distanceFunction());
        }
        return result;
    }

    public MFRectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(MFRectangle rectangle) {
        this.rectangle = rectangle;
    }

    public HoledRectLvFuncFactory(MFRectangle rectangle) {
        this.rectangle = rectangle;
    }

    public HoledRectLvFuncFactory() {
    }

    public static class Grid extends HoledRectLvFuncFactory {

        public Grid() {
        }

        public Grid(MFRectangle rectangle, double holeRadius, double holeDistanceInf) {
            super(rectangle);
            this.holeRadius = holeRadius;
            this.holeDistanceInf = holeDistanceInf;
        }

        private double holeRadius;
        private double holeDistanceInf;

        public double getHoleRadius() {
            return holeRadius;
        }

        public void setHoldRadiusInf(double holdRadiusInf) {
            this.holeRadius = holdRadiusInf;
        }

        public double getHoleDistanceInf() {
            return holeDistanceInf;
        }

        public void setHoldDistanceInf(double holdDistanceInf) {
            this.holeDistanceInf = holdDistanceInf;
        }

        @Override
        public Collection<? extends MFHole> getHoles() {
            final double width = rectangle.getWidth();
            final double height = rectangle.getHeight();
            final double r = holeDistanceInf / 2 + holeRadius;
            final int numCols = (int) floor(width / r / 2);
            final int numRows = (int) floor(height / r / 2);
            ArrayList<MFHole> result = new ArrayList<>(numCols * numRows);
            double dx = width / (numCols - 1);
            double dy = height / (numRows - 1);
            if (numCols < 2 || numRows < 2) {
                throw new IllegalStateException();
            }
            for (int row = 0; row < numRows; row++) {
                double y = (row == numRows - 1) ? rectangle.getDown() : rectangle.getUp() - dy * row;
                for (int col = 0; col < numCols; col++) {
                    double x = (col == numCols - 1) ? rectangle.getRight() : rectangle.getLeft() + dx * col;
                    result.add(new MFHole(new double[] { x, y }, holeRadius));
                }
            }
            return result;
        }

    }

    public static class Cheese extends HoledRectLvFuncFactory {
        private double holeRadius;
        private double holeDistanceInf;

        public double getHoleRadius() {
            return holeRadius;
        }

        public void setHoldRadiusInf(double holdRadiusInf) {
            this.holeRadius = holdRadiusInf;
        }

        public double getHoleDistanceInf() {
            return holeDistanceInf;
        }

        public void setHoldDistanceInf(double holdDistanceInf) {
            this.holeDistanceInf = holdDistanceInf;
        }

        public Cheese(MFRectangle rectangle, double holeRadius, double holeDistanceInf) {
            super(rectangle);
            this.holeRadius = holeRadius;
            this.holeDistanceInf = holeDistanceInf;
        }

        public Cheese() {
        }

        @Override
        public ArrayList<MFHole> getHoles() {
            final double width = rectangle.getWidth();
            final double height = rectangle.getHeight();
            double r = holeDistanceInf / 2 + holeRadius;
            int numCols;
            int numRows;
            double dx;
            double dy;
            double x00;
            double y00;
            double x10;
            double y10;
            if (width >= height) {
                numCols = (int) floor(width / (sqrt(3) * r)) + 1;
                numRows = (int) floor(height / (2 * r)) + 1;
                if (numCols < 2 || numRows < 2) {
                    throw new IllegalStateException();
                }
                dx = width / (numCols - 1);
                dy = height / (numRows - 1);
                x00 = rectangle.getLeft() + dx;
                y00 = rectangle.getUp();
                x10 = rectangle.getLeft();
                y10 = rectangle.getUp() - dy;
            } else {
                numRows = (int) floor(width / (sqrt(3) * r)) + 1;
                numCols = (int) floor(height / (2 * r)) + 1;
                if (numCols < 2 || numRows < 2) {
                    throw new IllegalStateException();
                }
                dx = width / (numCols - 1);
                dy = height / (numRows - 1);
                x00 = rectangle.getLeft() + dx;
                y00 = rectangle.getUp();
                x10 = rectangle.getLeft();
                y10 = rectangle.getUp() - dy;
            }
            ArrayList<MFHole> result = new ArrayList<>(numCols * numRows);

            for (int i = 0; i < numRows; i++) {
                double y = i % 2 == 0 ? y00 - i * dy : y10 - (i - 1) * dy;
                if (y < rectangle.getDown() - dy / 2) {
                    continue;
                }
                double x0 = i % 2 == 0 ? x00 : x10;
                for (int j = 0; j < numCols; j++) {
                    double x = x0 + j * dx;
                    if (x > rectangle.getRight() + dx / 2) {
                        continue;
                    }
                    result.add(new MFHole(new double[] { x, y }, holeRadius));
                }
            }

            return result;
        }
    }
}
