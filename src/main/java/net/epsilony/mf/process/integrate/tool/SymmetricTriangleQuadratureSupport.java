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

import net.epsilony.tb.analysis.Math2D;

/**
 * @author epsilon AT epsilony.net
 * 
 */
public class SymmetricTriangleQuadratureSupport {

    private final int quadratureDegree = -1;
    private int currentQuadratuePointIndex;
    private double[][] vertesCoords;
    private double[] weightsOfUnitTriangle;
    private double[][] barycentricCoordinates;
    private double triangleArea;

    public void setQuadratureDegree(int quadratureDegree) {
        SymmetricTriangleQuadratureDatas.checkQuadratureDegree(quadratureDegree);

        if (this.quadratureDegree == quadratureDegree) {
            return;
        }
        weightsOfUnitTriangle = SymmetricTriangleQuadratureDatas.weights(quadratureDegree);
        barycentricCoordinates = SymmetricTriangleQuadratureDatas.barycentricCoordinates(quadratureDegree);
        reset();
    }

    public void setTriangleVertes(double[][] vertes) {
        if (vertes.length != 3) {
            throw new IllegalArgumentException();
        }
        this.vertesCoords = vertes;
        triangleArea = Math.abs(Math2D.area(vertesCoords));
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
        return weightsOfUnitTriangle.length;
    }

    public int getQuadratureDegree() {
        return quadratureDegree;
    }

    public double getWeight() {
        return triangleArea * weightsOfUnitTriangle[currentQuadratuePointIndex];
    }

    public double[] getCoordinate() {
        return SymmetricTriangleQuadratureDatas.cartesianCoordinate(vertesCoords,
                barycentricCoordinates[currentQuadratuePointIndex]);
    }
}
