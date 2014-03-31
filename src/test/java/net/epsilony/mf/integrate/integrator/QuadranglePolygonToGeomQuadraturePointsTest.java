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
import net.epsilony.tb.quadrature.GaussLegendre;
import net.epsilony.tb.solid.GeomUnit;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class QuadranglePolygonToGeomQuadraturePointsTest {

    @Test
    public void testConstant() {
        PolygonIntegrateUnit polygon = new PolygonIntegrateUnit(4);
        GeomUnit mockUnit = mockGeomUnit();
        polygon.setEmbededIn(mockUnit);
        double[][] vertes = new double[][] { { 0.2, 0.3 }, { 5, -1 }, { 4.6, 5 }, { -0.5, 4 } };
        polygon.setVertesCoords(vertes);
        double area = Math2D.triangleArea(vertes[0][0], vertes[0][1], vertes[1][0], vertes[1][1], vertes[2][0],
                vertes[2][1])
                + Math2D.triangleArea(vertes[0][0], vertes[0][1], vertes[2][0], vertes[2][1], vertes[3][0],
                        vertes[3][1]);
        QuadranglePolygonToGeomQuadraturePoints sample = new QuadranglePolygonToGeomQuadraturePoints();
        boolean tested = false;
        for (int deg = 1; deg < LinearQuadratureSupport.getMaxDegree(); deg++) {
            double actArea;
            sample.setDegree(deg);
            List<GeomQuadraturePoint> points = sample.apply(polygon);
            actArea = 0;
            for (GeomQuadraturePoint pt : points) {
                actArea += pt.getWeight();
                assertTrue(mockUnit == pt.getGeomPoint().getGeomUnit());
            }
            assertEquals(area, actArea, 1e-12);
            int t = GaussLegendre.pointsNum(deg);
            assertEquals(t * t, points.size());
            tested = true;
        }
        assertTrue(tested);
    }

    @Test
    public void testLinear() {
        Function<double[], Double> func = (xy) -> 3 * xy[0] + 4 * xy[1];
        PolygonIntegrateUnit polygon = new PolygonIntegrateUnit(4);
        GeomUnit mockUnit = mockGeomUnit();
        polygon.setEmbededIn(mockUnit);
        double[][] vertes = new double[][] { { 0.2, 0.3 }, { 5, -1 }, { 4.6, 5 }, { -0.5, 4 } };
        polygon.setVertesCoords(vertes);
        double area1 = Math2D.triangleArea(vertes[0][0], vertes[0][1], vertes[1][0], vertes[1][1], vertes[2][0],
                vertes[2][1]);
        double area2 = Math2D.triangleArea(vertes[0][0], vertes[0][1], vertes[2][0], vertes[2][1], vertes[3][0],
                vertes[3][1]);
        QuadranglePolygonToGeomQuadraturePoints sample = new QuadranglePolygonToGeomQuadraturePoints();
        double[] tr1 = MathArrays.ebeAdd(vertes[0], vertes[1]);
        tr1 = MathArrays.ebeAdd(tr1, vertes[2]);
        tr1 = MathArrays.scale(1 / 3.0, tr1);
        double[] tr2 = MathArrays.ebeAdd(vertes[0], vertes[2]);
        tr2 = MathArrays.ebeAdd(tr2, vertes[3]);
        tr2 = MathArrays.scale(1 / 3.0, tr2);
        double exp = area1 * func.apply(tr1) + area2 * func.apply(tr2);
        boolean tested = false;
        for (int deg = 2; deg < LinearQuadratureSupport.getMaxDegree(); deg++) {
            double value = 0;
            sample.setDegree(deg);
            for (GeomQuadraturePoint pt : sample.apply(polygon)) {
                value += pt.getWeight() * func.apply(pt.getGeomPoint().getCoord());
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
