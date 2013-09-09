/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix.wrapper;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractWrapperMFMatrix<T> implements WrapperMFMatrix<T> {

    protected T matrix;
    protected boolean backendReallocatable = true;

    @Override
    public boolean isBackendReallocatable() {
        return backendReallocatable;
    }

    @Override
    public void setBackendReallocatable(boolean backendReallocatable) {
        this.backendReallocatable = backendReallocatable;
    }

    @Override
    public T getBackend() {
        return matrix;
    }
}
