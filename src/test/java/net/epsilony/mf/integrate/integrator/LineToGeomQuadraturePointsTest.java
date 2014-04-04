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
package net.epsilony.mf.integrate.integrator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.model.search.config.LRTreeSegmentsMetricSearcherConfigTest.SingleLine;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class LineToGeomQuadraturePointsTest {

    @Test
    public void testConstant() {
        double[] start = new double[] { -0.2, 3 };
        double[] end = new double[] { 5, -2 };
        SingleLine singleLine = new SingleLine(start, end);
        LineToGeomQuadraturePoints sample1 = new LineToGeomQuadraturePoints();
        boolean tested = false;
        for (int degree = 1; degree <= LinearQuadratureSupport.getMaxDegree(); degree++) {
            LineToGeomQuadraturePoints sample2 = new LineToGeomQuadraturePoints();
            sample1.setQuadratureDegree(degree);
            sample2.setQuadratureDegree(degree);
            List<GeomQuadraturePoint> res1 = sample1.apply(singleLine);
            List<GeomQuadraturePoint> res2 = sample2.apply(singleLine);
            assertLength(singleLine, res1);
            assertLength(singleLine, res2);
            tested = true;
        }
        assertTrue(tested);

    }

    private void assertLength(Line line, List<? extends GeomQuadraturePoint> points) {
        double length = MathArrays.distance(line.getStartCoord(), line.getEndCoord());
        double sumWeight = 0;
        for (GeomQuadraturePoint pt : points) {
            double weight = pt.getWeight();
            GeomPoint geomPoint = pt.getGeomPoint();
            GeomUnit geomUnit = geomPoint.getGeomUnit();
            assertTrue(geomUnit == line);
            assertTrue(geomUnit == geomPoint.getLoadKey());
            double[] values = line.values(geomPoint.getGeomCoord()[0], null);
            sumWeight += weight;
            assertArrayEquals(values, geomPoint.getCoord(), 1e-12);
        }
        assertEquals(length, sumWeight, 1e-12);
    }

    @Test
    public void testLinear() {
        Function<double[], Double> function = (xy) -> 3 * xy[0] + 4 * xy[1];
        double[] start = new double[] { -2, 0.2 };
        double[] end = new double[] { 3, -5 };
        double length = MathArrays.distance(start, end);
        double exp = length * function.apply(MathArrays.scale(0.5, MathArrays.ebeAdd(start, end)));
        LineToGeomQuadraturePoints sample1 = new LineToGeomQuadraturePoints();
        boolean tested = false;
        for (int deg = 1; deg <= LinearQuadratureSupport.getMaxDegree(); deg++) {
            sample1.setQuadratureDegree(deg);
            List<GeomQuadraturePoint> points = sample1.apply(new SingleLine(start, end));
            assertEquals(exp, quadrature(function, points), 1e-12);
            tested = true;
        }
        assertTrue(tested);
    }

    private double quadrature(Function<double[], Double> function, List<? extends GeomQuadraturePoint> points) {
        double result = 0;
        for (GeomQuadraturePoint pt : points) {
            double value = function.apply(pt.getGeomPoint().getCoord());
            double weight = pt.getWeight();
            result += value * weight;
        }
        return result;
    }

}
