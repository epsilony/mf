/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.math.BigDecimal;
import java.util.Iterator;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class BigDecimalDenseMatrix implements MFMatrix {

    BigDecimal[][] matrix;

    public BigDecimalDenseMatrix(int numRows, int numCols) {
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
        matrix[row][col] = BigDecimal.valueOf(value);
    }

    public void set(int row, int col, BigDecimal value) {
        matrix[row][col] = value;
    }

    @Override
    public void add(int row, int col, double value) {
        BigDecimal old = matrix[row][col];
        if (null == old) {
            matrix[row][col] = BigDecimal.valueOf(value);
        } else {
            matrix[row][col] = old.add(BigDecimal.valueOf(value));
        }
    }

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
        BigDecimal value = matrix[row][col];
        if (null == value) {
            return 0;
        }
        return value.doubleValue();
    }

    public BigDecimal getBigDecimal(int row, int col) {
        return matrix[row][col];
    }

    @Override
    public boolean isUpperSymmetric() {
        return false;
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return new BigDecimalDenseMatrixIterator();
    }

    class BigDecimalDenseMatrixIterator implements Iterator<MatrixEntry> {

        int row = 0;
        int col = 0;
        BigDecimal next = null;
        RawMatrixEntry rawMatrixEntry = new RawMatrixEntry();

        public BigDecimalDenseMatrixIterator() {
            findNextNonNull();
        }

        private void findNextNonNull() {
            while (row < matrix.length) {
                while (col < matrix[row].length) {
                    next = matrix[row][col];
                    if (next != null) {
                        return;
                    }
                    col++;
                }
                col = 0;
                row++;
            }
        }

        @Override
        public MatrixEntry next() {
            rawMatrixEntry.setCol(col);
            rawMatrixEntry.setRow(row);
            rawMatrixEntry.setEntryValue(next.doubleValue());
            findNextNonNull();
            return rawMatrixEntry;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
