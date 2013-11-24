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
import net.epsilony.tb.analysis.ArrvarFunction;
import net.epsilony.tb.quadrature.GaussLegendre;

import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LinearQuadratureSupportTest {

    LinearQuadratureSupport linearQuadratureSupport = new LinearQuadratureSupport();

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
            assertEquals(exp, act, 1e-12);
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
            assertEquals(exp, act, 1e-12);
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
            assertEquals(exp, act, 1e-12);
            getHere = true;
        }
        assertTrue(getHere);
    }
}
