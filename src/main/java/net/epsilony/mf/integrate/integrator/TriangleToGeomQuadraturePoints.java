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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.unit.SimpGeomPoint;
import net.epsilony.mf.integrate.unit.SimpGeomQuadraturePoint;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.SymmetricTriangleQuadratureUtils;
import net.epsilony.tb.quadrature.SymmetricTriangleQuadratureUtils.QuadIterator;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TriangleToGeomQuadraturePoints implements Function<PolygonIntegrateUnit, List<GeomQuadraturePoint>> {
    int degree = -1;

    public static int getMaxDegree() {
        return SymmetricTriangleQuadratureUtils.MAX_ALGEBRAIC_ACCURACY;
    }

    @Override
    public List<GeomQuadraturePoint> apply(PolygonIntegrateUnit polygon) {
        if (polygon.getVertesSize() != 3) {
            throw new IllegalArgumentException();
        }
        int numPts = SymmetricTriangleQuadratureUtils.numPointsByAlgebraicAccuracy(degree);
        ArrayList<GeomQuadraturePoint> result = new ArrayList<>(numPts);
        double x1 = polygon.getVertexCoord(0)[0];
        double y1 = polygon.getVertexCoord(0)[1];
        double x2 = polygon.getVertexCoord(1)[0];
        double y2 = polygon.getVertexCoord(1)[1];
        double x3 = polygon.getVertexCoord(2)[0];
        double y3 = polygon.getVertexCoord(2)[1];
        QuadIterator quadIterator = new SymmetricTriangleQuadratureUtils.QuadIterator(x1, y1, x2, y2, x3, y3, degree);
        while (quadIterator.hasNext()) {
            QuadraturePoint qp = quadIterator.next();
            SimpGeomQuadraturePoint gqp = new SimpGeomQuadraturePoint();
            gqp.setWeight(qp.weight);
            SimpGeomPoint geomPoint = new SimpGeomPoint();
            geomPoint.setCoord(qp.coord);
            geomPoint.setLoadKey(polygon.getLoadKey());
            gqp.setGeomPoint(geomPoint);
            result.add(gqp);
        }
        return result;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

}
