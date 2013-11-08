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
import javax.persistence.Column;
import javax.persistence.Transient;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.VectorEntry;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawMatrixEntry implements MatrixEntry, Serializable {

    int row;
    int col;
    double entryValue;

    public RawMatrixEntry(VectorEntry ve) {
        col = 0;
        row = ve.index();
        entryValue = ve.get();
    }

    public RawMatrixEntry(MatrixEntry me) {
        row = me.row();
        col = me.column();
        entryValue = me.get();
    }

    public RawMatrixEntry(int row, int col, double value) {
        this.row = row;
        this.col = col;
        this.entryValue = value;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public double getEntryValue() {
        return entryValue;
    }

    public void setEntryValue(double value) {
        this.entryValue = value;
    }

    public RawMatrixEntry() {
    }

    @Column(name = "mat_row", nullable = false)
    public int getRow() {
        return row();
    }

    @Override
    public int row() {
        return row;
    }

    @Column(name = "mat_col", nullable = false)
    public int getCol() {
        return col;
    }

    @Override
    public int column() {
        return col;
    }

    @Transient
    @Override
    public double get() {
        return entryValue;
    }

    @Override
    public void set(double value) {
        this.entryValue = value;
    }

    @Override
    public String toString() {
        return "RawMatrixEntry{" + "row=" + row + ", col=" + col + ", value=" + entryValue + '}';
    }
}
