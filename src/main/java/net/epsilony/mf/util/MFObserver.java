/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <D>
 */
public interface MFObserver<D> {

    void update(D data);
}
