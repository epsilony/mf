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
package net.epsilony.mf.integrate.unit;

import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class SimpGeomQuadraturePoint<T extends GeomUnit> implements GeomQuadraturePoint<T> {
    GeomPoint<T> geomPoint;

    double weight;

    @Override
    public GeomPoint<T> getGeomPoint() {
        return geomPoint;
    }

    public void setGeomPoint(GeomPoint<T> geomPoint) {
        this.geomPoint = geomPoint;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double quadratureWeight) {
        this.weight = quadratureWeight;
    }

    public SimpGeomQuadraturePoint(GeomPoint<T> geomPoint, double quadratureWeight) {
        this.geomPoint = geomPoint;
        this.weight = quadratureWeight;
    }

    public SimpGeomQuadraturePoint() {
    }

}
