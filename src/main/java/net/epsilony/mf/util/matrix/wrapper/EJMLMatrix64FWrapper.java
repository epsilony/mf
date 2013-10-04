/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

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
    public MFMatrixData genMatrixData() {
        MFMatrixData data = new MFMatrixData();
        data.setNumCols(matrix.numCols);
        data.setNumRows(matrix.numRows);
        LinkedList<RawMatrixEntry> entries = new LinkedList<>();
        for (MatrixEntry me : this) {
            entries.add((RawMatrixEntry) me);
        }
        data.setMatrixEntries(entries);
        data.setMatrixClass(matrix.getClass());
        return data;
    }
}
