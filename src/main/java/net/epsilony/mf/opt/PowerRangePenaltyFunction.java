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
package net.epsilony.mf.opt;

import static org.apache.commons.math3.util.FastMath.pow;

import java.util.function.DoubleFunction;

import net.epsilony.mf.util.math.ArrayPartialValue;
import net.epsilony.mf.util.math.PartialValue;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class PowerRangePenaltyFunction implements DoubleFunction<PartialValue> {
    private double scale = 1;
    private int power = 3;

    private final ArrayPartialValue result = new ArrayPartialValue(1, 1);

    @Override
    public PartialValue apply(double levelFunction) {
        if (levelFunction < 0) {
            result.set(0, pow(-levelFunction, power) * scale);
            result.set(1, -pow(-levelFunction, power - 1) * power * scale);
        } else {
            result.set(0, 0);
            result.set(1, 0);
        }
        return result;
    }

    public PowerRangePenaltyFunction(double scale, int power) {
        this.scale = scale;
        this.power = power;
    }

    public PowerRangePenaltyFunction() {
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

}
