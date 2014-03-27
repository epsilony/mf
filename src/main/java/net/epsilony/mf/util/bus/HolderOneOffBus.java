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

import java.util.function.Consumer;

import com.google.common.base.Supplier;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 *
 */
public class HolderOneOffBus<T> implements ConsumerRegistry<T>, GenericOneOffDispatcher<T> {
    private boolean holdingValue = false;
    private boolean immutable = true;
    private T value;
    private final OneOffConsumerBus<T> oneOffConsumerBus = new OneOffConsumerBus<>();
    private final Supplier<T> supplier = () -> value;

    @Override
    public void register(Consumer<? super T> consumer) {
        if (holdingValue) {
            consumer.accept(value);
        } else {
            oneOffConsumerBus.register(consumer);
        }
    }

    @Override
    public void register(Runnable runnable) {
        if (holdingValue) {
            runnable.run();
        } else {
            oneOffConsumerBus.register(runnable);
        }
    }

    public T getValue() {
        return value;
    }

    @Override
    public void postToNew(T value) {
        if (holdingValue && immutable) {
            throw new IllegalArgumentException("the value can only be set once");
        }
        this.value = value;
        holdingValue = true;
        oneOffConsumerBus.postToNew(value);
    }

    public boolean isHoldingValue() {
        return holdingValue;
    }

    public Supplier<T> supplier() {
        if (holdingValue) {
            return supplier;
        } else {
            return null;
        }
    }

    public boolean isImmutable() {
        return immutable;
    }

    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }

    public static void main(String[] args) {
        HolderOneOffBus<Integer> holderOneOffBus = new HolderOneOffBus<>();
        holderOneOffBus.postToNew(10);
        Integer integer = holderOneOffBus.supplier.get();
        System.out.println(integer);
    }
}
