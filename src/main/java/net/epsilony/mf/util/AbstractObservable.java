/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <T>
 * @param <D>
 */
public abstract class AbstractObservable<T extends MFObserver<D>, D> implements MFObservable<T, D> {

    ArrayList<T> observers = new ArrayList<>();

    public AbstractObservable() {
    }

    public void apprise(D data) {
        for (T observer : observers) {
            observer.update(data);
        }
    }

    @Override
    public boolean addObserver(T observer) {
        if (null == observer) {
            throw new IllegalArgumentException();
        }
        return observers.add(observer);
    }

    @Override
    public boolean addObservers(Collection<? extends T> c) {
        return observers.addAll(c);
    }

    @Override
    public boolean removeObserver(T observer) {
        return observers.remove(observer);
    }

    @Override
    public List<T> getObservers() {
        return observers;
    }

    @Override
    public void removeObservers() {
        observers.clear();
    }

}
