/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

import java.util.Iterator;
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
    public void add(int row, int col, double value) {
        matrix.add(row, col, value);
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return matrix.iterator();
    }

    @Override
    public boolean isUpperSymmetric() {
        return false;
    }
}
