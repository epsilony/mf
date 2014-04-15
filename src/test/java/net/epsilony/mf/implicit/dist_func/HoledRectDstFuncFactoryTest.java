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
package net.epsilony.mf.implicit.dist_func;

import static org.apache.commons.math3.util.MathArrays.distance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import net.epsilony.mf.model.MFHole;
import net.epsilony.mf.model.MFRectangle;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class HoledRectDstFuncFactoryTest {

    private MFRectangle rectangle;
    private double holeRadius;
    private double holeDistanceInf;

    @Test
    public void testCheeseWeakly() {
        rectangle = new MFRectangle();
        rectangle.setDrul(new double[] { 0, 5, 3, 0 });
        holeRadius = 1.2;
        holeDistanceInf = 0.1;
        HoledRectDstFuncFactory cheese = new HoledRectDstFuncFactory.Cheese(rectangle, holeRadius, holeDistanceInf);
        ArrayList<MFHole> holes = new ArrayList<>(cheese.getHoles());
        assertTrue(holes.size() > 0);
        double error = 1e-12;
        assertHolesCenterInside(holes, error);
        assertHolesNonOverlapping(holes);
    }

    @Test
    public void testGridWeakly() {
        rectangle = new MFRectangle();
        rectangle.setDrul(new double[] { 0, 5, 3, 0 });
        holeRadius = 0.6;
        holeDistanceInf = 0.1;
        HoledRectDstFuncFactory grid = new HoledRectDstFuncFactory.Grid(rectangle, holeRadius, holeDistanceInf);
        ArrayList<MFHole> holes = new ArrayList<>(grid.getHoles());
        assertTrue(holes.size() > 0);
        double error = 1e-12;
        assertHolesCenterInside(holes, error);
        assertHolesNonOverlapping(holes);
    }

    private void assertHolesCenterInside(ArrayList<MFHole> holes, double error) {
        for (MFHole hole : holes) {
            double[] center = hole.getCenter();
            double radius = hole.getRadius();
            assertEquals(holeRadius, radius, 0);
            double x = center[0];
            double y = center[1];
            assertTrue(x > rectangle.getLeft() - error);
            assertTrue(x < rectangle.getRight() + error);
            assertTrue(y < rectangle.getUp() + error);
            assertTrue(y > rectangle.getDown() - error);
        }

    }

    private void assertHolesNonOverlapping(ArrayList<MFHole> holes) {
        for (int i = 0; i < holes.size(); i++) {
            MFHole h1 = holes.get(i);
            for (int j = i + 1; j < holes.size(); j++) {
                MFHole h2 = holes.get(j);
                double dist = distance(h1.getCenter(), h2.getCenter());
                assertTrue(dist >= holeRadius * 2 + holeDistanceInf);
            }
        }
    }

}
