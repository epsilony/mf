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
package net.epsilony.mf.implicit.dist_func;

import java.util.function.ToDoubleFunction;

import org.apache.commons.math3.util.MathArrays;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CircleDstFunction implements ToDoubleFunction<double[]> {
    private double[] center;
    private double radius;
    private boolean hole = true;

    public CircleDstFunction(double[] center, double radius, boolean hole) {
        this.center = center;
        this.radius = radius;
        this.hole = hole;
    }

    public CircleDstFunction(double[] center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public CircleDstFunction() {
    }

    public double[] getCenter() {
        return center;
    }

    public void setCenter(double[] center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isHole() {
        return hole;
    }

    public void setConcrete(boolean concrete) {
        this.hole = concrete;
    }

    @Override
    public double applyAsDouble(double[] coord) {
        final double value = radius - MathArrays.distance(coord, center);
        return hole ? value : -value;
    }
}
