/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class BigDecimalTreeMapRowMatrix implements MFMatrix {

    ArrayList<TreeMap<Integer, BigDecimal>> rows;
    int numCols;

    public BigDecimalTreeMapRowMatrix(int numRows, int numCols) {
        if (numRows < 0 || numCols < 0) {
            throw new IllegalArgumentException();
        }
        this.numCols = numCols;
        rows = new ArrayList<>(numRows);
        for (int i = 0; i < numRows; i++) {
            rows.add(new TreeMap<Integer, BigDecimal>());
        }
    }

    @Override
    public int numRows() {
        return rows.size();
    }

    @Override
    public int numCols() {
        return numCols;
    }

    @Override
    public void set(int row, int col, double value) {
        set(row, col, BigDecimal.valueOf(value));
    }

    public void set(int row, int col, BigDecimal value) {
        checkCol(col);
        rows.get(row).put(col, value);
    }

    private void checkCol(int col) {
        if (col >= numCols || col < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void add(int row, int col, double value) {
        add(row, col, BigDecimal.valueOf(value));
    }

    public void add(int row, int col, BigDecimal value) {
        checkCol(col);
        TreeMap<Integer, BigDecimal> rowData = rows.get(row);
        BigDecimal old = rowData.get(col);
        if (null == old) {
            rowData.put(col, value);
        } else {
            rowData.put(col, old.add(value));
        }
    }

    @Override
    public double get(int row, int col) {
        BigDecimal value = getBigDecimal(row, col);
        return value.doubleValue();
    }

    public BigDecimal getBigDecimal(int row, int col) {
        checkCol(col);
        BigDecimal value = rows.get(row).get(col);
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
        return new BigDecimalTreeMapRowMatrixIterator();
    }

    class BigDecimalTreeMapRowMatrixIterator implements Iterator<MatrixEntry> {

        int row = 0;
        Map.Entry<Integer, BigDecimal> next = null;
        RawMatrixEntry rawMatrixEntry = new RawMatrixEntry();
        Iterator<Map.Entry<Integer, BigDecimal>> rowIterator;

        public BigDecimalTreeMapRowMatrixIterator() {
            if (numRows() < 1) {
                return;
            }
            rowIterator = rows.get(0).entrySet().iterator();
            findNextNonZero();
        }

        private void findNextNonZero() {
            do {
                while (rowIterator.hasNext()) {
                    next = rowIterator.next();
                    if (next.getValue().compareTo(BigDecimal.ZERO) != 0) {
                        return;
                    }
                }
                row++;
                if (row >= rows.size()) {
                    return;
                }
                rowIterator = rows.get(row).entrySet().iterator();
            } while (true);
        }

        @Override
        public MatrixEntry next() {
            rawMatrixEntry.setCol(next.getKey());
            rawMatrixEntry.setRow(row);
            rawMatrixEntry.setEntryValue(next.getValue().doubleValue());
            findNextNonZero();
            return rawMatrixEntry;
        }

        @Override
        public boolean hasNext() {
            return row < numRows();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
