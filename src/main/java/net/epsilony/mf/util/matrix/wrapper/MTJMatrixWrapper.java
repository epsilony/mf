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
