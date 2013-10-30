/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SingleSynchronziedBigDecimalMatrixFactory implements MatrixFactory<SynchronizedBigDecimalMFMatrix> {

    int numRows = -1;
    int numCols = -1;
    SynchronizedBigDecimalMFMatrix matrix = null;
    Class<? extends BigDecimalMFMatrix> matrixClass;

    public SingleSynchronziedBigDecimalMatrixFactory(Class<? extends BigDecimalMFMatrix> matrixClass) {
        this.matrixClass = matrixClass;
    }

    @Override
    public void setNumRows(int numRows) {
        if (matrix == null) {
            this.numRows = numRows;
        } else {
            if (this.numRows != numRows) {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public void setNumCols(int numCols) {
        if (matrix == null) {
            this.numCols = numCols;
        } else {
            if (this.numCols != numCols) {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public SynchronizedBigDecimalMFMatrix produce() {
        if (matrix == null) {
            genMatrix();
        }
        return matrix;
    }

    private void genMatrix() {
        AutoMFMatrixFactory autoMFMatrixFactory = new AutoMFMatrixFactory(matrixClass);
        autoMFMatrixFactory.setNumCols(numCols);
        autoMFMatrixFactory.setNumRows(numRows);
        BigDecimalMFMatrix bigDecimalMFMatrix = (BigDecimalMFMatrix) autoMFMatrixFactory.produce();
        matrix = new SynchronizedBigDecimalMFMatrix(bigDecimalMFMatrix);
    }
}
