/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
