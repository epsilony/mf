/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import net.epsilony.mf.util.matrix.HashRowMatrix;
import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.Factory;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class AssemblerMatrixVectorAllocator implements Factory<MFMatrix> {

    public final int DEFAULT_DENSE_MATRIX_SIZE_LIMIT = 300;
    int numRows;
    int numCols;
    int denseMatrixSizeLimit = DEFAULT_DENSE_MATRIX_SIZE_LIMIT;
    boolean useHashSparce = true;

    public void setDenseMatrixSizeLimit(int denseMatrixSizeLimit) {
        this.denseMatrixSizeLimit = denseMatrixSizeLimit;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    @Override
    public MFMatrix produce() {
        if (1 == numCols) {
            return MFMatries.wrap(new DenseVector(numRows));
        }
        if (numCols != numRows) {
            throw new IllegalStateException();
        }
        if (numRows <= denseMatrixSizeLimit) {
            return MFMatries.wrap(new DenseMatrix(numRows, numCols));
        } else {
            if (useHashSparce) {
                return new HashRowMatrix(numRows, numCols);
            } else {
                return MFMatries.wrap(new FlexCompRowMatrix(numRows, numCols));
            }
        }
    }
}
