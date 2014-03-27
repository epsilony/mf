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
package net.epsilony.mf.integrate.util;

import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.shape_func.ArrayShapeFunctionValue;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class LineLagrangleShapeFunction implements Function<GeomPoint, ShapeFunctionValue> {

    @Override
    public ShapeFunctionValue apply(GeomPoint geomPoint) {
        Line line = (Line) geomPoint.getGeomUnit();
        double[] geomCoord = geomPoint.getGeomCoord();
        double t = geomCoord == null ? Math2D.distance(geomPoint.getCoord(), line.getStartCoord()) / line.length()
                : geomCoord[0];
        int[] nodeLagrangleIds = new int[] { ((MFNode) line.getStart()).getLagrangeAssemblyIndex(),
                ((MFNode) line.getEnd()).getLagrangeAssemblyIndex() };
        double[] lagrangleDatas = new double[] { 1 - t, t };
        return new ArrayShapeFunctionValue(2, 0, new double[][] { lagrangleDatas }, nodeLagrangleIds);
    }
}
