/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.math.BigDecimal;
import java.util.Iterator;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class BigDecimalDenseMatrix implements BigDecimalMFMatrix {

    BigDecimal[][] matrix;

    public BigDecimalDenseMatrix(int numRows, int numCols) {
        if (numRows < 0 || numCols < 0) {
            throw new IllegalArgumentException();
        }
        matrix = new BigDecimal[numRows][numCols];
    }

    @Override
    public int numRows() {
        return matrix.length;
    }

    @Override
    public int numCols() {
        return matrix[0].length;
    }

    @Override
    public void set(int row, int col, double value) {
        set(row, col, new BigDecimal(value));
    }

    @Override
    public void set(int row, int col, BigDecimal value) {
        matrix[row][col] = value;
    }

    @Override
    public void add(int row, int col, double value) {
        add(row, col, new BigDecimal(value));
    }

    @Override
    public void add(int row, int col, BigDecimal value) {
        BigDecimal old = matrix[row][col];
        if (null == old) {
            matrix[row][col] = value;
        } else {
            matrix[row][col] = old.add(value);
        }
    }

    @Override
    public double get(int row, int col) {
        return getBigDecimal(row, col).doubleValue();
    }

    @Override
    public BigDecimal getBigDecimal(int row, int col) {
        BigDecimal value = matrix[row][col];
        if (null == value) {
            return BigDecimal.ZERO;
        }
        return value;
    }

    @Override
    public boolean isUpperSymmetric() {
        return false;
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return new DoubleDenseMatrixIterator();
    }

    @Override
    public Iterator<BigDecimalMatrixEntry> bigDecimalIterator() {
        return new BigDecimalDenseMatrixIterator();
    }

    abstract class AbstractBigDecimalDenseMatrixIterator {

        public AbstractBigDecimalDenseMatrixIterator() {
            findNextNonZero();
        }

        int row = 0;
        int col = -1;
        BigDecimal next = null;

        protected final void findNextNonZero() {
            col++;
            do {
                while (col < numCols()) {
                    next = matrix[row][col];
                    if (next != null && next.compareTo(BigDecimal.ZERO) != 0) {
                        return;
                    }
                    col++;
                }
                col = 0;
                row++;
            } while (row < numRows());
        }

        public boolean hasNext() {
            return row < numRows();
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    class DoubleDenseMatrixIterator extends AbstractBigDecimalDenseMatrixIterator implements Iterator<MatrixEntry> {

        RawMatrixEntry rawMatrixEntry = new RawMatrixEntry();

        @Override
        public MatrixEntry next() {
            rawMatrixEntry.setCol(col);
            rawMatrixEntry.setRow(row);
            rawMatrixEntry.setEntryValue(next.doubleValue());
            findNextNonZero();
            return rawMatrixEntry;
        }
    }

    class BigDecimalDenseMatrixIterator extends AbstractBigDecimalDenseMatrixIterator implements Iterator<BigDecimalMatrixEntry> {

        RawBigDecimalMatrixEntry rawMatrixEntry = new RawBigDecimalMatrixEntry();

        @Override
        public BigDecimalMatrixEntry next() {
            rawMatrixEntry.setColumn(col);
            rawMatrixEntry.setRow(row);
            rawMatrixEntry.setValue(next);
            findNextNonZero();
            return rawMatrixEntry;
        }
    }
}
