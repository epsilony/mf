/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <T>
 */
public class SynchronizedMatrixFactory<T> implements MatrixFactory<T> {

    private final MatrixFactory<T> innerFactory;

    public SynchronizedMatrixFactory(MatrixFactory<T> innerFactory) {
        this.innerFactory = innerFactory;
    }

    @Override
    synchronized public void setNumRows(int numRows) {
        innerFactory.setNumRows(numRows);
    }

    @Override
    synchronized public void setNumCols(int numCols) {
        innerFactory.setNumCols(numCols);
    }

    @Override
    synchronized public T produce() {
        return innerFactory.produce();
    }

    public static <T> MatrixFactory<T> wrap(MatrixFactory<T> factory) {
        return new SynchronizedMatrixFactory<>(factory);
    }
}
