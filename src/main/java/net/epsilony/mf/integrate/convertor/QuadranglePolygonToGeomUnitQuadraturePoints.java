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
package net.epsilony.mf.integrate.convertor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.epsilony.mf.integrate.unit.GeomUnitQuadraturePoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.util.convertor.Convertor;
import net.epsilony.tb.quadrature.QuadrangleQuadrature;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.solid.Facet;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class QuadranglePolygonToGeomUnitQuadraturePoints implements
        Convertor<PolygonIntegrateUnit, List<GeomUnitQuadraturePoint<Facet>>> {

    QuadrangleQuadrature quadrangleQuadrature = new QuadrangleQuadrature();

    @Override
    public List<GeomUnitQuadraturePoint<Facet>> convert(PolygonIntegrateUnit polygonUnit) {
        if (polygonUnit.getVertesSize() != 4) {
            throw new IllegalArgumentException();
        }
        quadrangleQuadrature.setQuadrangle(polygonUnit.getVertexCoord(0)[0], polygonUnit.getVertexCoord(0)[1],
                polygonUnit.getVertexCoord(1)[0], polygonUnit.getVertexCoord(1)[1], polygonUnit.getVertexCoord(2)[0],
                polygonUnit.getVertexCoord(2)[1], polygonUnit.getVertexCoord(3)[0], polygonUnit.getVertexCoord(3)[1]);
        List<GeomUnitQuadraturePoint<Facet>> result = new ArrayList<>(quadrangleQuadrature.numQuadraturePoints());
        Iterator<QuadraturePoint> iterator = quadrangleQuadrature.iterator();
        while (iterator.hasNext()) {
            GeomUnitQuadraturePoint<Facet> gqp = new GeomUnitQuadraturePoint<>();
            QuadraturePoint qp = iterator.next();
            gqp.setCoord(qp.coord);
            gqp.setWeight(qp.weight);
            gqp.setGeomUnit((Facet) polygonUnit.getEmbededIn());
        }
        return result;
    }

    public void setDegree(int degree) {
        quadrangleQuadrature.setDegree(degree);
    }

    public int numQuadraturePoints() {
        return quadrangleQuadrature.numQuadraturePoints();
    }

    public int getDegree() {
        return quadrangleQuadrature.getDegree();
    }

}
