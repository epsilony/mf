/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Embeddable
public class RawMatrixEntry implements MatrixEntry, Serializable {

    int row;
    int col;
    double value;

    public RawMatrixEntry(int row, int col, double value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Column(name = "mat_value", nullable = false)
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
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
        return value;
    }

    @Override
    public void set(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RawMatrixEntry{" + "row=" + row + ", col=" + col + ", value=" + value + '}';
    }
}
