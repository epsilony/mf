/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractWrapperMFMatrix<T> implements WrapperMFMatrix<T> {
    int id;
    T matrix;

    @Override
    public T getBackend() {
        return matrix;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
    
}
