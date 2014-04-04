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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.GeomUnit;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleToGeomQuadraturePointsTest {

    @Test
    public void testConstant() {
        double[][] vertes = new double[][] { { -2, -1.2 }, { 3, 0.1 }, { 0.7, 2 } };
        double area = Math2D.area(vertes);
        PolygonIntegrateUnit polygon = new PolygonIntegrateUnit(3);
        polygon.setVertesCoords(vertes);
        GeomUnit mockGeomUnit = mockGeomUnit();
        polygon.setEmbededIn(mockGeomUnit);
        TriangleToGeomQuadraturePoints triQuad = new TriangleToGeomQuadraturePoints();

        boolean tested = false;
        for (int deg = 1; deg <= PolygonToGeomQuadraturePoints.getMaxDegree(); deg++) {
            triQuad.setDegree(deg);
            List<GeomQuadraturePoint> points = triQuad.apply(polygon);
            double value = 0;
            for (GeomQuadraturePoint pt : points) {
                value += pt.getWeight();
                assertTrue(mockGeomUnit == pt.getGeomPoint().getLoadKey());
            }
            assertEquals(area, value, 1e-12);
            tested = true;
        }
        assertTrue(tested);
    }

    @Test
    public void testLinear() {
        double[][] vertes = new double[][] { { -2, -1.2 }, { 3, 0.1 }, { 0.7, 2 } };
        double area = Math2D.area(vertes);
        Function<double[], Double> func = (xy) -> 3 * xy[0] + 4 * xy[1];
        double[] center = MathArrays.ebeAdd(vertes[0], vertes[1]);
        center = MathArrays.ebeAdd(center, vertes[2]);
        center = MathArrays.scale(1 / 3.0, center);
        double exp = func.apply(center) * area;
        PolygonIntegrateUnit polygon = new PolygonIntegrateUnit(3);
        polygon.setVertesCoords(vertes);
        GeomUnit mockGeomUnit = mockGeomUnit();
        polygon.setEmbededIn(mockGeomUnit);
        PolygonToGeomQuadraturePoints quad = new PolygonToGeomQuadraturePoints();
        boolean tested = false;
        for (int deg = 1; deg <= PolygonToGeomQuadraturePoints.getMaxDegree(); deg++) {
            quad.setDegree(deg);
            List<GeomQuadraturePoint> points = quad.apply(polygon);
            double value = 0;
            for (GeomQuadraturePoint pt : points) {
                value += pt.getWeight() * func.apply(pt.getGeomPoint().getCoord());
                assertTrue(mockGeomUnit == pt.getGeomPoint().getLoadKey());
            }
            assertEquals(exp, value, 1e-12);
            tested = true;
        }
        assertTrue(tested);
    }

    private GeomUnit mockGeomUnit() {
        return new GeomUnit() {

            @Override
            public void setId(int id) {
                // TODO Auto-generated method stub

            }

            @Override
            public int getId() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public void setParent(GeomUnit parent) {
                // TODO Auto-generated method stub

            }

            @Override
            public GeomUnit getParent() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

}
