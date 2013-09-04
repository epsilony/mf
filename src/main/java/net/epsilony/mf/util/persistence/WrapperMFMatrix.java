/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WrapperMFMatrix<T> extends MFMatrix {

    T getBackend();
}
