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
package net.epsilony.mf.util.spring;

import java.util.Collection;

import net.epsilony.mf.util.event.EventBus;
import net.epsilony.mf.util.event.MethodEventBus;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class CollectionSizeEventBus implements EventBus {
    MethodEventBus innerEventBus = new MethodEventBus();

    @Override
    public void post(Object... values) {

        innerEventBus.post(getCollectionSize(values));
    }

    @Override
    public void postToNew(Object... values) {
        innerEventBus.postToNew(getCollectionSize(values));
    }

    private int getCollectionSize(Object[] values) {
        Collection<?> collection = (Collection<?>) values[0];
        return collection.size();
    }

    public void removeEmptyRegistryItems() {
        innerEventBus.removeEmptyRegistryItems();
    }

    public void registry(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        innerEventBus.registry(eventListener, methodName, parameterTypes);
    }

    public void registrySubEventBus(EventBus subBus) {
        innerEventBus.registrySubEventBus(subBus);
    }

    public void removeSubEventBus(EventBus subBus) {
        innerEventBus.removeSubEventBus(subBus);
    }

    public void remove(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        innerEventBus.remove(eventListener, methodName, parameterTypes);
    }

}
