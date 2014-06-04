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

import java.util.ArrayList;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 *
 */
public class IndexcialMethodBus<T> {
    private final ArrayList<MethodBus<T>> innerBuses;
    private final Class<T>                type;

    public IndexcialMethodBus(Class<T> type, int size) {
        this.type = type;
        innerBuses = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            innerBuses.add(new MethodBus<>(type));
        }
    }

    public void register(int index, Object object, String methodName) {
        MethodBus<T> innerEventBus = innerBuses.get(index);
        innerEventBus.register(object, methodName);
    }

    public void remove(int index, Object object, String methodName) {
        MethodBus<T> innerEventBus = innerBuses.get(index);
        innerEventBus.remove(object, methodName);
    }

    public void registerSubEventBus(int index, VarargsPoster subBus) {
        MethodBus<T> innerEventBus = innerBuses.get(index);
        innerEventBus.registerSubEventBus(subBus);
    }

    public void removeSubEventBus(int index, VarargsPoster subBus) {
        MethodBus<T> innerEventBus = innerBuses.get(index);
        innerEventBus.removeSubEventBus(subBus);
    }

    public void post(T value) {
        for (MethodBus<T> innerEventBus : innerBuses) {
            innerEventBus.post(value);
        }
    }

    public void postToFresh(T value) {
        for (MethodBus<T> innerEventBus : innerBuses) {
            innerEventBus.postToFresh(value);
        }
    }

    public void post(int index, T value) {
        MethodBus<T> innerEventBus = innerBuses.get(index);
        innerEventBus.post(value);
    }

    public void postToNew(int index, T value) {
        MethodBus<T> innerEventBus = innerBuses.get(index);
        innerEventBus.postToFresh(value);
    }

    public int size() {
        return innerBuses.size();
    }

    public Class<T> getType() {
        return type;
    }
}
