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
import net.epsilony.mf.integrate.unit.MFLineUnit;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class LineUnitToGeomQuadraturePoints implements Function<MFLineUnit, List<GeomQuadraturePoint>> {
    private final LineToGeomQuadraturePoints lineToGeomQuadraturePoints = new LineToGeomQuadraturePoints();

    public int getQuadratuePointsNum() {
        return lineToGeomQuadraturePoints.getQuadratuePointsNum();
    }

    public int getQuadratureDegree() {
        return lineToGeomQuadraturePoints.getQuadratureDegree();
    }

    public void setQuadratureDegree(int quadratureDegree) {
        lineToGeomQuadraturePoints.setQuadratureDegree(quadratureDegree);
    }

    @Override
    public List<GeomQuadraturePoint> apply(MFLineUnit t) {
        lineToGeomQuadraturePoints.setGeomAsBoundary(t.isAsBoundary());
        lineToGeomQuadraturePoints.setOneOffOverrideLoadKey(t.getOverrideLoadKey());
        List<GeomQuadraturePoint> result = lineToGeomQuadraturePoints.apply(t.getLine());
        return result;

    }

}
