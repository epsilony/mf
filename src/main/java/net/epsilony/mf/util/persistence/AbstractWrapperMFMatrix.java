/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractWrapperMFMatrix<T> implements WrapperMFMatrix<T> {

    T matrix;

    @Override
    public T getBackend() {
        return matrix;
    }
}
