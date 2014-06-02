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
package net.epsilony.mf.model;

import static org.apache.commons.math3.util.FastMath.ceil;
import static org.apache.commons.math3.util.FastMath.floor;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFRectangleGrid {
    private MFRectangle range;
    private int         numRows, numCols;

    public boolean isInside(boolean restrictly, double[] coord) {
        return range.isInside(restrictly, coord);
    }

    public double cellArea() {
        return range.getWidth() * range.getHeight() / (numCols * numRows);
    }

    public double cellWidth() {
        return range.getWidth() / numCols;
    }

    public double cellHeight() {
        return range.getHeight() / numRows;
    }

    public int col(double x) {
        if (x < range.getLeft() || x > range.getRight()) {
            throw new IllegalArgumentException();
        }
        int col = (int) floor((x - range.getLeft()) / (range.getWidth() / numCols));
        if (col == numCols) {
            col = numCols - 1;
        }
        return col;
    }

    public int row(double y) {
        if (y > range.getUp() || y < range.getDown()) {
            throw new IllegalArgumentException();
        }
        int row = (int) floor((range.getUp() - y) / (range.getHeight() / numRows));
        if (row == numRows) {
            row = numRows - 1;
        }
        return row;
    }

    public double cellLeft(int col) {
        if (col < 0 || col >= numCols) {
            throw new IllegalArgumentException("numCols: " + numCols + ", but col: " + col);
        }
        if (col == 0) {
            return range.getLeft();
        } else {
            return range.getLeft() + (range.getWidth() * col) / numCols;
        }
    }

    public double cellRight(int col) {
        if (col < 0 || col >= numCols) {
            throw new IllegalArgumentException("numCols: " + numCols + ", but col: " + col);
        }
        if (col == numCols - 1) {
            return range.getRight();
        } else {
            return range.getLeft() + (range.getWidth() * (col + 1)) / numCols;
        }
    }

    public double cellUp(int row) {
        if (row < 0 || row >= numRows) {
            throw new IllegalArgumentException("numRows: " + numRows + ", but row: " + row);
        }
        if (row == 0) {
            return range.getUp();
        } else {
            return range.getUp() - (range.getHeight() * row) / numRows;
        }
    }

    public double cellDown(int row) {
        if (row < 0 || row >= numRows) {
            throw new IllegalArgumentException("numRows: " + numRows + ", but row: " + row);
        }
        if (row == numRows - 1) {
            return range.getDown();
        } else {
            return range.getUp() - (range.getHeight() * (row + 1)) / numRows;
        }
    }

    public MFRectangle getRange() {
        return range;
    }

    public void setRange(MFRectangle gridRange) {
        gridRange.checkRectangleParameters();
        this.range = gridRange;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        if (numRows < 1) {
            throw new IllegalArgumentException();
        }
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumCols(int numCols) {
        if (numCols < 1) {
            throw new IllegalArgumentException();
        }
        this.numCols = numCols;
    }

    public static MFRectangleGrid byRangeCellSizeUpperBound(MFRectangle range, double cellSizeUpperBound) {
        int numRows = (int) ceil(range.getHeight() / cellSizeUpperBound);
        int numCols = (int) ceil(range.getWidth() / cellSizeUpperBound);

        MFRectangleGrid grid = new MFRectangleGrid();
        grid.setNumRows(numRows);
        grid.setNumCols(numCols);
        grid.setRange(range);
        return grid;
    }

}
