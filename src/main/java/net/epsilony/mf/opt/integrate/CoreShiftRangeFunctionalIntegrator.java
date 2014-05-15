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

import static org.apache.commons.math3.util.FastMath.sqrt;

import java.util.Arrays;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.util.math.PartialValue;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CoreShiftRangeFunctionalIntegrator implements LevelFunctionalIntegrator {
    private ToDoubleFunction<GeomPoint> coreFunction;

    private double shift = 0;
    private double core;
    private double[] gradient;
    private int gradientSize = -1;

    @Override
    public void prepare() {
        core = 0;
        if (null == gradient || gradient.length != gradientSize) {
            gradient = new double[gradientSize];
        } else {
            Arrays.fill(gradient, 0);
        }
    }

    @Override
    public void volumeIntegrate(GeomQuadraturePoint gqp, PartialValue levelValue, ShapeFunctionValue levelShapeValue) {
        double weightedCore = gqp.getWeight() * coreFunction.applyAsDouble(gqp.getGeomPoint());
        core += weightedCore;
    }

    @Override
    public void boundaryIntegrate(GeomQuadraturePoint gqp, PartialValue levelValue, ShapeFunctionValue levelShapeValue) {
        double weightedCore = gqp.getWeight() * coreFunction.applyAsDouble(gqp.getGeomPoint());

        double dx = levelValue.get(1);
        double dy = levelValue.get(2);
        double levelFunctionGradientNorm = sqrt(dx * dx + dy * dy);

        double gradScale = -weightedCore / levelFunctionGradientNorm;

        for (int i = 0; i < levelShapeValue.size(); i++) {
            int index = levelShapeValue.getNodeAssemblyIndex(i);
            gradient[index] += gradScale * levelShapeValue.get(i, 0);
        }
    }

    @Override
    public double value() {
        return core + shift;
    }

    @Override
    public double[] gradient() {
        return gradient;
    }

    public ToDoubleFunction<GeomPoint> getCoreFunction() {
        return coreFunction;
    }

    public void setCoreFunction(ToDoubleFunction<GeomPoint> coreFunction) {
        this.coreFunction = coreFunction;
    }

    public double getShift() {
        return shift;
    }

    public void setShift(double shift) {
        this.shift = shift;
    }

    public int getGradientSize() {
        return gradientSize;
    }

    @Override
    public void setGradientSize(int gradientLength) {
        this.gradientSize = gradientLength;
    }
}
