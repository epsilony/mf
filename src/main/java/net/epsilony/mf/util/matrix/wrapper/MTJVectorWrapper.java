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
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MTJVectorWrapper extends AbstractWrapperMFMatrix<Vector> {

    public MTJVectorWrapper(Vector vec) {
        this.matrix = vec;
    }

    @Override
    public int getNumRows() {
        return matrix.size();
    }

    @Override
    public int getNumCols() {
        return 1;
    }

    @Override
    public void setEntry(int row, int col, double value) {
        if (col != 0) {
            throw new IllegalArgumentException("for a vector wrapper the given col must be 0, not " + col);
        }
        matrix.set(row, value);
    }

    @Override
    public double getEntry(int row, int col) {
        if (col != 0) {
            throw new IllegalArgumentException("for a vector wrapper the given col must be 0, not " + col);
        }
        return matrix.get(row);
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return new MFMatries.DenseMFMatrixIterator(this);
    }

    @Override
    public MFMatrixData getMatrixData() {
        MFMatrixData data = new MFMatrixData();
        data.setNumCols(1);
        data.setNumRows(matrix.size());
        LinkedList<RawMatrixEntry> entries = new LinkedList<>();
        for (VectorEntry ve : matrix) {
            entries.add(new RawMatrixEntry(ve));
        }
        data.setMatrixEntries(entries);
        return data;
    }

    @Override
    public void setMatrixData(MFMatrixData data) {
        if (data.getNumCols() != 1) {
            throw new IllegalArgumentException();
        }
        if (data.getNumRows() != matrix.size()) {
            if (backendReallocatable) {
                try {
                    Constructor<? extends Vector> constructor = matrix.getClass().getConstructor(int.class);
                    matrix = constructor.newInstance(data.getNumRows());
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        } else {
            matrix.zero();
        }
        for (MatrixEntry me : data.getMatrixEntries()) {
            if (me.column() != 0) {
                throw new IllegalStateException();
            }
            matrix.set(me.row(), me.get());
        }
    }
}
