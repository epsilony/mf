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

/**
 * @author Man YUAN <epsilon@epsilony.net>
 *
 */
public class OneOffConsumerBus<T> implements ConsumerRegistry<T>, GenericOneOffDispatcher<T> {

    Deque<Consumer<? super T>> consumers = new ArrayDeque<>();

    @Override
    public void postToNew(T value) {
        for (Consumer<? super T> consumer : consumers) {
            consumer.accept(value);
        }
    }

    @Override
    public void register(Consumer<? super T> consumer) {
        consumers.push(consumer);
    }

    @Override
    public void register(Runnable runnable) {
        consumers.push((a) -> runnable.run());
    }
}
