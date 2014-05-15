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
package net.epsilony.mf.opt.integrate;

import java.util.Arrays;
import java.util.function.DoubleFunction;

import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.math.PartialValue;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class LevelPenaltyIntegrator implements LevelFunctionalIntegrator {

    // penalty to avoid negative
    private DoubleFunction<PartialValue> penalty;
    private double value;
    private double[] gradient;
    private int gradientSize;
    private double shift = -1;

    @Override
    public void prepare() {
        value = 0;
        if (null == gradient || gradient.length != gradientSize) {
            gradient = new double[gradientSize];
        } else {
            Arrays.fill(gradient, 0);
        }
    }

    public void integrate(double weight, double levelValue, ShapeFunctionValue levelShapeValue) {
        PartialValue pv = penalty.apply(levelValue);
        value += weight * pv.get(0);
        double pv_d = pv.get(1);
        double scale = weight * pv_d;
        for (int i = 0; i < levelShapeValue.size(); i++) {
            int index = levelShapeValue.getNodeAssemblyIndex(i);
            gradient[index] += scale * levelShapeValue.get(i, 0);
        }
    }

    @Override
    public double value() {
        return value + shift;
    }

    @Override
    public double[] gradient() {
        return gradient;
    }

    public int getGradientSize() {
        return gradientSize;
    }

    @Override
    public void setGradientSize(int gradientSize) {
        this.gradientSize = gradientSize;
    }

    public void setPenalty(DoubleFunction<PartialValue> penalty) {
        this.penalty = penalty;
    }

    @Override
    public void volumeIntegrate(GeomQuadraturePoint gqp, PartialValue levelValue, ShapeFunctionValue levelShapeValue) {
    }

    @Override
    public void boundaryIntegrate(GeomQuadraturePoint gqp, PartialValue levelValue, ShapeFunctionValue levelShapeValue) {
        integrate(gqp.getWeight(), levelValue.get(0), levelShapeValue);
    }

}
