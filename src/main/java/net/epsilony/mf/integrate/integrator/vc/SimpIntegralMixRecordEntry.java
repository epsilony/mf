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
package net.epsilony.mf.integrate.integrator.vc;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.shape_func.ShapeFunctionValue;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SimpIntegralMixRecordEntry implements IntegralMixRecordEntry {
    private double             weight;
    private GeomPoint          geomPoint;
    private ShapeFunctionValue shapeFunctionValue;

    @Override
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public GeomPoint getGeomPoint() {
        return geomPoint;
    }

    public void setGeomPoint(GeomPoint geomPoint) {
        this.geomPoint = geomPoint;
    }

    @Override
    public ShapeFunctionValue getShapeFunctionValue() {
        return shapeFunctionValue;
    }

    public void setShapeFunctionValue(ShapeFunctionValue shapeFunctionValue) {
        this.shapeFunctionValue = shapeFunctionValue;
    }

}
