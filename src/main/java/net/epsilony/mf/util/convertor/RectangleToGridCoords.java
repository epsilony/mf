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
package net.epsilony.mf.util.convertor;

import java.util.ArrayList;
import java.util.Random;

import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.MFRectangleEdge;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public abstract class RectangleToGridCoords implements Convertor<MFRectangle, ArrayList<ArrayList<double[]>>> {
    public static class ByNumRowsCols extends RectangleToGridCoords {

        public void setNumRows(int numRows) {
            if (numRows < 1) {
                throw new IllegalArgumentException();
            }
            if (this.numRows == numRows) {
                return;
            }
            needPrepare = true;
            this.numRows = numRows;
        }

        public void setNumCols(int numCols) {
            if (numCols < 1) {
                throw new IllegalArgumentException();
            }
            if (this.numCols == numCols) {
                return;
            }
            needPrepare = true;
            this.numCols = numCols;
        }

        @Override
        protected void genNumRowsCols() {
        }
    }

    public static class BySizeSup extends RectangleToGridCoords {
        double stepSup = -1;

        public double getStepSup() {
            return stepSup;
        }

        public void setStepSup(double sizeSup) {
            if (sizeSup <= 0) {
                throw new IllegalArgumentException();
            }
            if (this.stepSup == sizeSup) {
                return;
            }
            needPrepare = true;
            this.stepSup = sizeSup;
        }

        @Override
        protected void genNumRowsCols() {
            if (stepSup <= 0) {
                throw new IllegalStateException();
            }
            numCols = (int) Math.ceil(rectangle.getWidth() / stepSup) + 1;
            numRows = (int) Math.ceil(rectangle.getHeight() / stepSup) + 1;
        }

        public BySizeSup(double stepSup) {
            setStepSup(stepSup);
        }

        public BySizeSup() {
        }

    }

    protected MFRectangle rectangle;
    protected int numRows, numCols;
    private double stepX, stepY;
    protected boolean needPrepare = true;
    private double disturbRatio = 0;
    private Random disturbRandom = null;

    @Override
    public ArrayList<ArrayList<double[]>> convert(MFRectangle input) {
        setRectangle(input);
        ArrayList<ArrayList<double[]>> coordGrids = genCoordGrid();
        disturbCoordGrids(coordGrids);
        return coordGrids;
    }

    public ArrayList<ArrayList<double[]>> genCoordGrid() {
        prepare();
        ArrayList<ArrayList<double[]>> result = new ArrayList<>(numRows);
        for (int i = 0; i < numRows; i++) {
            ArrayList<double[]> coordsRow = new ArrayList<>(numCols);
            result.add(coordsRow);
            double y = getY(i);
            for (int j = 0; j < numCols; j++) {
                double x = getX(j);
                coordsRow.add(new double[] { x, y });
            }
        }
        return result;
    }

    private void disturbCoordGrids(ArrayList<ArrayList<double[]>> coordGrids) {
        if (0 == disturbRatio) {
            return;
        }
        if (null == disturbRandom) {
            disturbRandom = new Random();
        }
        for (int i = 0; i < numRows; i++) {
            ArrayList<double[]> row = coordGrids.get(i);
            for (int j = 0; j < numCols; j++) {
                double xDisturb = getRandomDisturb(getXDisturbRange(i, j));
                double yDisturb = getRandomDisturb(getYDisturbRange(i, j));
                double[] coord = row.get(j);
                coord[0] += xDisturb * disturbRatio;
                coord[0] += yDisturb * disturbRatio;
            }
        }
    }

    private double[] getXDisturbRange(int i, int j) {
        if (i < 0 || j < 0 || i >= numRows || j >= numCols) {
            throw new IllegalArgumentException();
        }
        if (j == 0 || j == numCols - 1) {
            return null;
        } else {
            return new double[] { -stepX / 2, stepX / 2 };
        }
    }

    private double[] getYDisturbRange(int i, int j) {
        if (i < 0 || j < 0 || i >= numRows || j >= numCols) {
            throw new IllegalArgumentException();
        }
        if (i == 0 || i == numRows - 1) {
            return null;
        } else {
            return new double[] { -stepY / 2, stepY / 2 };
        }
    }

    private double getRandomDisturb(double[] range) {
        if (null == range) {
            return 0;
        }
        double rand = 0;
        while (rand == 0) {
            rand = disturbRandom.nextDouble();
        }
        return range[0] * (1 - rand) + range[1] * rand;
    }

    public MFRectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(MFRectangle rectangle) {
        if (this.rectangle == rectangle) {
            return;
        }
        needPrepare = true;
        this.rectangle = rectangle;
    }

    public int getNumRows() {
        prepare();
        return numRows;
    }

    public int getNumCols() {
        prepare();
        return numCols;
    }

    private void prepare() {
        if (!needPrepare) {
            return;
        }
        genNumRowsCols();
        genXYSteps();
        needPrepare = true;
    }

    public double getStepX() {
        prepare();
        return stepX;
    }

    public double getStepY() {
        prepare();
        return stepY;
    }

    public double getX(int col) {
        if (col < 0 || col >= numCols) {
            throw new IllegalArgumentException();
        }
        prepare();
        return col == numCols - 1 ? rectangle.getEdgePosition(MFRectangleEdge.RIGHT) : rectangle
                .getEdgePosition(MFRectangleEdge.LEFT) + col * stepX;
    }

    public double getY(int row) {
        if (row < 0 || row >= numRows) {
            throw new IllegalArgumentException();
        }
        prepare();
        return row == numRows - 1 ? rectangle.getEdgePosition(MFRectangleEdge.UP) : rectangle
                .getEdgePosition(MFRectangleEdge.DOWN) + row * stepY;
    }

    public double getDisturbRatio() {
        return disturbRatio;
    }

    public void setDisturbRatio(double disturbRatio) {
        if (disturbRatio < 0 || disturbRatio > 1) {
            throw new IllegalArgumentException();
        }
        this.disturbRatio = disturbRatio;
    }

    public Random getDisturbRandom() {
        return disturbRandom;
    }

    public void setDisturbRandom(Random disturbRandom) {
        this.disturbRandom = disturbRandom;
    }

    protected abstract void genNumRowsCols();

    private void genXYSteps() {
        stepX = rectangle.getWidth() / (numCols - 1);
        stepY = rectangle.getHeight() / (numRows - 1);
    }
}