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
package net.epsilony.mf.process.integrate.tool;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import net.epsilony.tb.analysis.ArrvarFunction;
import net.epsilony.tb.common_func.MonomialBases2D;
import net.epsilony.tb.quadrature.SymmetricTriangleQuadratureUtils;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author epsilon
 * 
 */
public class SymmetricTriangleQuadratureSupportTest {

    @Test
    public void testByArea() {
        double[][] vertes = new double[][] { { 1, -2 }, { -1, 1 }, { 2, 2 } };
        double expArea = 5.5;
        SymmetricTriangleQuadratureSupport support = new SymmetricTriangleQuadratureSupport();
        support.setTriangleVertes(vertes);
        for (int quadratureDegree = SymmetricTriangleQuadratureDatas.MIN_ALGEBRAIC_ACCURACY; quadratureDegree <= SymmetricTriangleQuadratureDatas.MAX_ALGEBRAIC_ACCURACY; quadratureDegree++) {
            support.setQuadratureDegree(quadratureDegree);
            double actArea = 0;
            while (support.hasNext()) {
                support.next();
                actArea += support.getWeight();
            }
            Assert.assertEquals(expArea, actArea, 1e-13);
        }
    }

    @Test
    public void testByRandomPolynomial() {
        double x1 = 0.1;
        double y1 = 0.3;
        double x2 = 10.2;
        double y2 = 1.1;
        double x3 = 5.5;
        double y3 = 4.9;

        double[][] vertes = new double[][] { { x1, y1 }, { x2, y2 }, { x3, y3 } };

        SymmetricTriangleQuadratureSupport support = new SymmetricTriangleQuadratureSupport();
        support.setTriangleVertes(vertes);

        for (int polynomialDegree = SymmetricTriangleQuadratureDatas.MIN_ALGEBRAIC_ACCURACY; polynomialDegree <= SymmetricTriangleQuadratureDatas.MAX_ALGEBRAIC_ACCURACY; polynomialDegree++) {
            Random2DPolynomial randPoly = new Random2DPolynomial(polynomialDegree);
            boolean tested = false;
            for (int quadratureDegree = polynomialDegree; quadratureDegree <= SymmetricTriangleQuadratureUtils.MAX_ALGEBRAIC_ACCURACY; quadratureDegree++) {
                support.setQuadratureDegree(quadratureDegree);

                double act = 0;
                while (support.hasNext()) {
                    support.next();
                    act += randPoly.value(support.getCoordinate()) * support.getWeight();
                }

                double exp = -integrateWithSimpson(randPoly, x1, y1, x2, y2)
                        + integrateWithSimpson(randPoly, x1, y1, x3, y3)
                        + integrateWithSimpson(randPoly, x3, y3, x2, y2);
                assertEquals(exp, act, Math.max(1e-6, 1e-6 * exp));
                tested = true;
            }
            Assert.assertTrue(tested);
        }
    }

    public double integrateWithSimpson(ArrvarFunction fun, double x1, double y1, double x2, double y2) {
        SimpsonIntegrator integrator = new SimpsonIntegrator();
        return integrator.integrate((int) 1e4, new IntegrateY(fun, x1, y1, x2, y2), x1, x2);
    }

    static class Random2DPolynomial implements ArrvarFunction {

        @Override
        public double value(double[] vec) {
            double[][] output = null;
            double[][] bs = bases.values(vec, output);
            double result = 0;
            for (int i = 0; i < bs[0].length; i++) {
                result += bs[0][i] * pars[i];
            }
            return result;
        }

        double[] pars;
        MonomialBases2D bases;

        public Random2DPolynomial(int power) {
            bases = new MonomialBases2D();
            bases.setDegree(power);
            pars = new double[bases.basesSize()];
            Random rand = new Random();
            for (int i = 0; i < pars.length; i++) {
                pars[i] = rand.nextDouble();
            }
        }
    }

    static class IntegrateY implements UnivariateFunction {

        @Override
        public double value(final double x) {
            double low = 0;
            double up = y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            UnivariateFunction f = new UnivariateFunction() {
                @Override
                public double value(double y) {
                    return func.value(new double[] { x, y });

                }
            };
            return simpIntegrator.integrate(10000, f, low, up);
        }

        final ArrvarFunction func;
        double x1, y1, x2, y2;
        SimpsonIntegrator simpIntegrator = new SimpsonIntegrator();

        public IntegrateY(ArrvarFunction func, double x1, double y1, double x2, double y2) {
            this.func = func;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

}
