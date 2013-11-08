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

package net.epsilony.mf.process.integrate;

import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * 
 * @author epsilon
 */
public class RawMFIntegrateResult implements MFIntegrateResult {

    boolean lagrangle;
    int lagrangleDimension;
    MFMatrix mainMatrix, mainVector;

    @Override
    public boolean isLagrangle() {
        return lagrangle;
    }

    public void setLagrangle(boolean lagrangle) {
        this.lagrangle = lagrangle;
    }

    @Override
    public int getLagrangleDimension() {
        return lagrangleDimension;
    }

    public void setLagrangleDimension(int lagrangleDimension) {
        this.lagrangleDimension = lagrangleDimension;
    }

    @Override
    public MFMatrix getMainMatrix() {
        return mainMatrix;
    }

    public void setMainMatrix(MFMatrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    @Override
    public MFMatrix getMainVector() {
        return mainVector;
    }

    public void setMainVector(MFMatrix mainVector) {
        this.mainVector = mainVector;
    }

    public void set(MFIntegrateResult result) {
        this.mainMatrix = result.getMainMatrix();
        this.mainVector = result.getMainVector();
        this.lagrangle = result.isLagrangle();
        this.lagrangleDimension = result.getLagrangleDimension();
    }
}
