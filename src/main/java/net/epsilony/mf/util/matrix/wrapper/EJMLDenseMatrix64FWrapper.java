/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

import java.util.Iterator;
import net.epsilony.mf.util.matrix.MFMatries;
import no.uib.cipr.matrix.MatrixEntry;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class EJMLDenseMatrix64FWrapper extends AbstractWrapperMFMatrix<DenseMatrix64F> {

    public EJMLDenseMatrix64FWrapper(DenseMatrix64F denseMatrix64F) {
        this.matrix = denseMatrix64F;
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
    public void add(int row, int col, double value) {
        matrix.add(row, col, value);
    }

    @Override
    public double get(int row, int col) {
        return matrix.get(row, col);
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return new MFMatries.DenseMFMatrixIterator(this);
    }

    @Override
    public boolean isUpperSymmetric() {
        return false;
    }
}
