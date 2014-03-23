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

/**
 * @author Man YUAN <epsilon@epsilony.net>
 *
 */
public class HolderGenericBus<T> {
    private boolean holdingValue = false;
    private T value;
    private final GenericEventBus<T> innerBus;

    public HolderGenericBus(Class<T> type) {
        innerBus = new GenericEventBus<>(type);
    }

    public Class<T> getType() {
        return innerBus.getType();
    }

    public void post(T value) {
        innerBus.post(value);
        this.value = value;
        holdingValue = true;
    }

    public void register(Object eventListener, String methodName) {
        innerBus.register(eventListener, methodName);
        if (holdingValue) {
            innerBus.postToNew(value);
        }
    }

    public void registerRunnable(Object eventListener, String methodName) {
        innerBus.registerRunnable(eventListener, methodName);
        if (holdingValue) {
            innerBus.postToNew(value);
        }
    }

    public void registerSubEventBus(EventBus subBus) {
        innerBus.registerSubEventBus(subBus);
        if (holdingValue) {
            innerBus.postToNew(value);
        }
    }

    public void removeSubEventBus(EventBus subBus) {
        innerBus.removeSubEventBus(subBus);
    }

    public void remove(Object eventListener, String methodName) {
        innerBus.remove(eventListener, methodName);
    }

    public void removeRunnable(Object eventListener, String methodName) {
        innerBus.removeRunnable(eventListener, methodName);
    }

    public EventBus eventBus() {
        return new EventBus() {

            @Override
            public void post(Object... values) {
                T value = checkAndExtractValues(values);
                HolderGenericBus.this.post(value);

            }

            @Override
            public void postToNew(Object... values) {
                post(values);
            }

            private T checkAndExtractValues(Object[] values) {
                return getType().cast(values[0]);
            }
        };
    }

    public boolean isHoldingValue() {
        return holdingValue;
    }

    public T getValue() {
        return value;
    }

}
