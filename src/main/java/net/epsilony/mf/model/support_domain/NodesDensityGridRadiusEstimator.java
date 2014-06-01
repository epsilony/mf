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

import static org.apache.commons.math3.util.FastMath.PI;
import static org.apache.commons.math3.util.FastMath.sqrt;

import java.util.Collection;

import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.MFRectangleGrid;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NodesDensityGridRadiusEstimator implements NodesNumRadiusEstimator {
    public final static double DEFAULT_RESULT_ENLARGE_RATIO = EnsureNodesAutoSupportDomainSearcher.DEFAULT_RESULT_ENLARGE_RATIO * 1.05;
    private double[] center;

    private int nodesNumAim;

    private double[][] estimateCellNodesNum;

    private MFRectangleGrid grid;

    private double resultEnlargeRatio = DEFAULT_RESULT_ENLARGE_RATIO;
    private double defaultNodesDensity = 0.01;

    @Override
    public void setCenter(double[] center) {
        this.center = center;
    }

    @Override
    public void setNodesNumAim(int nodesNumAim) {
        this.nodesNumAim = nodesNumAim;
    }

    @Override
    public double estimate() {
        double cellArea = grid.cellArea();
        double cellNodesNum;
        if (!grid.isInside(false, center)) {
            cellNodesNum = cellArea * defaultNodesDensity;
            return estimateByCellNodesNum(cellNodesNum);
        }

        int row = grid.row(center[1]);
        int col = grid.col(center[0]);

        cellNodesNum = estimateCellNodesNum[row][col];
        if (cellNodesNum <= 0) {
            cellNodesNum = cellArea * defaultNodesDensity;
        }

        return estimateByCellNodesNum(cellNodesNum);
    }

    private double estimateByCellNodesNum(double cellNodesNum) {
        return sqrt(grid.cellArea() / PI * nodesNumAim / cellNodesNum) * resultEnlargeRatio;
    }

    @Override
    public void feedBack(double radius, int nodesNum) {
        if (radius <= 0 || nodesNum < 1) {
            throw new IllegalArgumentException();
        }

        if (!grid.isInside(false, center)) {
            return;
        }

        int row = grid.row(center[1]);
        int col = grid.col(center[0]);

        double newNum = nodesNum * grid.cellArea() / (PI * radius * radius);
        double oriNum = estimateCellNodesNum[row][col];
        if (newNum > oriNum) {
            estimateCellNodesNum[row][col] = newNum;
        }
    }

    public double getDefaultNodesDensity() {
        return defaultNodesDensity;
    }

    public void setDefaultNodesDensity(double defaultNodesDensity) {
        if (defaultNodesDensity <= 0) {
            throw new IllegalArgumentException();
        }
        this.defaultNodesDensity = defaultNodesDensity;
    }

    public double getResultEnlargeRatio() {
        return resultEnlargeRatio;
    }

    public void setResultEnlargeRatio(double resultEnlargeRatio) {
        this.resultEnlargeRatio = resultEnlargeRatio;
    }

    public MFRectangleGrid getGrid() {
        return grid;
    }

    public void setGrid(MFRectangleGrid grid) {
        estimateCellNodesNum = new double[grid.getNumRows()][grid.getNumCols()];
        this.grid = grid;
    }

    public static NodesDensityGridRadiusEstimator fromNodes(Collection<? extends Node> nodes, int cellSizeUpperBound,
            MFRectangle nodesRange) {

        NodesDensityGridRadiusEstimator result = new NodesDensityGridRadiusEstimator();

        MFRectangleGrid grid = MFRectangleGrid.byRangeCellSizeUpperBound(nodesRange, cellSizeUpperBound);
        result.setGrid(grid);

        result.setDefaultNodesDensity(nodesRange.getArea() / nodes.size());

        for (Node node : nodes) {
            double[] coord = node.getCoord();
            int col = grid.col(coord[0]);
            int row = grid.row(coord[1]);
            result.estimateCellNodesNum[row][col]++;
        }

        return result;

    }

    public static NodesDensityGridRadiusEstimator fromNodes(Collection<? extends Node> nodes, int cellSizeUpperBound) {
        return fromNodes(nodes, cellSizeUpperBound, MFRectangle.coordsRange(nodes.stream().map(Node::getCoord)));
    }
}
