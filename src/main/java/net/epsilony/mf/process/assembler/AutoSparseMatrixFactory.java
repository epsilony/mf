/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.mf.util.matrix.MatrixFactory;

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
    public MFMatrix produce() {
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
        return factory.produce();
    }
}
