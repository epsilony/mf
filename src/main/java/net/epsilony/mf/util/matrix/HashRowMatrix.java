/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import gnu.trove.map.hash.TIntDoubleHashMap;
import java.util.Iterator;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class HashRowMatrix implements MFMatrix {

    public static final int DEFAULT_ROW_CAPACITY = 30;

    TIntDoubleHashMap[] rows;
    int numCols;

    public HashRowMatrix(int numRows, int numCols) {
        this.numCols = numCols;
        rows = new TIntDoubleHashMap[numRows];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new TIntDoubleHashMap(DEFAULT_ROW_CAPACITY);
        }
    }

    public TIntDoubleHashMap getRow(int row) {
        return rows[row];
    }

    @Override
    public int numRows() {
        return rows.length;
    }

    @Override
    public int numCols() {
        return numCols;
    }

    @Override
    public void set(int row, int col, double value) {
        checkCol(col);
        rows[row].put(col, value);
    }

    private void checkCol(int col) throws IllegalArgumentException {
        if (col >= numCols) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void add(int row, int col, double value) {
        checkCol(col);
        rows[row].adjustOrPutValue(col, value, value);
    }

    @Override
    public double get(int row, int col) {
        checkCol(col);
        return rows[row].get(col);
    }

    @Override
    public boolean isUpperSymmetric() {
        return false;
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return new HashRowsIterator();
    }

    class HashRowsIterator implements Iterator<MatrixEntry> {

        int nextRow = 0;
        int nextColIndex = 0;
        int rowSize;
        int[] keys = new int[DEFAULT_ROW_CAPACITY];
        double[] values = new double[DEFAULT_ROW_CAPACITY];
        RawMatrixEntry result = new RawMatrixEntry();

        public HashRowsIterator() {

        }

        @Override
        public boolean hasNext() {
            return nextRow < numRows();
        }

        @Override
        public MatrixEntry next() {
            if (0 == nextColIndex) {
                fetchRowData();
            }
            double value = values[nextColIndex];
            int nextCol = keys[nextColIndex];

            result.set(value);
            result.setCol(nextCol);
            result.setRow(nextRow);

            nextColIndex++;
            if (nextColIndex >= rowSize) {
                nextColIndex = 0;
                nextRow++;
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void fetchRowData() {
            TIntDoubleHashMap row = rows[nextRow];
            rowSize = row.size();
            values = row.values(values);
            keys = row.keys(keys);
        }
    }

}
