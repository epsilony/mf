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

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public abstract class AbstractVCNode implements VCNode {

    public static boolean DEFAULT_CLEAR_MATRIX_VECTOR_WHEN_SOLVED = false;

    @Override
    public abstract void boundaryIntegrate(double[] coord, PartialValue shapeFunction, PartialTuple basesValue,
            double weight, double[] unitOutNormal);

    @Override
    public abstract void volumeIntegrate(double[] coord, PartialValue shapeFunction, PartialTuple basesValue,
            double weight);

    protected int assemblyIndex;
    protected DenseMatrix64F matrix;
    protected DenseMatrix64F vector;
    private DenseMatrix64F result;
    private boolean clearMatrixVectorWhenSolved = DEFAULT_CLEAR_MATRIX_VECTOR_WHEN_SOLVED;
    private ArrayPartialValue valueResult;

    public boolean isClearMatrixVectorWhenSolved() {
        return clearMatrixVectorWhenSolved;
    }

    public void setClearMatrixVectorWhenSolved(boolean clearMatrixVectorWhenSolved) {
        this.clearMatrixVectorWhenSolved = clearMatrixVectorWhenSolved;
    }

    abstract protected int getBasesSize();

    abstract protected int getSpatialDimension();

    public AbstractVCNode(int assemblyIndex) {
        this();
        this.assemblyIndex = assemblyIndex;
    }

    public AbstractVCNode() {
        init();
    }

    protected void init() {
        int basesSize = getBasesSize();
        matrix = new DenseMatrix64F(basesSize, basesSize);
        vector = new DenseMatrix64F(basesSize, 1);
    }

    public void setAssemblyIndex(int assemblyIndex) {
        this.assemblyIndex = assemblyIndex;
    }

    @Override
    public int getAssemblyIndex() {
        return assemblyIndex;
    }

    @Override
    public void solve() {
        result = new DenseMatrix64F(getBasesSize(), 1);
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
            valueResult = new ArrayPartialValue(getSpatialDimension(), basesFunction.getMaxPartialOrder());
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