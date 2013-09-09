/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WrapperMFMatrix<T> extends MFMatrix {

    T getBackend();
}
