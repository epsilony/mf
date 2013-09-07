/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import java.util.Iterator;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix64F;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFMatries {

    public final static Logger logger = LoggerFactory.getLogger(MFMatries.class);

    public static WrapperMFMatrix<Matrix> wrap(Matrix mat) {
        return new MTJMatrixWrapper(mat);
    }

    public static WrapperMFMatrix<Vector> wrap(Vector vec) {
        return new MTJVectorWrapper(vec);
    }

    public static WrapperMFMatrix<Matrix64F> wrap(Matrix64F mat) {
        return new EJMLMatrix64FWrapper(mat);
    }

    public static WrapperMFMatrix<Matrix> allocateMTJ(int numRows, int numCols) {
        return new MTJMatrixWrapper(new DenseMatrix(numRows, numCols));
    }

    public static WrapperMFMatrix<Matrix> allocateMTJ(MatrixInfo matInfo) {
        Matrix mat;
        if (matInfo.isRecommandedDense()) {
            mat = new DenseMatrix(matInfo.numRows, matInfo.numCols);
        } else {
            mat = new FlexCompRowMatrix(matInfo.numRows, matInfo.numCols);
        }
        return new MTJMatrixWrapper(mat);
    }

    public static WrapperMFMatrix<Matrix64F> allocateEJML(int numRows, int numCols) {
        return new EJMLMatrix64FWrapper(new DenseMatrix64F(numRows, numCols));
    }

    public static WrapperMFMatrix<Matrix64F> allocateEJML(MatrixInfo matInfo) {
        return allocateEJML(matInfo.numRows, matInfo.numCols);
    }

    static class MTJMatrixWrapper extends AbstractWrapperMFMatrix<Matrix> {

        public MTJMatrixWrapper(Matrix matrix) {
            this.matrix = matrix;
        }

        @Override
        public int numRows() {
            return matrix.numRows();
        }

        @Override
        public int numCols() {
            return matrix.numColumns();
        }

        @Override
        public void set(int row, int column, double value) {
            matrix.set(row, column, value);
        }

        @Override
        public double get(int row, int column) {
            return matrix.get(row, column);
        }

        @Override
        public Iterator<MatrixEntry> iterator() {
            return matrix.iterator();
        }
    }

    static class MTJVectorWrapper extends AbstractWrapperMFMatrix<Vector> {

        public MTJVectorWrapper(Vector vec) {
            this.matrix = vec;
        }


        @Override
        public int numRows() {
            return matrix.size();
        }

        @Override
        public int numCols() {
            return 1;
        }

        @Override
        public void set(int row, int col, double value) {
            if (col != 0) {
                throw new IllegalArgumentException("for a vector wrapper the given col must be 0, not " + col);
            }
            matrix.set(row, value);
        }

        @Override
        public double get(int row, int col) {
            if (col != 0) {
                throw new IllegalArgumentException("for a vector wrapper the given col must be 0, not " + col);
            }
            return matrix.get(row);
        }

        @Override
        public Iterator<MatrixEntry> iterator() {
            return new DenseMFMatrixIterator(this);
        }
    }

    static class EJMLMatrix64FWrapper extends AbstractWrapperMFMatrix<Matrix64F> {

        public EJMLMatrix64FWrapper(Matrix64F mat) {
            this.matrix = mat;
        }

        @Override
        public int numRows() {
            return matrix.numRows;
        }

        @Override
        public int numCols() {
            return matrix.numCols;
        }

        @Override
        public void set(int row, int col, double value) {
            matrix.set(row, col, value);
        }

        @Override
        public double get(int row, int col) {
            return matrix.get(row, col);
        }

        @Override
        public Iterator<MatrixEntry> iterator() {
            return new DenseMFMatrixIterator(this);
        }

        @Override
        public Matrix64F getBackend() {
            return matrix;
        }
    }

    static class DenseMFMatrixIterator implements Iterator<MatrixEntry> {

        public DenseMFMatrixIterator(MFMatrix matrix) {
            this.matrix = matrix;
        }
        MFMatrix matrix;
        int col = 0;
        int row = 0;

        @Override
        public boolean hasNext() {
            return row < matrix.numRows();
        }

        @Override
        public MatrixEntry next() {
            MatrixEntry result = new RawMatrixEntry(row, col, matrix.get(row, col));
            col++;
            if (col >= matrix.numCols()) {
                col = 0;
                row++;
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    static class VectorIteratorWrapper implements Iterator<MatrixEntry> {

        Iterator<VectorEntry> vectorIter;

        @Override
        public boolean hasNext() {
            return vectorIter.hasNext();
        }

        @Override
        public MatrixEntry next() {
            VectorEntry ve = vectorIter.next();
            return new RawMatrixEntry(ve.index(), 0, ve.get());
        }

        @Override
        public void remove() {
            vectorIter.remove();
        }
    }

    static class RawMatrixEntry implements MatrixEntry {

        int row;
        int col;
        double value;

        public RawMatrixEntry(int row, int col, double value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        @Override
        public int row() {
            return row;
        }

        @Override
        public int column() {
            return col;
        }

        @Override
        public double get() {
            return value;
        }

        @Override
        public void set(double value) {
            this.value = value;
        }
    }
}
