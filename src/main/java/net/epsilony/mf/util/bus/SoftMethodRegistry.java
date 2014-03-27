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

import static net.epsilony.mf.util.bus.VarargsMethodBus.EMPTY_TYPES;

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
 * @author Man YUAN <epsilonyuan@gmail.com>
 * 
 */
public abstract class SoftMethodRegistry implements MethodRegistry {

    protected final Map<Key, Method> listenerRegistry = new LinkedHashMap<>();
    protected final Set<Key> newRegistied = new HashSet<>();
    private final ArrayListCache<Key> tempKeyList = new ArrayListCache<>();
    protected Class<?>[] parameterTypes;

    public void removeEmptyRegistryItems() {
        ArrayList<Key> emptyItems = tempKeyList.get();
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

    @Override
    public void register(Object postListener, String methodName) {

        Method method = MethodUtils.getMatchingAccessibleMethod(postListener.getClass(), methodName, parameterTypes);

        if (null == method) {
            method = MethodUtils.getMatchingAccessibleMethod(postListener.getClass(), methodName, EMPTY_TYPES);
        }

        if (null == method) {
            throw new IllegalArgumentException(String.format(
                    "EventListener %s does not have a method called %s with parameter types: %s or zero parameters",
                    postListener, methodName, Arrays.toString(parameterTypes)));
        }
        Key key = new Key(postListener, methodName);
        listenerRegistry.put(key, method);
        newRegistied.add(key);
    }

    @Override
    public void registerSubEventBus(VarargsPoster subBus) {
        Key key = new Key(subBus, null);
        listenerRegistry.put(key, null);
    }

    @Override
    public void removeSubEventBus(VarargsPoster subBus) {
        Key key = new Key(subBus, null);
        listenerRegistry.remove(key);
    }

    @Override
    public void remove(Object postListener, String methodName) {
        Key key = new Key(postListener, methodName);
        listenerRegistry.remove(key);
        newRegistied.remove(key);
    }

    protected void _post(boolean onlyPostToFresh) {
        ArrayList<Key> emptyKeys = tempKeyList.get();
        emptyKeys.clear();
        for (Entry<Key, Method> entry : listenerRegistry.entrySet()) {
            final Key key = entry.getKey();
            final Object object = key.getObject();
            if (null == object) {
                emptyKeys.add(key);
                continue;
            }

            if (object instanceof VarargsPoster && entry.getKey().methodName == null) {
                VarargsPoster subEventBus = (VarargsPoster) object;
                if (onlyPostToFresh) {
                    subEventBus.postToFresh(genValues());
                } else {
                    subEventBus.post(genValues());
                }
                continue;
            }

            if (onlyPostToFresh && !newRegistied.contains(key)) {
                continue;
            }

            final Object[] values = genValues();
            try {
                Method method = entry.getValue();
                if (method.getParameterTypes().length > 0) {
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

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    abstract protected Object[] genValues();

    protected class Key {
        private final WeakReference<Object> objectReference;
        final String methodName;
        final int objectHash;

        public Key(Object object, String methodName) {
            this.objectReference = new WeakReference<>(object);
            objectHash = (object == null) ? 0 : object.hashCode();
            this.methodName = methodName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
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

    protected SoftMethodRegistry(Class<?>... parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

}