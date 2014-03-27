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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ConsumerBus<T> implements Poster<T>, EachPoster<T>, ConsumerRegistry<T> {
    private boolean autoPostLastToFresh = true;
    private boolean clearFuturePosted = false;
    private final Deque<Consumer<? super T>> registry = new ArrayDeque<>();
    private final Deque<Consumer<? super T>> freshRegistry = new ArrayDeque<>();
    private Supplier<? extends T> last = null;

    public boolean isClearFuturePosted() {
        return clearFuturePosted;
    }

    public void setClearFuturePosted(boolean clearFuturePosted) {
        this.clearFuturePosted = clearFuturePosted;
    }

    public boolean isAutoPostLastToFresh() {
        return autoPostLastToFresh;
    }

    public void setAutoPostLastToFresh(boolean autoPostLastToFresh) {
        this.autoPostLastToFresh = autoPostLastToFresh;
    }

    @Override
    public void register(Consumer<? super T> consumer) {
        freshRegistry.add(consumer);
        if (last != null && autoPostLastToFresh) {
            postToEachFresh(last);
        }
    }

    @Override
    public void clearRegistry() {
        registry.clear();
        freshRegistry.clear();
        last = null;
    }

    private void forEachConsumer(Supplier<? extends T> supplier, boolean onlyFresh) {
        Consumer<? super Consumer<? super T>> consumer = (c) -> c.accept(supplier.get());
        if (!onlyFresh) {
            registry.stream().forEachOrdered(consumer);
            if (clearFuturePosted) {
                registry.clear();
            }
        }
        freshRegistry.stream().forEachOrdered(consumer);
        if (!clearFuturePosted) {
            registry.addAll(freshRegistry);
        }
        freshRegistry.clear();
        last = supplier;
    }

    @Override
    public void post(T value) {
        postToEach(() -> value);
    }

    @Override
    public void postToFresh(T value) {
        postToEachFresh(() -> value);
    }

    @Override
    public void postToEach(Supplier<? extends T> supplier) {
        forEachConsumer(supplier, false);
    }

    @Override
    public void postToEachFresh(Supplier<? extends T> supplier) {
        forEachConsumer(supplier, true);
    }
}
