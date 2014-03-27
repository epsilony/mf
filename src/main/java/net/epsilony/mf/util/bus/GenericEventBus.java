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

/**
 * @author Man YUAN <epsilon@epsilony.net>
 *
 */
public class GenericEventBus<T> {
    private final Class<T> type;

    private final MethodEventBus innerEventBus = new MethodEventBus();

    public void post(T value) {
        innerEventBus.post(value);
    }

    public void postToNew(T value) {
        innerEventBus.postToNew(value);
    }

    public void removeEmptyRegistryItems() {
        innerEventBus.removeEmptyRegistryItems();
    }

    public void register(Object eventListener, String methodName) {
        innerEventBus.register(eventListener, methodName, new Class[] { type });
    }

    public void registerRunnable(Object eventListener, String methodName) {
        innerEventBus.register(eventListener, methodName, new Class[0]);
    }

    public void registerSubEventBus(EventBus subBus) {
        innerEventBus.registerSubEventBus(subBus);
    }

    public void removeSubEventBus(EventBus subBus) {
        innerEventBus.removeSubEventBus(subBus);
    }

    public void remove(Object eventListener, String methodName) {
        innerEventBus.remove(eventListener, methodName, new Class[] { type });
    }

    public void removeRunnable(Object eventListener, String methodName) {
        innerEventBus.remove(eventListener, methodName, new Class[0]);
    }

    public GenericEventBus(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public EventBus eventBus() {
        return new EventBus() {

            @Override
            public void post(Object... values) {
                T value = checkAndExtractValues(values);
                GenericEventBus.this.post(value);

            }

            @Override
            public void postToNew(Object... values) {
                T value = checkAndExtractValues(values);
                GenericEventBus.this.postToNew(value);
            }

            private T checkAndExtractValues(Object[] values) {
                return type.cast(values[0]);
            }

        };
    }
}
