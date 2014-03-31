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
package net.epsilony.mf.integrate.integrator;

import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.quadrature.GaussLegendre;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LinearQuadratureSupport {

    int quadratureDegree = -1;

    int currentQuadratuePointIndex;

    double[] startCoord;

    double[] endCoord;

    double[] gaussLegendreWeights;

    double[] gaussLegendreParameters;

    private double length;

    public static int getMaxDegree() {
        return GaussLegendre.pointsNum2Degree(GaussLegendre.MAXPOINTS);
    }

    public static int getMaxPointsNum() {
        return GaussLegendre.MAXPOINTS;
    }

    public double getLinearParameter() {
        return (gaussLegendreParameters[currentQuadratuePointIndex] + 1) / 2;
    }

    public double getLinearWeight() {
        return gaussLegendreWeights[currentQuadratuePointIndex] * length / 2;
    }

    public double[] getLinearCoord() {
        double[] result = new double[startCoord.length];
        double par = getLinearParameter();
        for (int i = 0; i < result.length; i++) {
            result[i] = startCoord[i] * (1 - par) + endCoord[i] * par;
        }
        return result;
    }

    public void reset() {
        currentQuadratuePointIndex = -1;
    }

    public boolean hasNext() {
        return currentQuadratuePointIndex < getQuadratuePointsNum() - 1;
    }

    public void next() {
        if (!hasNext()) {
            throw new IllegalArgumentException();
        }
        currentQuadratuePointIndex++;
    }

    public int getQuadratuePointsNum() {
        return gaussLegendreParameters.length;
    }

    public int getQuadratureDegree() {
        return quadratureDegree;
    }

    public void setQuadratureDegree(int quadratureDegree) {
        GaussLegendre.checkDegree(quadratureDegree);
        if (this.quadratureDegree == quadratureDegree) {
            return;
        }
        this.quadratureDegree = quadratureDegree;
        double[][] pointsAndWeights = GaussLegendre.pointsWeightsByDegree(quadratureDegree);
        gaussLegendreParameters = pointsAndWeights[0];
        gaussLegendreWeights = pointsAndWeights[1];
        reset();
    }

    public void setStartEndCoords(double[] startCoord, double[] endCoord) {
        this.startCoord = startCoord;
        this.endCoord = endCoord;
        length = Math2D.distance(startCoord, endCoord);
        reset();
    }
}
