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

package net.epsilony.mf.cons_law;

import java.util.Arrays;
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawConstitutiveLaw implements ConstitutiveLaw {

    MFMatrix matrix;

    public MFMatrix getMatrix() {
        return matrix;
    }

    public RawConstitutiveLaw(MFMatrix matrix) {
        if (matrix.numCols() != matrix.numRows()) {
            throw new IllegalArgumentException();
        }
        this.matrix = matrix;
    }

    @Override
    public double[] calcStressByEngineeringStrain(double[] strain, double[] result) {
        if (null == result) {
            result = new double[matrix.numRows()];
        } else {
            Arrays.fill(result, 0);
        }
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                result[i] += matrix.get(i, j) * strain[j];
            }
        }
        return result;
    }

    @Override
    public void setDimension(int dim) {
        int[] dims = new int[] { 1, 3, 6 };
        if (dims[dim - 1] != matrix.numRows()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int getDimension() {
        switch (matrix.numRows()) {
        case 6:
            return 3;
        case 3:
            return 2;
        case 1:
            return 1;
        default:
            throw new IllegalStateException();
        }
    }
}
