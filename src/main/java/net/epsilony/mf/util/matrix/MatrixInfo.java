/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import net.epsilony.mf.util.MFConstants;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MatrixInfo {

    int numRows, numCols, entriesSize;

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public int getEntriesSize() {
        return entriesSize;
    }

    public void setEntitesSize(int entitesSize) {
        this.entriesSize = entitesSize;
    }

    boolean isRecommandedDense() {
        return isRecommandedDense(numRows, numCols, entriesSize);
    }

    public static boolean isRecommandedDense(int numRows, int numCols, int entriesSize) {
        int fullSize = numRows * numCols;
        double ratio = entriesSize / (double) fullSize;
        if (ratio < MFConstants.RECOMMANDED_DENSE_MATRIX_RATIO_LIMIT) {
            return false;
        } else {
            return true;
        }
    }
}
