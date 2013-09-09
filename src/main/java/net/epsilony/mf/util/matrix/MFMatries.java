/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import net.epsilony.mf.util.matrix.wrapper.EJMLMatrix64FWrapper;
import net.epsilony.mf.util.matrix.wrapper.MTJVectorWrapper;
import net.epsilony.mf.util.matrix.wrapper.MTJMatrixWrapper;
import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
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

    public static class DenseMFMatrixIterator implements Iterator<MatrixEntry> {

        public DenseMFMatrixIterator(MFMatrix matrix) {
            this.matrix = matrix;
        }
        MFMatrix matrix;
        int col = 0;
        int row = 0;

        @Override
        public boolean hasNext() {
            return row < matrix.getNumRows();
        }

        @Override
        public MatrixEntry next() {
            MatrixEntry result = new RawMatrixEntry(row, col, matrix.getEntry(row, col));
            col++;
            if (col >= matrix.getNumCols()) {
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
}
