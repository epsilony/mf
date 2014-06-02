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
package net.epsilony.mf.model.support_domain;

import static org.apache.commons.math3.util.FastMath.sqrt;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.MFRectangleGrid;
import net.epsilony.tb.analysis.Math2D;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MaxNeighbourGridRadiusEstimator {

    private double[][]      maxInfluenceRadius;
    private MFRectangleGrid grid;

    public double estimate(double[] coord) {
        int row = grid.row(coord[1]);
        int col = grid.col(coord[0]);
        return maxInfluenceRadius[row][col];
    }

    public void updateByNode(MFNode node) {
        double[] center = node.getCoord();
        double radius = node.getInfluenceRadius();
        double cx = center[0];
        MFRectangle range = grid.getRange();
        int left = cx - radius < range.getLeft() ? 0 : grid.col(cx - radius);
        int right = cx + radius > range.getRight() ? grid.getNumCols() - 1 : grid.col(cx + radius);
        double cy = center[1];
        int up = cy + radius > range.getUp() ? 0 : grid.row(cy + radius);
        int down = cy - radius < range.getDown() ? grid.getNumRows() - 1 : grid.row(cy - radius);

        double cellWidth = grid.cellWidth();
        double cellHeight = grid.cellHeight();
        double cellRadius = sqrt(cellWidth * cellWidth + cellHeight * cellHeight) / 2;

        for (int row = up; row <= down; row++) {
            for (int col = left; col <= right; col++) {
                double x = (grid.cellLeft(col) + grid.cellRight(col)) / 2;
                double y = (grid.cellUp(row) + grid.cellDown(row)) / 2;
                double distanceSq = Math2D.distanceSquare(x, y, center[0], center[1]);
                double d = radius + cellRadius;
                d = d * d;
                if (distanceSq < d) {
                    double oriRadius = maxInfluenceRadius[row][col];
                    if (oriRadius < radius) {
                        maxInfluenceRadius[row][col] = radius;
                    }
                }
            }
        }
    }

    public MFRectangleGrid getGrid() {
        return grid;
    }

    public void setGrid(MFRectangleGrid grid) {
        maxInfluenceRadius = new double[grid.getNumRows()][grid.getNumCols()];
        this.grid = grid;
    }

}
