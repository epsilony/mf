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
import java.util.LinkedList;
import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrixData;
import net.epsilony.mf.util.matrix.RawMatrixEntry;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MTJVectorWrapper extends AbstractWrapperMFMatrix<Vector> {

    public MTJVectorWrapper(Vector vec) {
        this.matrix = vec;
    }

    @Override
    public int numRows() {
        return matrix.size();
    }

    @Override
    public int numCols() {
        return 1;
    }

    @Override
    public void set(int row, int col, double value) {
        checkColumn(col);
        matrix.set(row, value);
    }

    @Override
    public double get(int row, int col) {
        checkColumn(col);
        return matrix.get(row);
    }

    @Override
    public void add(int row, int col, double value) {
        checkColumn(col);
        matrix.add(row, value);
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return new MFMatries.DenseMFMatrixIterator(this);
    }

    @Override
    public MFMatrixData genMatrixData() {
        MFMatrixData data = new MFMatrixData();
        data.setNumCols(1);
        data.setNumRows(matrix.size());
        LinkedList<MatrixEntry> entries = new LinkedList<>();
        for (VectorEntry ve : matrix) {
            entries.add(new RawMatrixEntry(ve));
        }
        data.setMatrixEntries(entries);
        data.setMatrixClass(matrix.getClass());
        return data;
    }

    private void checkColumn(int col) throws IllegalArgumentException {
        if (col != 0) {
            throw new IllegalArgumentException("for a vector wrapper the given col must be 0, not " + col);
        }
    }

    @Override
    public boolean isUpperSymmetric() {
        return false;
    }
}
