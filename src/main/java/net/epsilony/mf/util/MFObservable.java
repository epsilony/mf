/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <T>
 * @param <D>
 */
public interface MFObservable<T extends MFObserver<D>, D> {

    boolean addObserver(T observer);

    boolean addObservers(Collection<? extends T> c);

    void removeObservers();

    List<T> getObservers();

    boolean removeObserver(T observer);

}
