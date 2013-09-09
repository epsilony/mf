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
import no.uib.cipr.matrix.Vector;

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
    public MFMatrixData genMatrixData() {
        MFMatrixData data = new MFMatrixData();
        data.setNumCols(matrix.numColumns());
        data.setNumRows(matrix.numRows());
        LinkedList<RawMatrixEntry> entries = new LinkedList<>();
        for (MatrixEntry me : matrix) {
            entries.add(new RawMatrixEntry(me));
        }
        data.setMatrixEntries(entries);
        data.setMatrixClass(matrix.getClass());
        return data;
    }
}
