/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import gnu.trove.map.hash.TIntByteHashMap;
import java.util.Iterator;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ByteHashRowMatrix implements MFMatrix {

    public static final int DEFAULT_ROW_CAPACITY = 30;

    TIntByteHashMap[] rows;
    int numCols;

    public ByteHashRowMatrix(int numRows, int numCols) {
        this.numCols = numCols;
        rows = new TIntByteHashMap[numRows];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new TIntByteHashMap(DEFAULT_ROW_CAPACITY);
        }
    }

    @Override
    public int numRows() {
        return rows.length;
    }

    @Override
    public int numCols() {
        return numCols;
    }

    public void set(int row, int col, byte value) {
        checkCol(col);
        rows[row].put(col, value);
    }

    private void checkCol(int col) throws IllegalArgumentException {
        if (col >= numCols) {
            throw new IllegalArgumentException();
        }
    }

    public void add(int row, int col, byte value) {
        checkCol(col);
        rows[row].adjustOrPutValue(col, value, value);
    }

    @Override
    public double get(int row, int col) {
        return getByte(row, col);
    }

    public byte getByte(int row, int col) {
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

    @Override
    public void set(int row, int col, double value) {
        set(row, col, (byte) value);
    }

    @Override
    public void add(int row, int col, double value) {
        add(row, col, (byte) value);
    }

    class HashRowsIterator implements Iterator<MatrixEntry> {

        int nextRow = 0;
        int nextColIndex = 0;
        int rowSize;
        int[] keys = new int[DEFAULT_ROW_CAPACITY];
        byte[] values = new byte[DEFAULT_ROW_CAPACITY];
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
            TIntByteHashMap row = rows[nextRow];
            rowSize = row.size();
            values = row.values(values);
            keys = row.keys(keys);
        }
    }

}
