/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.io.Serializable;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFMatrix extends Iterable<MatrixEntry>, Serializable {

    int getNumRows();

    int getNumCols();

    void setEntry(int row, int col, double value);

    double getEntry(int row, int col);
}
