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
package net.epsilony.mf.integrate.integrator.vc;

import net.epsilony.mf.util.math.ArrayPartialValue;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.math.PartialVectorFunction;
import net.epsilony.mf.util.math.Pds2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class PoissonLinearVCNode2D implements VCIntegralNode {

    private static final int SPATIAL_DIMENSION = 2;
    private static final int BASES_SIZE = 2;

    public static boolean DEFAULT_CLEAR_MATRIX_VECTOR_WHEN_SOLVED = false;

    private int assemblyIndex;
    private DenseMatrix64F matrix;
    private DenseMatrix64F vector;
    private DenseMatrix64F result;
    private boolean clearMatrixVectorWhenSolved = DEFAULT_CLEAR_MATRIX_VECTOR_WHEN_SOLVED;
    private ArrayPartialValue valueResult;

    public boolean isClearMatrixVectorWhenSolved() {
        return clearMatrixVectorWhenSolved;
    }

    public void setClearMatrixVectorWhenSolved(boolean clearMatrixVectorWhenSolved) {
        this.clearMatrixVectorWhenSolved = clearMatrixVectorWhenSolved;
    }

    public PoissonLinearVCNode2D() {
        init();
    }

    private void init() {
        matrix = new DenseMatrix64F(BASES_SIZE, BASES_SIZE);
        vector = new DenseMatrix64F(BASES_SIZE, 1);
    }

    public PoissonLinearVCNode2D(int assemblyIndex) {
        this();
        this.assemblyIndex = assemblyIndex;
    }

    public void setAssemblyIndex(int assemblyIndex) {
        this.assemblyIndex = assemblyIndex;
    }

    @Override
    public int getAssemblyIndex() {
        return assemblyIndex;
    }

    @Override
    public void volumeIntegrate(PartialValue shapeFunction, PartialTuple basesValue, double weight) {

        vector.add(0, 0, -shapeFunction.get(Pds2.U_x) * weight);
        vector.add(1, 0, -shapeFunction.get(Pds2.U_y) * weight);

        if (basesValue.size() != matrix.numRows) {
            throw new IllegalStateException();
        }

        for (int index = 0; index < basesValue.size(); index++) {
            matrix.add(0, index, basesValue.get(index, Pds2.U_x) * weight);
            matrix.add(1, index, basesValue.get(index, Pds2.U_y) * weight);
        }

    }

    @Override
    public void boundaryIntegrate(PartialValue shapeFunction, PartialTuple basesValue, double weight,
            double[] unitOutNormal) {
        double sv = weight * shapeFunction.get(0);
        vector.add(0, 0, sv * unitOutNormal[0]);
        vector.add(1, 0, sv * unitOutNormal[1]);

        if (basesValue.size() != matrix.numRows) {
            throw new IllegalStateException();
        }
        for (int index = 0; index < basesValue.size(); index++) {
            double bv = -weight * basesValue.get(index, 0);
            matrix.add(0, index, bv * unitOutNormal[0]);
            matrix.add(1, index, bv * unitOutNormal[1]);

        }

    }

    @Override
    public void solve() {
        result = new DenseMatrix64F(2, 1);
        boolean solved = CommonOps.solve(matrix, vector, result);
        if (!solved) {
            throw new IllegalStateException();
        }
        if (clearMatrixVectorWhenSolved) {
            matrix = null;
            vector = null;
        }
    }

    @Override
    public double[] getVC() {
        return result == null ? null : result.data;
    }

    public PartialValue value(PartialVectorFunction basesFunction, double[] coord) {

        PartialTuple basesValues = basesFunction.value(coord);
        if (null == valueResult || valueResult.getMaxPartialOrder() != basesFunction.getMaxPartialOrder()) {
            valueResult = new ArrayPartialValue(SPATIAL_DIMENSION, basesFunction.getMaxPartialOrder());
        } else {
            valueResult.fill(0);
        }
        for (int pd = 0; pd < basesValues.partialSize(); pd++) {
            for (int i = 0; i < basesValues.size(); i++) {
                valueResult.add(pd, basesValues.get(i, pd) * result.get(i, 0));
            }
        }
        return valueResult;
    }

    public DenseMatrix64F getMatrix() {
        return matrix;
    }

    public DenseMatrix64F getVector() {
        return vector;
    }

}
