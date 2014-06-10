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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.MFRectangleGrid;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Node;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rits.cloning.Cloner;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MaxNeighbourGridRadiusEstimator {

    private double[][]         cellIntersectingMaxInfluenceRadius;
    private MFRectangleGrid    grid;
    private double             maxInfluenceRadius;
    public static final Logger logger = LoggerFactory.getLogger(MaxNeighbourGridRadiusEstimator.class);

    public double estimate(double[] coord) {
        if (!grid.isInside(false, coord)) {
            return maxInfluenceRadius;
        }
        int row = grid.row(coord[1]);
        int col = grid.col(coord[0]);
        return cellIntersectingMaxInfluenceRadius[row][col];
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
                    double oriRadius = cellIntersectingMaxInfluenceRadius[row][col];
                    if (oriRadius < radius) {
                        cellIntersectingMaxInfluenceRadius[row][col] = radius;
                    }
                }
            }
        }
    }

    public MFRectangleGrid getGrid() {
        return grid;
    }

    public void setGrid(MFRectangleGrid grid) {
        cellIntersectingMaxInfluenceRadius = new double[grid.getNumRows()][grid.getNumCols()];
        this.grid = grid;
        maxInfluenceRadius = 0;
    }

    public double[][] getCellIntersectingMaxInfluenceRadius() {
        return cellIntersectingMaxInfluenceRadius;
    }

    public void setCellIntersectingMaxInfluenceRadius(double[][] cellIntersectingMaxInfluenceRadius) {
        this.cellIntersectingMaxInfluenceRadius = cellIntersectingMaxInfluenceRadius;
    }

    public double getMaxInfluenceRadius() {
        return maxInfluenceRadius;
    }

    public void setMaxInfluenceRadius(double maxInfluenceRadius) {
        this.maxInfluenceRadius = maxInfluenceRadius;
    }

    public void setup(MaxNeighbourGridRadiusEstimator src) {
        Cloner cloner = new Cloner();
        MaxNeighbourGridRadiusEstimator deepClone = cloner.deepClone(src);
        try {
            BeanUtils.copyProperties(this, deepClone);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public static MaxNeighbourGridRadiusEstimator newInstance(Collection<? extends MFNode> nodes,
            double cellSizeUpperBound) {
        MFRectangle nodesRange = MFRectangle.coordsRange(nodes.stream().map(Node::getCoord));
        MFRectangleGrid grid = MFRectangleGrid.byRangeCellSizeUpperBound(nodesRange, cellSizeUpperBound);

        MaxNeighbourGridRadiusEstimator result = new MaxNeighbourGridRadiusEstimator();
        result.setGrid(grid);
        for (MFNode node : nodes) {
            result.updateByNode(node);
        }

        result.setMaxInfluenceRadius(Arrays.stream(result.cellIntersectingMaxInfluenceRadius)
                .flatMapToDouble(Arrays::stream).max().getAsDouble());

        return result;
    }
}
