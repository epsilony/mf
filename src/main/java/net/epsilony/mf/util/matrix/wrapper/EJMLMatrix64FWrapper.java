/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrixData;
import net.epsilony.mf.util.matrix.RawMatrixEntry;
import no.uib.cipr.matrix.MatrixEntry;
import org.ejml.data.Matrix64F;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class EJMLMatrix64FWrapper extends AbstractWrapperMFMatrix<Matrix64F> {

    public EJMLMatrix64FWrapper(Matrix64F mat) {
        this.matrix = mat;
    }

    @Override
    public int getNumRows() {
        return matrix.numRows;
    }

    @Override
    public int getNumCols() {
        return matrix.numCols;
    }

    @Override
    public void setEntry(int row, int col, double value) {
        matrix.set(row, col, value);
    }

    @Override
    public double getEntry(int row, int col) {
        return matrix.get(row, col);
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return new MFMatries.DenseMFMatrixIterator(this);
    }

    @Override
    public Matrix64F getBackend() {
        return matrix;
    }

    @Override
    public MFMatrixData getMatrixData() {
        MFMatrixData data = new MFMatrixData();
        data.setNumCols(matrix.numCols);
        data.setNumRows(matrix.numRows);
        LinkedList<RawMatrixEntry> entries = new LinkedList<>();
        for (MatrixEntry me : this) {
            entries.add((RawMatrixEntry) me);
        }
        data.setMatrixEntries(entries);
        return data;
    }

    @Override
    public void setMatrixData(MFMatrixData data) {
        if (matrix.numCols != data.getNumCols() || matrix.numRows != data.getNumRows()) {
            if (isBackendReallocatable()) {
                try {
                    Constructor<? extends Matrix64F> constructor = matrix.getClass().getConstructor(int.class, int.class);
                    matrix = constructor.newInstance(data.getNumRows(), data.getNumCols());
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new IllegalStateException(ex);
                }
            } else {
                throw new IllegalStateException("backend is not reallocatable");
            }
        } else {
            for (int i = 0; i < matrix.numRows; i++) {
                for (int j = 0; j < matrix.numCols; j++) {
                    matrix.set(i, j, 0);
                }
            }
        }
        for (MatrixEntry me : data.getMatrixEntries()) {
            matrix.set(me.row(), me.column(), me.get());
        }
    }
}
