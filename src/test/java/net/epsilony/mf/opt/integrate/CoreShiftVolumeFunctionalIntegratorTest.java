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

import static org.apache.commons.math3.util.MathArrays.scale;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.SimpGeomPoint;
import net.epsilony.mf.integrate.unit.SimpGeomQuadraturePoint;
import net.epsilony.mf.shape_func.SimpShapeFunctionValue;
import net.epsilony.mf.util.math.ArrayPartialTuple;
import net.epsilony.mf.util.math.ArrayPartialValue;
import net.epsilony.mf.util.math.PartialTuple;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CoreShiftVolumeFunctionalIntegratorTest {

    @Test
    public void test() {
        Function<double[], PartialTuple> levelShapeFunction = (crd) -> {
            ArrayPartialTuple result = new ArrayPartialTuple.SingleArray(2, 2, 1);
            double x = crd[0];
            double y = crd[1];

            double phi1 = x * x + x * y + y * y;
            double phi1_x = 2 * x + y;
            double phi1_y = x + 2 * y;
            double phi2 = 2 * x * x - x * y + y * y;
            double phi2_x = 4 * x - y;
            double phi2_y = -x + 2 * y;
            result.set(0, 0, phi1);
            result.set(0, 1, phi1_x);
            result.set(0, 2, phi1_y);

            result.set(1, 0, phi2);
            result.set(1, 1, phi2_x);
            result.set(1, 2, phi2_y);

            return result;
        };

        double[] alphas = { 2, -1 };

        double[] coord = { -1.1, -7 };

        double weight = 0.13;

        double shift = 2.3;

        ArrayPartialValue levelValue = new ArrayPartialValue(2, 1);
        PartialTuple levelTuple = levelShapeFunction.apply(coord);
        for (int i = 0; i < levelTuple.size(); i++) {
            for (int j = 0; j < levelValue.partialSize(); j++) {
                levelValue.add(j, levelTuple.get(i, j) * alphas[i]);
            }
        }
        SimpShapeFunctionValue levelShape = new SimpShapeFunctionValue(levelTuple, IntUnaryOperator.identity());

        ToDoubleFunction<GeomPoint> coreFunction = (qp) -> {
            double[] crd = qp.getCoord();
            return crd[0] * 2 - crd[1];
        };

        double[] expGrad = { -1.32811968330590, -1.00268334578024 };
        double expValue = 2.92400000000000;

        GeomQuadraturePoint unit = new SimpGeomQuadraturePoint(new SimpGeomPoint(null, null, coord, null, null), weight);

        CoreShiftRangeFunctionalIntegrator integrator = new CoreShiftRangeFunctionalIntegrator();

        integrator.setGradientSize(2);
        integrator.setShift(shift);
        integrator.setCoreFunction(coreFunction);

        integrator.prepare();
        integrator.volumeIntegrate(unit,null,null);
        double actValue = integrator.value();
        assertEquals(expValue, actValue, 1e-14);
        integrator.volumeIntegrate(unit,null,null);
        double actValue2 = integrator.value();
        assertEquals(expValue * 2 - shift, actValue2, 1e-14);

        double[] gradient = integrator.gradient();
        assertArrayEquals(gradient, new double[integrator.getGradientSize()], 0);
        integrator.boundaryIntegrate(unit, levelValue, levelShape);
        assertArrayEquals(expGrad, gradient, 1e-14);
        integrator.boundaryIntegrate(unit, levelValue, levelShape);
        double[] expGrad2 = scale(2, expGrad);
        assertArrayEquals(expGrad2, gradient, 1e-14);

    }
}
