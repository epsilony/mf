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

package net.epsilony.mf.process.assembler;

import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractAssembler implements Assembler {

    public static final int DEFAULT_SPATIAL_DIMENSION = 2;
    public static final int DEFAULT_VALUE_DIMENSION = 2;
    protected int spatialDimension = DEFAULT_SPATIAL_DIMENSION;
    protected int valueDimension = DEFAULT_VALUE_DIMENSION;
    transient protected MFMatrix mainMatrix;
    transient protected MFMatrix mainVector;
    transient protected AssemblyInput assemblyInput;

    public MFMatrix getMainMatrix() {
        return mainMatrix;
    }

    @Override
    public void setMainMatrix(MFMatrix mainMatrix) {
        if (mainMatrix.numCols() != mainMatrix.numRows()) {
            throw new IllegalArgumentException();
        }
        if (mainMatrix.isUpperSymmetric()) {
            throw new IllegalArgumentException("not supporting upper symmetrical matrix");
        }
        this.mainMatrix = mainMatrix;

    }

    public MFMatrix getMainVector() {
        if (mainMatrix.numCols() != 1) {
            throw new IllegalArgumentException();
        }
        return mainVector;
    }

    @Override
    public void setMainVector(MFMatrix mainVector) {
        this.mainVector = mainVector;
    }

    public int getSpatialDimension() {
        return spatialDimension;
    }

    @Override
    public void setSpatialDimension(int spatialDimension) {
        this.spatialDimension = spatialDimension;
    }

    public int getValueDimension() {
        return valueDimension;
    }

    @Override
    public void setValueDimension(int valueDimension) {
        this.valueDimension = valueDimension;
    }

    public AssemblyInput getAssemblyInput() {
        return assemblyInput;
    }

    @Override
    public void setAssemblyInput(AssemblyInput assemblyInput) {
        this.assemblyInput = assemblyInput;
    }
}
