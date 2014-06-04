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

import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import org.ejml.data.DenseMatrix64F;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PlaneStress implements ConstitutiveLaw {

    private final double                          youngModulity, poissonRatio;
    private final WrapperMFMatrix<DenseMatrix64F> matrixWrapper;
    private final DenseMatrix64F                  matrix;

    public MFMatrix getMatrix() {
        return matrixWrapper;
    }

    public PlaneStress(double youngModulity, double poissonRatio) {
        this.youngModulity = youngModulity;
        this.poissonRatio = poissonRatio;
        double t = youngModulity / (1 - poissonRatio * poissonRatio);
        matrix = new DenseMatrix64F(3, 3);
        matrix.set(0, 0, t);
        matrix.set(0, 1, poissonRatio * t);
        matrix.set(1, 0, poissonRatio * t);
        matrix.set(1, 1, t);
        matrix.set(2, 2, (1 - poissonRatio) / 2 * t);
        matrixWrapper = MFMatries.wrap(matrix);
    }

    public double getYoungModulity() {
        return youngModulity;
    }

    public double getNu() {
        return poissonRatio;
    }

    @Override
    public double[] calcStressByEngineeringStrain(double[] strain, double[] result) {

        double s11 = matrix.get(0, 0) * strain[0] + matrix.get(0, 1) * strain[1];
        double s22 = matrix.get(1, 0) * strain[0] + matrix.get(1, 1) * strain[1];
        double s12 = matrix.get(2, 2) * strain[2];
        if (null == result) {
            result = new double[] { s11, s22, s12 };
        } else {
            result[0] = s11;
            result[1] = s22;
            result[2] = s12;
        }
        return result;
    }

    @Override
    public void setDimension(int dim) {
        if (dim != 2) {
            throw new IllegalArgumentException("PlaneStress only supports dimension 2");
        }
    }

    @Override
    public int getDimension() {
        return 2;
    }
}
