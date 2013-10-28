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
public class BigDecimalTreeMapRowMatrix implements BigDecimalMFMatrix {

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

    @Override
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

    @Override
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

    @Override
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
        return new DoubleTreeMapRowMatrixIterator();
    }

    @Override
    public Iterator<BigDecimalMatrixEntry> bigDecimalIterator() {
        return new BigDecimalTreeMapRowMatrixIterator();
    }

    abstract class AbstractTreeMapRowMatrixIterator {

        int row = 0;
        Map.Entry<Integer, BigDecimal> next = null;
        Iterator<Map.Entry<Integer, BigDecimal>> rowIterator;

        public AbstractTreeMapRowMatrixIterator() {
            if (numRows() < 1) {
                return;
            }
            rowIterator = rows.get(0).entrySet().iterator();
            findNextNonZero();
        }

        final protected void findNextNonZero() {
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

        public boolean hasNext() {
            return row < numRows();
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    class DoubleTreeMapRowMatrixIterator extends AbstractTreeMapRowMatrixIterator implements Iterator<MatrixEntry> {

        RawMatrixEntry rawMatrixEntry = new RawMatrixEntry();

        @Override
        public MatrixEntry next() {
            rawMatrixEntry.setCol(next.getKey());
            rawMatrixEntry.setRow(row);
            rawMatrixEntry.setEntryValue(next.getValue().doubleValue());
            findNextNonZero();
            return rawMatrixEntry;
        }
    }

    class BigDecimalTreeMapRowMatrixIterator extends AbstractTreeMapRowMatrixIterator implements Iterator<BigDecimalMatrixEntry> {

        RawBigDecimalMatrixEntry rawMatrixEntry = new RawBigDecimalMatrixEntry();

        @Override
        public BigDecimalMatrixEntry next() {
            rawMatrixEntry.setColumn(next.getKey());
            rawMatrixEntry.setRow(row);
            rawMatrixEntry.setValue(next.getValue());
            findNextNonZero();
            return rawMatrixEntry;
        }
    }

}
