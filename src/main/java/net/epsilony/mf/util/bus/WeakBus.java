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
package net.epsilony.mf.util.bus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class WeakBus<T> implements Poster<T>, EachPoster<T>, BiConsumerRegistry<T> {
    private boolean                        autoPostLastToFresh = true;
    private boolean                        clearFuturePosted   = false;
    private final LinkedList<Item<T>>      registry            = new LinkedList<>();
    private final LinkedList<Item<T>>      freshRegistry       = new LinkedList<>();
    private Supplier<? extends T>          last                = null;
    private final String                   name;
    private final List<WeakBus<? super T>> subBuses            = new ArrayList<>();

    public WeakBus(String name) {
        this.name = name;
    }

    @Override
    public <K> void register(BiConsumer<? super K, ? super T> method, K obj) {
        freshRegistry.add(new Item<T>(obj, method));
        if (autoPostLastToFresh && null != last) {
            _postToEach(last, true);
        }
    }

    @Override
    public void clear() {
        registry.clear();
        freshRegistry.clear();
    }

    private void _postToEach(Supplier<? extends T> supplier, boolean onlyFresh) {
        last = supplier;

        if (!onlyFresh) {
            postToCollection(registry, supplier);
        }
        postToCollection(freshRegistry, supplier);
        registry.addAll(freshRegistry);
        freshRegistry.clear();

        for (WeakBus<? super T> subBus : subBuses) {
            subBus.postToEach(supplier);
        }
    }

    private void postToCollection(Iterable<Item<T>> iterable, Supplier<? extends T> supplier) {
        Iterator<Item<T>> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            Item<T> item = iterator.next();
            Object object = item.weakReference.get();
            if (null == object) {
                iterator.remove();
                continue;
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
            BiConsumer<Object, ? super T> method = (BiConsumer) item.method;
            T value = supplier.get();
            method.accept(object, value);

            if (clearFuturePosted) {
                iterator.remove();
            }
        }
    }

    @Override
    public void postToFresh(T value) {
        postToEachFresh(() -> value);

    }

    @Override
    public void postToEachFresh(Supplier<? extends T> supplier) {
        _postToEach(supplier, true);
    }

    @Override
    public void postToEach(Supplier<? extends T> supplier) {
        _postToEach(supplier, false);
    }

    @Override
    public void post(T value) {
        postToEach(() -> value);
    }

    public boolean isAutoPostLastToFresh() {
        return autoPostLastToFresh;
    }

    public boolean isClearFuturePosted() {
        return clearFuturePosted;
    }

    public void setAutoPostLastToFresh(boolean autoPostLastToFresh) {
        this.autoPostLastToFresh = autoPostLastToFresh;
    }

    public void setClearFuturePosted(boolean clearFuturePosted) {
        this.clearFuturePosted = clearFuturePosted;
    }

    public Supplier<? extends T> getLast() {
        return last;
    }

    public String getName() {
        return name;
    }

    public boolean addSubBus(WeakBus<? super T> e) {
        return subBuses.add(e);
    }

    public void clearSubBus() {
        subBuses.clear();
    }

    private static class Item<T> {
        private final WeakReference<Object>    weakReference;
        private final BiConsumer<?, ? super T> method;

        public Item(Object obj, BiConsumer<?, ? super T> method) {
            this.weakReference = new WeakReference<Object>(obj);
            this.method = method;
        }
    }
}
