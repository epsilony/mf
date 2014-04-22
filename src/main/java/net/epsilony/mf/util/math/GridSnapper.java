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
package net.epsilony.mf.util.math;

import static org.apache.commons.math3.util.FastMath.abs;
import static org.apache.commons.math3.util.FastMath.ceil;
import static org.apache.commons.math3.util.FastMath.floor;
import static org.apache.commons.math3.util.FastMath.round;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class GridSnapper {
    private final double[] steps;

    public GridSnapper(double[] steps) {
        this.steps = steps;
    }

    public double[] getSteps() {
        return steps.clone();
    }

    public double nearest(double v) {
        if (!Double.isFinite(v)) {
            throw new IllegalArgumentException();
        }
        double res = Double.POSITIVE_INFINITY;
        for (double step : steps) {
            double g = round(v / step) * step;
            if (abs(g - v) < abs(res - v)) {
                res = g;
            }
        }
        return res;
    }

    public double sup(double v) {
        if (!Double.isFinite(v)) {
            throw new IllegalArgumentException();
        }
        double res = Double.POSITIVE_INFINITY;
        for (double step : steps) {
            double g = ceil(v / step) * step;
            if (g - v < res - v) {
                res = g;
            }
        }
        return res;
    }

    public double inf(double v) {
        if (!Double.isFinite(v)) {
            throw new IllegalArgumentException();
        }
        double res = Double.NEGATIVE_INFINITY;
        for (double step : steps) {
            double g = floor(v / step) * step;
            if (g - v > res - v) {
                res = g;
            }
        }
        return res;
    }

    public static GridSnapper five() {
        return new GridSnapper(new double[] { 5 });
    }

    public static GridSnapper quarter() {
        return new GridSnapper(new double[] { 1 / 4.0 });
    }

    public static GridSnapper tenth() {
        return new GridSnapper(new double[] { 1 / 10.0 });
    }

    public static GridSnapper unit() {
        return new GridSnapper(new double[] { 1 });
    }

}
