/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawBigDecimalMatrixEntry implements BigDecimalMatrixEntry {

    int column, row;
    BigDecimal value;

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public int row() {
        return row;
    }

    @Override
    public BigDecimal get() {
        return value;
    }
}
