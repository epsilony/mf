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
package net.epsilony.mf.util.proxy.parm;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.util.proxy.parm.ann.MFParmIgnore;
import net.epsilony.mf.util.proxy.parm.ann.MFParmOptional;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmIndexer {
    private Class<?>                        targetClass;
    private Map<String, PropertyDescriptor> nameToDescriptor;
    private Map<Method, PropertyDescriptor> getterToDescriptor;
    private Map<Method, PropertyDescriptor> setterToDescriptor;
    private Set<Method>                     optionalSetters;
    private Set<Method>                     ignoredSetters;
    private boolean                         defaultNullPolicy;

    public MFParmIndexer(Class<?> targetClass) {
        this.targetClass = targetClass;

        defaultNullPolicy = MFParmUtils.isNullPermitted(targetClass);

        nameToDescriptor = new LinkedHashMap<>();
        getterToDescriptor = new LinkedHashMap<>();
        setterToDescriptor = new LinkedHashMap<>();
        ignoredSetters = new LinkedHashSet<>();
        optionalSetters = new LinkedHashSet<>();
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(targetClass);
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            nameToDescriptor.put(descriptor.getName(), descriptor);
            Method readMethod = descriptor.getReadMethod();
            if (null != readMethod) {
                getterToDescriptor.put(readMethod, descriptor);
            }
            Method writeMethod = descriptor.getWriteMethod();
            if (null == writeMethod) {
                continue;
            }
            setterToDescriptor.put(writeMethod, descriptor);
            if (writeMethod.isAnnotationPresent(MFParmIgnore.class)) {
                ignoredSetters.add(writeMethod);
                continue;
            }

            setterToDescriptor.put(writeMethod, descriptor);
            if (writeMethod.isAnnotationPresent(MFParmOptional.class)) {
                optionalSetters.add(writeMethod);
            }

        }

        nameToDescriptor = Collections.unmodifiableMap(nameToDescriptor);
        getterToDescriptor = Collections.unmodifiableMap(getterToDescriptor);
        setterToDescriptor = Collections.unmodifiableMap(setterToDescriptor);
        ignoredSetters = Collections.unmodifiableSet(ignoredSetters);
        optionalSetters = Collections.unmodifiableSet(optionalSetters);

        checkInvalidMethodAnnotations();
    }

    private void checkInvalidMethodAnnotations() {
        for (Method method : targetClass.getMethods()) {
            if (method.isAnnotationPresent(MFParmOptional.class) && !optionalSetters.contains(method)) {
                throw new IllegalStateException("@" + MFParmOptional.class.getSimpleName()
                        + " should only be on not ignored setters, not " + method);
            }
            if (method.isAnnotationPresent(MFParmIgnore.class) && !ignoredSetters.contains(method)) {
                throw new IllegalStateException("@" + MFParmIgnore.class.getSimpleName()
                        + " should be only on setters, not " + method);
            }
        }
    }

    public boolean isSetter(Method method) {
        return setterToDescriptor.containsKey(method);
    }

    public Method getSetter(String name) {
        PropertyDescriptor descriptor = nameToDescriptor.get(name);
        if (null == descriptor) {
            return null;
        }
        return descriptor.getWriteMethod();
    }

    public boolean isGetter(Method method) {
        return getterToDescriptor.containsKey(method);
    }

    public Method getGetter(String name) {
        PropertyDescriptor descriptor = nameToDescriptor.get(name);
        if (null == descriptor) {
            return null;
        }
        return descriptor.getReadMethod();
    }

    public String getParmName(Method method) {
        PropertyDescriptor descriptor = getDiscriptor(method);
        if (null == descriptor) {
            return null;
        }
        return descriptor.getName();
    }

    public PropertyDescriptor getDiscriptor(Method method) {
        PropertyDescriptor descriptor = setterToDescriptor.get(method);
        if (null == descriptor) {
            descriptor = getterToDescriptor.get(method);
        }
        return descriptor;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public boolean isDefaultNullPermitted() {
        return defaultNullPolicy;
    }

    public boolean isNullPermitted(Method method) {
        if (!isSetter(method)) {
            throw new IllegalArgumentException(method + "is not a parameter setter");
        }
        return MFParmUtils.isNullPermitted(defaultNullPolicy, method);
    }

    public Map<String, PropertyDescriptor> getNameToDescriptor() {
        return nameToDescriptor;
    }

    public Map<Method, PropertyDescriptor> getGetterToDescriptor() {
        return getterToDescriptor;
    }

    public Map<Method, PropertyDescriptor> getSetterToDescriptor() {
        return setterToDescriptor;
    }

    public Set<Method> getOptionalSetters() {
        return optionalSetters;
    }

    public Set<Method> getIgnoredSetters() {
        return ignoredSetters;
    }
}
