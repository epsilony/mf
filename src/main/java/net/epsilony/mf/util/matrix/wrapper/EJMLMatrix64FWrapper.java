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
import net.epsilony.mf.util.matrix.MFMatries;
import no.uib.cipr.matrix.MatrixEntry;
import org.ejml.data.Matrix64F;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class EJMLMatrix64FWrapper extends AbstractWrapperMFMatrix<Matrix64F> {

    public EJMLMatrix64FWrapper(Matrix64F mat) {
        this.matrix = mat;
    }

    @Override
    public int numRows() {
        return matrix.numRows;
    }

    @Override
    public int numCols() {
        return matrix.numCols;
    }

    @Override
    public void set(int row, int col, double value) {
        matrix.set(row, col, value);
    }

    @Override
    public double get(int row, int col) {
        return matrix.get(row, col);
    }

    @Override
    public Iterator<MatrixEntry> iterator() {
        return new MFMatries.DenseMFMatrixIterator(this);
    }

    @Override
    public Matrix64F getBackend() {
        return matrix;
    }

//    @Override
//    public MFMatrixData genMatrixData() {
//        MFMatrixData data = new MFMatrixData();
//        data.setNumCols(matrix.numCols);
//        data.setNumRows(matrix.numRows);
//        LinkedList<MatrixEntry> entries = new LinkedList<>();
//        for (MatrixEntry me : this) {
//            entries.add(me);
//        }
//        data.setMatrixEntries(entries);
//        data.setMatrixClass(matrix.getClass());
//        return data;
//    }
    @Override
    public void add(int row, int col, double value) {
        matrix.set(row, col, value + matrix.get(row, col));
    }

    @Override
    public boolean isUpperSymmetric() {
        return false;
    }
}
