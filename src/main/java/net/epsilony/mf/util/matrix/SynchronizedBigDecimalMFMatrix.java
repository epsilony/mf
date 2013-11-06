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

import java.math.BigDecimal;
import java.util.Iterator;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SynchronizedBigDecimalMFMatrix implements BigDecimalMFMatrix{
    private final BigDecimalMFMatrix matrix;

    public SynchronizedBigDecimalMFMatrix(BigDecimalMFMatrix matrix) {
        this.matrix = matrix;
    }

    @Override
    synchronized public void add(int row, int col, BigDecimal value) {
        matrix.add(row, col, value);
    }

    @Override
    synchronized public BigDecimal getBigDecimal(int row, int col) {
        return matrix.getBigDecimal(row, col);
    }

    @Override
    synchronized public void set(int row, int col, BigDecimal value) {
        matrix.set(row, col, value);
    }

    @Override
    public Iterator<BigDecimalMatrixEntry> bigDecimalIterator() {
        return matrix.bigDecimalIterator();
    }

    @Override
    synchronized public int numRows() {
        return matrix.numRows();
    }

    @Override
    synchronized public int numCols() {
        return matrix.numCols();
    }

    @Override
    synchronized public void set(int row, int col, double value) {
        matrix.set(row, col, value);
    }

    @Override
    synchronized public void add(int row, int col, double value) {
        matrix.add(row, col, value);
    }

    @Override
    synchronized public double get(int row, int col) {
        return matrix.get(row, col);
    }

    @Override
    synchronized public boolean isUpperSymmetric() {
        return matrix.isUpperSymmetric();
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return matrix.iterator();
    }
}
