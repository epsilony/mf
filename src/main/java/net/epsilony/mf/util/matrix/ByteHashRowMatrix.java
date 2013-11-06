/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.epsilony.mf.util.matrix;

import gnu.trove.map.hash.TIntByteHashMap;
import java.util.Iterator;
import net.epsilony.tb.MiscellaneousUtils;
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
        if (numRows < 0 || numCols < 0) {
            throw new IllegalArgumentException();
        }
        this.numCols = numCols;
        rows = new TIntByteHashMap[numRows];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new TIntByteHashMap(DEFAULT_ROW_CAPACITY);
        }
    }

    public TIntByteHashMap getRow(int row) {
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

    private void checkCol(int col) throws IllegalArgumentException {
        if (col >= numCols || col < 0) {
            throw new IllegalArgumentException();
        }
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

    public void set(int row, int col, byte value) {
        checkCol(col);
        rows[row].put(col, value);
    }

    @Override
    public void add(int row, int col, double value) {
        add(row, col, (byte) value);
    }

    public void add(int row, int col, byte value) {
        checkCol(col);
        rows[row].adjustOrPutValue(col, value, value);
    }

    @Override
    public String toString() {
        return String.format("%s [%d * %d]", MiscellaneousUtils.simpleToString(this), numRows(), numCols());
    }

    class HashRowsIterator implements Iterator<MatrixEntry> {

        int nextRow = 0;
        int nextColIndex = -1;
        byte nextValue;
        int rowSize; //the real size of keys and values
        int[] keysMaybeTooLong = new int[DEFAULT_ROW_CAPACITY];
        byte[] valuesMaybeTooLong = new byte[DEFAULT_ROW_CAPACITY];
        RawMatrixEntry result = new RawMatrixEntry();

        public HashRowsIterator() {
            if (rows.length < 1) {
                return;
            }
            fetchRowData();
            fetchNextNonZero();
        }

        private void fetchRowData() {
            TIntByteHashMap row = rows[nextRow];
            rowSize = row.size();
            valuesMaybeTooLong = row.values(valuesMaybeTooLong);
            keysMaybeTooLong = row.keys(keysMaybeTooLong);
        }

        private void fetchNextNonZero() {
            nextColIndex++;
            do {
                while (nextColIndex < rowSize) {
                    nextValue = valuesMaybeTooLong[nextColIndex];
                    if (0 != nextValue) {
                        return;
                    }
                    nextColIndex++;
                }
                nextRow++;
                if (nextRow >= rows.length) {
                    return;
                }
                fetchRowData();
                nextColIndex = 0;
            } while (true);
        }

        @Override
        public boolean hasNext() {
            return nextRow < numRows();
        }

        @Override
        public MatrixEntry next() {
            int nextCol = keysMaybeTooLong[this.nextColIndex];
            result.set(nextValue);
            result.setCol(nextCol);
            result.setRow(nextRow);

            fetchNextNonZero();

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
