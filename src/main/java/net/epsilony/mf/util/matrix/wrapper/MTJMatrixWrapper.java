/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import net.epsilony.mf.util.matrix.MFMatrixData;
import net.epsilony.mf.util.matrix.RawMatrixEntry;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MTJMatrixWrapper extends AbstractWrapperMFMatrix<Matrix> {

    public MTJMatrixWrapper(Matrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public int getNumRows() {
        return matrix.numRows();
    }

    @Override
    public int getNumCols() {
        return matrix.numColumns();
    }

    @Override
    public void setEntry(int row, int column, double value) {
        matrix.set(row, column, value);
    }

    @Override
    public double getEntry(int row, int column) {
        return matrix.get(row, column);
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return matrix.iterator();
    }

    @Override
    public MFMatrixData getMatrixData() {
        MFMatrixData data = new MFMatrixData();
        data.setNumCols(matrix.numColumns());
        data.setNumRows(matrix.numRows());
        LinkedList<RawMatrixEntry> entries = new LinkedList<>();
        for (MatrixEntry me : matrix) {
            entries.add(new RawMatrixEntry(me));
        }
        data.setMatrixEntries(entries);
        return data;
    }

    @Override
    public void setMatrixData(MFMatrixData data) {
        if (matrix.numColumns() != data.getNumCols() || matrix.numRows() != data.getNumRows()) {
            if (isBackendReallocatable()) {
                try {
                    Constructor<? extends Matrix> constructor = matrix.getClass().getConstructor(int.class, int.class);
                    matrix = constructor.newInstance(data.getNumRows(), data.getNumCols());
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new IllegalStateException(ex);
                }
            } else {
                throw new IllegalStateException("backend is not reallocatable");
            }
        } else {
            matrix.zero();
        }
        for (MatrixEntry me : data.getMatrixEntries()) {
            matrix.set(me.row(), me.column(), me.get());
        }
    }
}
