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
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.epsilony.mf.model.MFNode;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NodesDensityGridRadiusEstimatorTest {

    List<MFNode>                    nodes;
    NodesDensityGridRadiusEstimator estimator;
    private int                     expNumRows;
    private int                     expNumCols;
    private double[][]              diagCorners;
    private double[][]              centerCellCoords;
    private double                  cellArea;

    @Test
    public void test() {
        expNumCols = 3;
        expNumRows = 5;
        diagCorners = new double[][] { { -1, -1 }, { 2, 4 } };
        centerCellCoords = new double[][] { { 0.5, 1.5 }, { 0.25, 1.25 }, { 0.75, 1.75 } };
        ArrayList<double[]> nodesCoords = Lists.newArrayList(diagCorners);
        nodesCoords.addAll(Arrays.asList(centerCellCoords));
        nodes = nodesCoords.stream().map(MFNode::new).collect(Collectors.toList());

        cellArea = 1;
        estimator = NodesDensityGridRadiusEstimator.fromNodes(nodes, 1);

        assertEquals(cellArea * expNumCols * expNumRows / nodes.size(), estimator.getDefaultNodesDensity(), 1e-14);
        assertEquals(expNumCols, estimator.getGrid().getNumCols());
        assertEquals(expNumRows, estimator.getGrid().getNumRows());

        int aimNum = 10;
        estimator.setNodesNumAim(aimNum);

        // a blank grid
        estimator.setCenter(new double[] { -0.9, 3.9 });

        double act = estimator.estimate();
        double exp = sqrt(aimNum / (PI * estimator.getDefaultNodesDensity()))
                * NodesDensityGridRadiusEstimator.DEFAULT_RESULT_ENLARGE_RATIO;
        assertEquals(exp, act, 1e-14);

        // center of center cell
        estimator.setCenter(new double[] { 0.5, 1.5 });
        act = estimator.estimate();
        exp = sqrt(cellArea / PI * aimNum / 3.0) * NodesDensityGridRadiusEstimator.DEFAULT_RESULT_ENLARGE_RATIO;
        assertEquals(exp, act, 1e-14);

        // feed back
        double feedBack_radis = 3;
        int feedBack_nodes = 120;
        double feedBack_desity = feedBack_nodes / (PI * feedBack_radis * feedBack_radis);

        estimator.feedBack(feedBack_radis, feedBack_nodes);
        act = estimator.estimate();
        exp = sqrt(aimNum / (PI * feedBack_desity)) * NodesDensityGridRadiusEstimator.DEFAULT_RESULT_ENLARGE_RATIO;
        assertEquals(exp, act, 1e-14);

    }
}
