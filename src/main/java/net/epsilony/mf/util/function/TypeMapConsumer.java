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

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import net.epsilony.mf.util.TypeProcessorMap;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TypeMapConsumer<T> implements Consumer<T> {
    private final TypeProcessorMap typeMap = new TypeProcessorMap();
    private Function<T, Class<?>> typeGetter = null;

    @SuppressWarnings("unchecked")
    @Override
    public void accept(T t) {
        Consumer<? super T> consumer;
        if (null == typeGetter) {
            consumer = (Consumer<? super T>) typeMap.get(t.getClass());
        } else {
            consumer = (Consumer<? super T>) typeMap.get(typeGetter.apply(t));
        }
        consumer.accept(t);
    }

    public void register(Class<?> type, Consumer<?> processor) {
        typeMap.register(type, processor);
    }

    public void register(Class<?>[] types, Consumer<?> processor) {
        typeMap.register(types, processor);
    }

    public void register(Map<? extends Class<?>, ? extends Consumer<?>> typeProcessorMap) {
        typeMap.register(typeProcessorMap);
    }

    public void remove(Class<?> type) {
        typeMap.remove(type);
    }

    public Consumer<?> get(Class<?> type) {
        return (Consumer<?>) typeMap.get(type);
    }

    public Map<Class<?>, Object> getRegistryCopy() {
        return typeMap.getRegistryCopy();
    }

    public Function<T, Class<?>> getTypeGetter() {
        return typeGetter;
    }

    public void setTypeGetter(Function<T, Class<?>> typeGetter) {
        this.typeGetter = typeGetter;
    }

}
