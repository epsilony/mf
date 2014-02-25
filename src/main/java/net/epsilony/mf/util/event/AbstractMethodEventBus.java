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

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.epsilony.mf.util.ArrayListCache;

import org.apache.commons.beanutils.MethodUtils;

/**
 * @author epsilon
 * 
 */
public abstract class AbstractMethodEventBus {

    protected Map<Key, Method> listenerRegistry = new LinkedHashMap<>();
    protected Set<Key> newRegistied = new HashSet<>();
    protected ArrayListCache<Key> emptyRegistryKeyCache = new ArrayListCache<>();
    protected boolean onlyPostToNew = false;

    protected void removeEmptyRegistryItems() {
        ArrayList<Key> emptyItems = emptyRegistryKeyCache.get();
        emptyItems.clear();
        for (Key key : listenerRegistry.keySet()) {
            if (key.getObject() == null) {
                emptyItems.add(key);
            }
        }
        for (Key emptyKey : emptyItems) {
            listenerRegistry.remove(emptyKey);
        }
    }

    protected class Key {
        private final WeakReference<Object> objectReference;
        final String methodName;
        final Class<?>[] parameterTypes;
        final int objectHash;

        public Key(Object object, String methodName, Class<?>[] argTypes) {
            this.objectReference = new WeakReference<>(object);
            objectHash = (object == null) ? 0 : object.hashCode();
            this.methodName = methodName;
            this.parameterTypes = argTypes;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(parameterTypes);
            result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
            result = prime * result + objectHash;
            return result;
        }

        public Object getObject() {
            return objectReference.get();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Key other = (Key) obj;
            if (!Arrays.equals(parameterTypes, other.parameterTypes))
                return false;
            if (methodName == null) {
                if (other.methodName != null)
                    return false;
            } else if (!methodName.equals(other.methodName))
                return false;

            Object object = getObject();
            Object otherObject = other.getObject();
            if (object == null) {
                if (otherObject != null)
                    return false;
            } else if (!object.equals(otherObject))
                return false;
            return true;
        }
    }

    public void registry(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        Method method = MethodUtils.getMatchingAccessibleMethod(eventListener.getClass(), methodName, parameterTypes);
        if (null == method) {
            throw new IllegalArgumentException(String.format(
                    "EventListener %s does not have a method called %s with parameterTypes %s", eventListener,
                    methodName, Arrays.toString(parameterTypes)));
        }
        Key key = new Key(eventListener, methodName, parameterTypes);
        listenerRegistry.put(key, method);
        newRegistied.add(key);

    }

    public void remove(Object eventListener, String methodName, Class<?>[] parameterTypes) {
        Key key = new Key(eventListener, methodName, parameterTypes);
        listenerRegistry.remove(key);
        newRegistied.remove(key);
    }

    protected void _post() {
        ArrayList<Key> emptyKeys = emptyRegistryKeyCache.get();
        emptyKeys.clear();
        for (Entry<Key, Method> entry : listenerRegistry.entrySet()) {
            final Key key = entry.getKey();
            final Object object = key.getObject();
            if (null == object) {
                emptyKeys.add(key);
                continue;
            }
            if (onlyPostToNew && !newRegistied.contains(key)) {
                continue;
            }
            final Object[] values = genValues();
            try {
                if (key.parameterTypes.length > 0) {
                    entry.getValue().invoke(object, values);
                } else {
                    entry.getValue().invoke(object);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(String.format("Object:%s method:%s fail with %s", object,
                        entry.getValue(), Arrays.toString(values)), e);
            }
            newRegistied.remove(key);
        }

        for (Key emptyKey : emptyKeys) {
            listenerRegistry.remove(emptyKey);
            newRegistied.remove(emptyKey);
        }
    }

    protected abstract Object[] genValues();

}