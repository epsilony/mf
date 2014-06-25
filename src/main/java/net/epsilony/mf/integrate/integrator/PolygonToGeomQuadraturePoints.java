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

import java.util.List;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class PolygonToGeomQuadraturePoints implements Function<PolygonIntegrateUnit, List<GeomQuadraturePoint>> {
    int                                     quadratureDegree;
    QuadranglePolygonToGeomQuadraturePoints quad = new QuadranglePolygonToGeomQuadraturePoints();
    TriangleToGeomQuadraturePoints          tri  = new TriangleToGeomQuadraturePoints();

    public static int getMaxDegree() {
        return Math.min(QuadranglePolygonToGeomQuadraturePoints.getMaxDegree(),
                TriangleToGeomQuadraturePoints.getMaxDegree());
    }

    @Override
    public List<GeomQuadraturePoint> apply(PolygonIntegrateUnit t) {
        switch (t.getVertesSize()) {
        case 3:
            tri.setDegree(quadratureDegree);
            return tri.apply(t);
        case 4:
            quad.setDegree(quadratureDegree);
            return quad.apply(t);
        default:
            throw new IllegalArgumentException();
        }
    }

    public int getQuadratureDegree() {
        return quadratureDegree;
    }

    public void setQuadratureDegree(int degree) {
        this.quadratureDegree = degree;
    }

}
