/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.epsilony.mf.util.matrix.wrapper.EJMLMatrix64FWrapper;
import net.epsilony.mf.util.matrix.wrapper.MTJVectorWrapper;
import net.epsilony.mf.util.matrix.wrapper.MTJMatrixWrapper;
import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import java.util.Iterator;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import org.ejml.data.Matrix64F;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFMatries {

    public final static Logger logger = LoggerFactory.getLogger(MFMatries.class);

    public static Object allocateMatrix(MFMatrixData data) {
        Object matrix;
        final Class<?> matrixClass = data.getMatrixClass();
        try {
            Constructor<?> constructor;
            if (Vector.class.isAssignableFrom(matrixClass)) {
                constructor = matrixClass.getConstructor(int.class);
                if (data.numCols != 1) {
                    throw new IllegalStateException();
                }
                matrix = constructor.newInstance(data.getNumRows());
            } else {
                constructor = matrixClass.getConstructor(int.class, int.class);
                matrix = constructor.newInstance(data.getNumRows(), data.getNumCols());
            }

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }

        if (Vector.class.isAssignableFrom(matrixClass)) {
            Vector vector = (Vector) matrix;
            for (MatrixEntry me : data.getMatrixEntries()) {
                if (me.column() != 0) {
                    throw new IllegalStateException();
                }
                vector.set(me.row(), me.get());
            }
        } else if (Matrix.class.isAssignableFrom(matrixClass)) {
            Matrix mtjMat = (Matrix) matrix;
            for (MatrixEntry me : data.getMatrixEntries()) {
                mtjMat.set(me.row(), me.column(), me.get());
            }
        } else if (Matrix64F.class.isAssignableFrom(matrixClass)) {
            Matrix64F matrix64F = (Matrix64F) matrix;
            for (MatrixEntry me : data.getMatrixEntries()) {
                matrix64F.set(me.row(), me.column(), me.get());
            }
        } else {
            throw new IllegalStateException();
        }
        return matrix;
    }

    public static WrapperMFMatrix<Matrix> wrap(Matrix mat) {
        return new MTJMatrixWrapper(mat);
    }

    public static WrapperMFMatrix<Vector> wrap(Vector vec) {
        return new MTJVectorWrapper(vec);
    }

    public static WrapperMFMatrix<Matrix64F> wrap(Matrix64F mat) {
        return new EJMLMatrix64FWrapper(mat);
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