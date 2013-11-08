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

import java.util.LinkedList;
import net.epsilony.mf.util.matrix.MFMatrixData;
import net.epsilony.mf.util.matrix.RawMatrixEntry;
import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.MatrixEntry;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <T>
 */
public abstract class AbstractWrapperMFMatrix<T> implements WrapperMFMatrix<T> {

    protected T matrix;

    @Override
    public T getBackend() {
        return matrix;
    }

    @Override
    public MFMatrixData genMatrixData() {
        MFMatrixData data = new MFMatrixData();
        data.setNumCols(numCols());
        data.setNumRows(numRows());
        LinkedList<MatrixEntry> entries = new LinkedList<>();
        for (MatrixEntry me : this) {
            entries.add(new RawMatrixEntry(me));
        }
        data.setMatrixEntries(entries);
        data.setMatrixClass(matrix.getClass());
        return data;
    }

    @Override
    public String toString() {
        return String.format("%s{%s [%d * %d]}", MiscellaneousUtils.simpleToString(this),
                MiscellaneousUtils.simpleToString(matrix), numRows(), numCols());
    }
}
