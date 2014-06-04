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
package net.epsilony.mf.model.function;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.epsilony.mf.model.function.SingleLineFractionizer.ByAverageNeighbourCoordsDistanceSup;
import net.epsilony.mf.model.function.SingleLineFractionizer.ByNumberOfNewCoords;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Node;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class SingleLineFractionizerTest {

    @Test
    public void testByNumber() {
        int number = 3;
        MFLine line = new SimpMFLine(new Node(0.1, 4));
        MFLine line2 = new SimpMFLine(new Node(-0.1, 8));
        MFLine2DUtils.link(line, line2);
        List<double[]> exps = Lists.newArrayList(new double[] { 0.05, 5 }, new double[] { 0, 6 }, new double[] {
                -0.05,
                7 });

        ByNumberOfNewCoords byNumberOfNewCoords = new SingleLineFractionizer.ByNumberOfNewCoords(number);
        List<double[]> newCoords = byNumberOfNewCoords.apply(line);
        assertEquals(number, newCoords.size());
        for (int i = 0; i < number; i++) {
            double[] exp = exps.get(i);
            double[] act = newCoords.get(i);
            assertArrayEquals(exp, act, 1e-12);
        }
    }

    @Test
    public void testDisturbingConfilcation() {
        int number = 1000;
        final MFLine line = new SimpMFLine(new Node(0.1, 4));
        MFLine line2 = new SimpMFLine(new Node(-0.1, 8));
        MFLine2DUtils.link(line, line2);
        ByNumberOfNewCoords byNumberOfNewCoords = new SingleLineFractionizer.ByNumberOfNewCoords(number);
        byNumberOfNewCoords.setDisturbRatio(1 - 1e-14);
        List<double[]> newCoords = byNumberOfNewCoords.apply(line);

        Comparator<double[]> distanceToStart = new Comparator<double[]>() {

            @Override
            public int compare(double[] o1, double[] o2) {
                double d = Math2D.distance(o1, line.getStartCoord()) - Math2D.distance(o2, line.getStartCoord());
                return (int) Math.signum(d);
            }
        };

        ArrayList<double[]> newCoordsSorted = new ArrayList<>(newCoords);
        Collections.sort(newCoordsSorted, distanceToStart);
        int i = 0;
        for (double[] newCoord : newCoords) {
            assertTrue(newCoordsSorted.get(i) == newCoord);
            i++;
        }

    }

    @Test
    public void testByNewCoordDistanceSup() {
        double sup = 1.1;
        MFLine line = new SimpMFLine(new Node(0.1, 4));
        MFLine line2 = new SimpMFLine(new Node(0.1, 8));
        MFLine2DUtils.link(line, line2);
        List<double[]> exps = Lists.newArrayList(new double[] { 0.1, 5 }, new double[] { 0.1, 6 }, new double[] {
                0.1,
                7 });
        int expSize = 3;

        ByAverageNeighbourCoordsDistanceSup byUndisturbedCoordsDistanceSup = new SingleLineFractionizer.ByAverageNeighbourCoordsDistanceSup(
                sup);
        List<double[]> newCoords = byUndisturbedCoordsDistanceSup.apply(line);

        assertEquals(expSize, newCoords.size());

        int i = 0;
        for (double[] act : newCoords) {
            assertArrayEquals(exps.get(i), act, 1e-12);
            i++;
        }
    }

}
