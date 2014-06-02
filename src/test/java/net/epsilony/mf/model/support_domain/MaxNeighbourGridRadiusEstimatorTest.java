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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.MFRectangleGrid;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MaxNeighbourGridRadiusEstimatorTest {

    @Test
    public void test() {
        MFRectangle range = new MFRectangle(-1, 5, 3, -1);
        int numRows = 4;
        int numCols = 6;

        MFRectangleGrid grid = new MFRectangleGrid();
        grid.setRange(range);
        grid.setNumCols(numCols);
        grid.setNumRows(numRows);

        double[][] centers = { { -0.5, -0.5 }, { 1.5, 1.5 }, { 3.5, -0.5 } };
        double[] radius = { 2, 1, 3 };
        ArrayList<double[][]> expRadius = new ArrayList<>();
        expRadius.add(new double[][] {
                { 0, 0, 0, 0, 0, 0 },
                { 2, 2, 0, 0, 0, 0 },
                { 2, 2, 2, 0, 0, 0 },
                { 2, 2, 2, 0, 0, 0 } });
        expRadius.add(new double[][] {
                { 0, 1, 1, 1, 0, 0 },
                { 2, 2, 1, 1, 0, 0 },
                { 2, 2, 2, 1, 0, 0 },
                { 2, 2, 2, 0, 0, 0 }, });
        expRadius.add(new double[][] {
                { 0, 1, 3, 3, 3, 3 },
                { 2, 3, 3, 3, 3, 3 },
                { 2, 3, 3, 3, 3, 3 },
                { 2, 3, 3, 3, 3, 3 }, });

        MaxNeighbourGridRadiusEstimator estimator = new MaxNeighbourGridRadiusEstimator();
        estimator.setGrid(grid);
        for (int i = 0; i < centers.length; i++) {
            double[] center = centers[i];
            double r = radius[i];
            double[][] exps = expRadius.get(i);
            MFNode node = new MFNode(center);
            node.setInfluenceRadius(r);

            estimator.updateByNode(node);

            assertEstimator(estimator, exps);
        }
    }

    private void assertEstimator(MaxNeighbourGridRadiusEstimator estimator, double[][] exps) {
        Field[] allFields = FieldUtils.getAllFields(MaxNeighbourGridRadiusEstimator.class);
        double[][] actRadius = null;
        for (Field field : allFields) {
            if (field.getName().equals("maxInfluenceRadius")) {
                try {
                    field.setAccessible(true);
                    actRadius = (double[][]) field.get(estimator);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        assertEquals(exps.length, actRadius.length);
        for (int i = 0; i < exps.length; i++) {
            double[] exp = exps[i];
            double[] act = actRadius[i];
            assertArrayEquals(exp, act, 0);
        }
    }
}
