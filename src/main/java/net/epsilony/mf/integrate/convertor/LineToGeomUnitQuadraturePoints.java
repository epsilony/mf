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
import java.util.List;

import net.epsilony.mf.integrate.unit.GeomUnitQuadraturePoint;
import net.epsilony.mf.integrate.util.LinearQuadratureSupport;
import net.epsilony.mf.util.convertor.Convertor;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LineToGeomUnitQuadraturePoints implements Convertor<Line, List<GeomUnitQuadraturePoint<Line>>> {
    protected final LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();

    public int getQuadratuePointsNum() {
        return linearQuadratureSupport.getQuadratuePointsNum();
    }

    public int getQuadratureDegree() {
        return linearQuadratureSupport.getQuadratureDegree();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        linearQuadratureSupport.setQuadratureDegree(quadratureDegree);
    }

    @Override
    public List<GeomUnitQuadraturePoint<Line>> convert(Line line) {
        List<GeomUnitQuadraturePoint<Line>> result = new ArrayList<>(linearQuadratureSupport.getQuadratuePointsNum());
        linearQuadratureSupport.setStartEndCoords(line.getStartCoord(), line.getEndCoord());
        linearQuadratureSupport.reset();
        while (linearQuadratureSupport.hasNext()) {
            linearQuadratureSupport.next();
            GeomUnitQuadraturePoint<Line> gqp = new GeomUnitQuadraturePoint<>();
            gqp.setCoord(linearQuadratureSupport.getLinearCoord());
            gqp.setWeight(linearQuadratureSupport.getLinearWeight());
            gqp.setGeomUnit(line);
            result.add(gqp);
        }
        return result;
    }

}
