/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.io.Serializable;
import no.uib.cipr.matrix.MatrixEntry;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFMatrix extends Iterable<MatrixEntry>, Serializable {

    int numRows();

    int numCols();

    void set(int row, int col, double value);

    void add(int row, int col, double value);

    double get(int row, int col);

    boolean isUpperSymmetric();
}
