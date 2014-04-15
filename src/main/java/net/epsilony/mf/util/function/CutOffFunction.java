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
package net.epsilony.mf.util.function;

import java.util.function.DoubleUnaryOperator;
import java.util.function.ToDoubleFunction;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CutOffFunction implements DoubleUnaryOperator {

    private double upperLimit = Double.POSITIVE_INFINITY;
    private double lowerLimit = Double.NEGATIVE_INFINITY;

    public double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    @Override
    public double applyAsDouble(double operand) {
        if (operand >= upperLimit) {
            return upperLimit;
        } else if (operand <= lowerLimit) {
            return lowerLimit;
        }
        return operand;
    }

    public <T> ToDoubleFunction<T> combine(ToDoubleFunction<T> before) {
        return t -> applyAsDouble(before.applyAsDouble(t));
    }
}
