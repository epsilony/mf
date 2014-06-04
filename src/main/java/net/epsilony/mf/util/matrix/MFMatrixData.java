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

import java.io.Serializable;
import java.util.List;

import no.uib.cipr.matrix.MatrixEntry;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFMatrixData implements Serializable {

    int               numRows;
    int               numCols;
    Class<?>          matrixClass;
    List<MatrixEntry> matrixEntries;
    int               id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Class<?> getMatrixClass() {
        return matrixClass;
    }

    public void setMatrixClass(Class<?> matrixClass) {
        this.matrixClass = matrixClass;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public List<MatrixEntry> getMatrixEntries() {
        return matrixEntries;
    }

    public void setMatrixEntries(List<MatrixEntry> matrixEntries) {
        this.matrixEntries = matrixEntries;
    }

    @Override
    public String toString() {
        return "MFMatrixData{" + "numRows=" + numRows + ", numCols=" + numCols + ", matrixClass=" + matrixClass
                + ", id=" + id + '}';
    }

}
