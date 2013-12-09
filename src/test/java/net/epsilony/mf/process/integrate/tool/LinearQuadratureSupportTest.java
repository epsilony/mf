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
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.epsilony.tb.analysis.ArrvarFunction;
import net.epsilony.tb.quadrature.GaussLegendre;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.gauss.GaussIntegrator;
import org.apache.commons.math3.analysis.integration.gauss.GaussIntegratorFactory;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LinearQuadratureSupportTest {

    LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();
    Random random = new Random();
    double start = -1.2;
    double end = 1.7;
    Logger logger = LoggerFactory.getLogger(LinearQuadratureSupportTest.class);

    @Test
    public void testLength() {

        double[] startCoord = new double[] { 1, -1 };
        double[] endCoord = new double[] { -2, 3 };
        double exp = 5;
        boolean getHere = false;
        linearQuadratureSupport.setStartEndCoords(startCoord, endCoord);
        for (int quadratureDegree = 1; quadratureDegree < GaussLegendre.MAXPOINTS * 2 - 1; quadratureDegree++) {
            double act = 0;
            linearQuadratureSupport.setQuadratureDegree(quadratureDegree);
            linearQuadratureSupport.reset();
            while (linearQuadratureSupport.hasNext()) {
                linearQuadratureSupport.next();
                double weight = linearQuadratureSupport.getLinearWeight();
                act += weight;
            }
            assertEquals(exp, act, 1e-15);
            getHere = true;
        }
        assertTrue(getHere);
    }

    @Test
    public void testLadderX() {
        ArrvarFunction func = new ArrvarFunction() {
            @Override
            public double value(double[] vec) {
                return vec[0];
            }
        };

        double[] startCoord = new double[] { 1, 2 };
        double[] endCoord = new double[] { -2, 6 };
        double exp = -2.5;
        boolean getHere = false;
        linearQuadratureSupport.setStartEndCoords(startCoord, endCoord);
        for (int quadratureDegree = 1; quadratureDegree < GaussLegendre.MAXPOINTS * 2 - 1; quadratureDegree++) {
            linearQuadratureSupport.setQuadratureDegree(quadratureDegree);
            linearQuadratureSupport.reset();
            double act = 0;
            while (linearQuadratureSupport.hasNext()) {
                linearQuadratureSupport.next();
                double funcValue = func.value(linearQuadratureSupport.getLinearCoord());
                act += funcValue * linearQuadratureSupport.getLinearWeight();
            }
            assertEquals(exp, act, 1e-13);
            getHere = true;
        }
        assertTrue(getHere);
    }

    @Test
    public void testLadderY() {
        ArrvarFunction func = new ArrvarFunction() {
            @Override
            public double value(double[] vec) {
                return vec[1];
            }
        };

        double[] startCoord = new double[] { 1, 2 };
        double[] endCoord = new double[] { -2, 6 };
        double exp = 20;
        boolean getHere = false;
        linearQuadratureSupport.setStartEndCoords(startCoord, endCoord);
        for (int quadratureDegree = 1; quadratureDegree < GaussLegendre.MAXPOINTS * 2 - 1; quadratureDegree++) {
            linearQuadratureSupport.setQuadratureDegree(quadratureDegree);
            linearQuadratureSupport.reset();
            double act = 0;
            while (linearQuadratureSupport.hasNext()) {
                linearQuadratureSupport.next();
                double funcValue = func.value(linearQuadratureSupport.getLinearCoord());
                act += funcValue * linearQuadratureSupport.getLinearWeight();
            }
            assertEquals(exp, act, 1e-13);
            getHere = true;
        }
        assertTrue(getHere);
    }

    @Test
    public void testWithRandomPolynomail() {
        for (int degree = 1; degree <= GaussLegendre.pointsNum2Degree(GaussLegendre.MAXPOINTS); degree++) {
            testWithRandomPolynomial(degree);
        }
    }

    private void testWithRandomPolynomial(int degree) {
        LinearQuadratureSupport support = new LinearQuadratureSupport();
        support.setStartEndCoords(new double[] { start, 0 }, new double[] { end, 0 });
        support.setQuadratureDegree(degree);
        GaussIntegratorFactory gaussFactory = new GaussIntegratorFactory();
        GaussIntegrator legendre = gaussFactory.legendre(GaussLegendre.pointsNum(degree), start, end);
        GaussIntegrator legendreHighPrecision = gaussFactory.legendreHighPrecision(GaussLegendre.pointsNum(degree),
                start, end);

        for (int polyDegree = 1; polyDegree <= degree; polyDegree++) {
            PolynomialFunction integratedFunction = genRandomPolynomial(polyDegree + 1);
            UnivariateFunction toBeIntegrated = integratedFunction.derivative();

            support.reset();
            double act = 0;
            while (support.hasNext()) {
                support.next();
                double x = support.getLinearCoord()[0];
                double weight = support.getLinearWeight();
                double value = toBeIntegrated.value(x);
                act += value * weight;
            }
            double exp = integratedFunction.value(end) - integratedFunction.value(start);
            double apExp = legendre.integrate(toBeIntegrated);
            double apHighExp = legendreHighPrecision.integrate(toBeIntegrated);
            logger.debug(String.format("poly_deg = %d, int_deg = %d, exp = %20.16f, act = %20.16f", polyDegree, degree,
                    exp, act));
            logger.debug(String.format("    exp - act       = %20.16f", exp - act));
            logger.debug(String.format("    apExp - act     = %20.16f", apExp - act));
            logger.debug(String.format("    apHighExp - act = %20.16f", apHighExp - act));
            logger.debug(String.format("    apHighExp - exp = %20.16f", apHighExp - exp));
            logger.debug(String.format("    (exp-act)/exp   = %20.16e", (exp - act) / exp));
            assertEquals(exp, act, exp * 1e-14);
        }
    }

    private PolynomialFunction genRandomPolynomial(int degree) {
        double[] coefs = new double[degree + 1];
        for (int i = 0; i < coefs.length; i++) {
            coefs[i] = random.nextDouble();
        }
        PolynomialFunction result = new PolynomialFunction(coefs);
        return result;

    }
}
