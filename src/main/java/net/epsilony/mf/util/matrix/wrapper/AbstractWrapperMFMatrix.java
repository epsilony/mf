/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractWrapperMFMatrix<T> implements WrapperMFMatrix<T> {

    protected T matrix;

    @Override
    public T getBackend() {
        return matrix;
    }
}
