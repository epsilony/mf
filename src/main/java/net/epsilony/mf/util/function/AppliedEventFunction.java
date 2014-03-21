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
package net.epsilony.mf.util.function;

import java.util.function.Function;

import net.epsilony.mf.util.event.EventBus;
import net.epsilony.mf.util.event.MethodEventBus;

/**
 * @author epsilon
 *
 */
public class AppliedEventFunction<T, R> implements Function<T, R> {
    private final Function<? super T, ? extends R> function;
    private final MethodEventBus methodEventBus = new MethodEventBus();

    @Override
    public R apply(T t) {
        R result = function.apply(t);
        methodEventBus.post(result);
        return result;
    }

    public AppliedEventFunction(Function<? super T, ? extends R> function) {
        super();
        this.function = function;
    }

    public void register(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        methodEventBus.register(eventListener, methodName, parameterTypes);
    }

    public void registerSubEventBus(EventBus subBus) {
        methodEventBus.registerSubEventBus(subBus);
    }

    public void removeSubEventBus(EventBus subBus) {
        methodEventBus.removeSubEventBus(subBus);
    }

    public void remove(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        methodEventBus.remove(eventListener, methodName, parameterTypes);
    }

}
