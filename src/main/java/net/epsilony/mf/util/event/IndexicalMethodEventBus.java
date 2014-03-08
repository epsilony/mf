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

import java.util.ArrayList;

/**
 * @author epsilon
 * 
 */
public class IndexicalMethodEventBus implements EventBus {
    ArrayList<MethodEventBus> methodEventBuses;

    public IndexicalMethodEventBus(int size) {
        methodEventBuses = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            methodEventBuses.add(new MethodEventBus());
        }
    }

    public void register(int index, Object object, String methodName, Class<?>[] parameterTypes) {
        methodEventBuses.get(index).register(object, methodName, parameterTypes);
    }

    public void remove(int index, Object object, String methodName, Class<?>[] parameterTypes) {
        methodEventBuses.get(index).remove(object, methodName, parameterTypes);
    }

    public void registerSubEventBus(int index, EventBus subBus) {
        methodEventBuses.get(index).registerSubEventBus(subBus);
    }

    public void removeSubEvenBus(int index, EventBus subBus) {
        methodEventBuses.get(index).removeSubEventBus(subBus);
    }

    @Override
    public void post(Object... values) {
        for (MethodEventBus meb : methodEventBuses) {
            meb.post(values);
        }
    }

    @Override
    public void postToNew(Object... values) {
        for (MethodEventBus meb : methodEventBuses) {
            meb.postToNew(values);
        }
    }

    public void post(int index, Object... values) {
        methodEventBuses.get(index).post(values);
    }

    public void postToNew(int index, Object... values) {
        methodEventBuses.get(index).postToNew(values);
    }

    public int size() {
        return methodEventBuses.size();
    }

}
