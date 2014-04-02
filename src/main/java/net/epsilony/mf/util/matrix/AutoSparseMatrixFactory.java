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

package net.epsilony.mf.util.matrix;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class AutoSparseMatrixFactory implements MatrixFactory<MFMatrix> {

    public final int DEFAULT_DENSE_MATRIX_SIZE_LIMIT = 300;
    int numRows;
    int numCols;
    int denseMatrixSizeLimit = DEFAULT_DENSE_MATRIX_SIZE_LIMIT;
    MatrixFactory<? extends MFMatrix> denseMatrixFactory = null;
    MatrixFactory<? extends MFMatrix> sparseMatrixFactory = null;

    public static AutoSparseMatrixFactory produceDefault() {
        AutoSparseMatrixFactory result = new AutoSparseMatrixFactory();
        result.setDenseMatrixFactory(new AutoMFMatrixFactory(DenseMatrix.class));
        result.setSparseMatrixFactory(new AutoMFMatrixFactory(FlexCompRowMatrix.class));
        return result;
    }

    public void setDenseMatrixSizeLimit(int denseMatrixSizeLimit) {
        this.denseMatrixSizeLimit = denseMatrixSizeLimit;
    }

    @Override
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    @Override
    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public void setDenseMatrixFactory(MatrixFactory<? extends MFMatrix> denseMatrixFactory) {
        this.denseMatrixFactory = denseMatrixFactory;
    }

    public void setSparseMatrixFactory(MatrixFactory<? extends MFMatrix> sparseMatrixFactory) {
        this.sparseMatrixFactory = sparseMatrixFactory;
    }

    @Override
    public MFMatrix get() {
        MatrixFactory<? extends MFMatrix> factory;
        if (numCols != numRows) {
            throw new IllegalStateException();
        }
        if (numRows <= denseMatrixSizeLimit) {
            factory = denseMatrixFactory;
        } else {
            factory = sparseMatrixFactory;
        }
        factory.setNumCols(numCols);
        factory.setNumRows(numRows);
        return factory.get();
    }
}
