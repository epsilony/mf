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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class WeakBus<T> {

    private final LinkedList<Item<T>>                     registry = new LinkedList<>();
    private Supplier<? extends T>                         supplier = null;
    private final String                                  name;
    private final LinkedList<Item<Supplier<? extends T>>> subBuses = new LinkedList<>();

    public WeakBus(String name) {
        this.name = name;
    }

    public <K> void register(BiConsumer<? super K, ? super T> method, K obj) {
        registry.add(new Item<T>(obj, method));
        if (null != supplier) {
            method.accept(obj, supplier.get());
        }
    }

    public <K> void registerSubBus(BiConsumer<? super K, Supplier<? extends T>> method, K obj) {
        subBuses.add(new Item<Supplier<? extends T>>(obj, method));
        if (null != supplier) {
            method.accept(obj, supplier);
        }
    }

    public void postToEach(Supplier<? extends T> supplier) {
        this.supplier = supplier;

        postToRegistries();

        postToSubBuses();
    }

    private void postToRegistries() {
        Iterator<Item<T>> iterator = registry.iterator();
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
        }
    }

    private void postToSubBuses() {
        Iterator<Item<Supplier<? extends T>>> iterator = subBuses.iterator();
        while (iterator.hasNext()) {
            Item<Supplier<? extends T>> item = iterator.next();
            Object object = item.weakReference.get();
            if (null == object) {
                iterator.remove();
                continue;
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
            BiConsumer<Object, Supplier<? extends T>> method = (BiConsumer) item.method;
            method.accept(object, supplier);
        }
    }

    public void post(T value) {
        postToEach(() -> value);
    }

    public Supplier<? extends T> getSupplier() {
        return supplier;
    }

    public String getName() {
        return name;
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
