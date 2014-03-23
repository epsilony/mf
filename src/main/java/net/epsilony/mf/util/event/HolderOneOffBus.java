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

import java.util.function.Consumer;

import com.google.common.base.Supplier;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 *
 */
public class HolderOneOffBus<T> {
    private boolean holdingValue = false;
    private T value;
    private final OneOffConsumerBus<T> oneOffConsumerBus = new OneOffConsumerBus<>();
    private final Supplier<T> supplier = () -> value;

    public void registry(Consumer<? super T> consumer) {
        if (holdingValue) {
            consumer.accept(value);
        } else {
            oneOffConsumerBus.registry(consumer);
        }
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (holdingValue) {
            throw new IllegalArgumentException("the value can only be set once");
        }
        this.value = value;
        holdingValue = true;
        oneOffConsumerBus.post(value);
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

    public static void main(String[] args) {
        HolderOneOffBus<Integer> holderOneOffBus = new HolderOneOffBus<>();
        holderOneOffBus.setValue(10);
        Integer integer = holderOneOffBus.supplier.get();
        System.out.println(integer);
    }
}
