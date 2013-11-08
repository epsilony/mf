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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.epsilony.mf.util.matrix.wrapper.EJMLDenseMatrix64FWrapper;
import net.epsilony.mf.util.matrix.wrapper.EJMLMatrix64FWrapper;
import net.epsilony.mf.util.matrix.wrapper.MTJMatrixWrapper;
import net.epsilony.mf.util.matrix.wrapper.MTJVectorWrapper;
import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;

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

    public static Object allocateMatrix(MFMatrixData data) {
        Class<?> matrixClass = data.getMatrixClass();
        Object matrix = produceMatrix(data.getNumRows(), data.getNumCols(), matrixClass);
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
        } else if (MFMatrix.class.isAssignableFrom(matrixClass)) {
            MFMatrix mfMatrix = (MFMatrix) matrix;
            for (MatrixEntry me : data.getMatrixEntries()) {
                mfMatrix.set(me.row(), me.column(), me.get());
            }
        } else {
            throw new IllegalStateException();
        }
        return matrix;
    }

    @SuppressWarnings("unchecked")
    public static <T> T produceMatrix(int numRows, int numCols, Class<T> matrixClass) {
        T matrix;
        if (matrixClass.isAssignableFrom(double[][].class)) {
            return (T) new double[numRows][numCols];
        }
        try {
            Constructor<?> constructor;
            if (Vector.class.isAssignableFrom(matrixClass)) {
                constructor = matrixClass.getConstructor(int.class);
                if (numCols != 1) {
                    throw new IllegalStateException();
                }
                matrix = (T) constructor.newInstance(numRows);
            } else {
                constructor = matrixClass.getConstructor(int.class, int.class);
                matrix = (T) constructor.newInstance(numRows, numCols);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
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

    public static WrapperMFMatrix<DenseMatrix64F> wrap(DenseMatrix64F mat) {
        return new EJMLDenseMatrix64FWrapper(mat);
    }

    public static WrapperMFMatrix<DenseMatrix64F> wrap(double[][] data) {
        DenseMatrix64F denseMatrix64F = new DenseMatrix64F(data);
        return wrap(denseMatrix64F);
    }

    public static MFMatrix wrap(Object matrix, Class<?> matrixClass) {
        if (Matrix.class.isAssignableFrom(matrixClass)) {
            return wrap((Matrix) matrix);
        } else if (Vector.class.isAssignableFrom(matrixClass)) {
            return wrap((Vector) matrix);
        } else if (DenseMatrix64F.class.isAssignableFrom(matrixClass)) {
            return wrap((DenseMatrix64F) matrix);
        } else if (Matrix64F.class.isAssignableFrom(matrixClass)) {
            return wrap((Matrix64F) matrix);
        } else if (double[][].class.isAssignableFrom(matrixClass)) {
            return wrap((double[][]) matrix);
        }
        throw new IllegalArgumentException();
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
            throw new UnsupportedOperationException("Not supported yet."); // To
                                                                           // change
                                                                           // body
                                                                           // of
                                                                           // generated
                                                                           // methods,
                                                                           // choose
                                                                           // Tools
                                                                           // |
                                                                           // Templates.
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

    public static void main(String[] args) {
        WrapperMFMatrix<DenseMatrix64F> wrap = wrap(new double[][] { { 1, 2, 3 }, { 4, 5, 6 } });
        System.out.println("wrap = " + wrap);
    }
}
