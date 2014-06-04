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
package net.epsilony.mf.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TypeProcessorMap {
    Map<Class<?>, Object> registry              = new LinkedHashMap<>();
    Map<Class<?>, Object> subClassRegistryCache = new HashMap<>();
    Class<?>              lastType;
    Object                lastProcessor;

    private void resetCache() {
        lastType = null;
        lastProcessor = null;
        subClassRegistryCache.clear();
    }

    public void register(Class<?> type, Object processor) {
        if (type == null || processor == null) {
            throw new IllegalArgumentException();
        }
        registry.put(type, processor);
        resetCache();
    }

    public void register(Class<?>[] types, Object processor) {
        for (Class<?> type : types) {
            register(type, processor);
        }
    }

    public void register(Map<? extends Class<?>, ? extends Object> typeProcessorMap) {
        for (Map.Entry<? extends Class<?>, ? extends Object> entry : typeProcessorMap.entrySet()) {
            register(entry.getKey(), entry.getValue());
        }
    }

    public void remove(Class<?> type) {
        registry.remove(type);
        resetCache();
    }

    public Object get(Class<?> type) {
        if (null == type) {
            throw new IllegalArgumentException();
        }
        if (lastType == type) {
            return lastProcessor;
        }
        Object processor = subClassRegistryCache.get(type);
        if (null == processor) {
            processor = registry.get(type);
        }

        if (null == processor) {
            Class<?> aimType = null;
            for (Class<?> key : registry.keySet()) {
                if (key.isAssignableFrom(type)) {
                    if (aimType == null || aimType.isAssignableFrom(key)) {
                        processor = registry.get(key);
                        aimType = key;
                    }
                }
            }
        }

        // if (processor == null) {
        // // throw new
        // // IllegalArgumentException("Can not find suitable processor for " +
        // // type + ", registed are: "
        // // + registry);
        // }

        lastType = type;
        lastProcessor = processor;
        if (null != processor) {
            subClassRegistryCache.put(type, processor);
        }
        return processor;
    }

    public Map<Class<?>, Object> getRegistryCopy() {
        return new HashMap<>(registry);
    }
}
