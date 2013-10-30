/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import net.epsilony.tb.Factory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <T>
 */
public interface MatrixFactory<T> extends Factory<T> {

    void setNumRows(int numRows);

    void setNumCols(int numCols);

    @Override
    T produce();
}
