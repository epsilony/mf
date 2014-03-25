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
package net.epsilony.mf.util.event;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import org.apache.commons.math3.exception.NullArgumentException;

import com.google.common.base.Supplier;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SupplierOneOffBus<T> implements ConsumerRegistry<T> {

    private boolean immutable = true;
    private Supplier<? extends T> supplier;
    private final Deque<Consumer<? super T>> consumers = new ArrayDeque<>();

    @Override
    public void register(Consumer<? super T> consumer) {
        if (supplier != null) {
            consumer.accept(supplier.get());
        } else {
            consumers.push(consumer);
        }
    }

    @Override
    public void register(Runnable runnable) {
        if (supplier != null) {
            runnable.run();
        } else {
            consumers.push((a) -> runnable.run());
        }
    }

    public void setSupplierAndPost(Supplier<? extends T> supplier) {
        if (null == supplier) {
            throw new NullArgumentException();
        }
        if (null != this.supplier && immutable) {
            throw new IllegalArgumentException("the value can only be set once");
        }
        this.supplier = supplier;
        for (Consumer<? super T> consumer : consumers) {
            consumer.accept(supplier.get());
        }
    }

    public Supplier<? extends T> getSupplier() {
        return supplier;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }
}
