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
package net.epsilony.mf.util.parm;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.epsilony.mf.util.bus.WeakBus;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmUtils {

    public static boolean isBusPoolMethod(Method method) {
        if (!method.isAnnotationPresent(MFParmBusPool.class)) {
            return false;
        }
        if (!isMethodFitBusPool(method)) {
            throw new IllegalStateException("method [" + method + "] is not fit for @"
                    + MFParmBusPool.class.getSimpleName());
        }
        return true;
    }

    public static boolean isMethodFitBusPool(Method method) {
        return method.getParameterCount() == 1 && WeakBus.class.isAssignableFrom(method.getReturnType())
                && method.getParameterTypes()[0].equals(String.class);
    }

    public static boolean isClassNullPermit(Class<?> cls) {
        MFParmNullPolicy nullPolicy = cls.getAnnotation(MFParmNullPolicy.class);
        return null != nullPolicy && nullPolicy.permit() == true;
    }

    public static boolean isMethodNullPermit(boolean defaultPermit, Method method) {
        MFParmNullPolicy nullPolicy = method.getAnnotation(MFParmNullPolicy.class);
        boolean permitNull = null != nullPolicy ? nullPolicy.permit() : defaultPermit;
        return permitNull;
    }

    public static Map<String, Method> findParameterNameToSetter(Class<?> cls) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(cls);
        Map<String, Method> result = new HashMap<>();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            Method writeMethod = descriptor.getWriteMethod();
            if (null == writeMethod || writeMethod.isAnnotationPresent(MFParmIgnore.class)) {
                continue;
            }
            result.put(descriptor.getName(), writeMethod);
        }
        return result;
    }

    public static Method findBusPoolMethod(Class<?> cls) {
        Method[] methods = cls.getMethods();
        Method busPoolMethod = null;
        for (Method method : methods) {
            if (!isBusPoolMethod(method)) {
                continue;
            }
            if (busPoolMethod != null) {
                throw new IllegalStateException(cls + "@" + MFParmBusPool.class + " is not unique");
            }
            busPoolMethod = method;
        }
        return busPoolMethod;
    }

    public static boolean registryToBusPool(Object pool, String parameterName, Object registry) {
        Method busPoolMethod = findBusPoolMethod(pool.getClass());
        WeakBus<Object> weakBus = null;
        try {
            @SuppressWarnings("unchecked")
            WeakBus<Object> ret = (WeakBus<Object>) busPoolMethod.invoke(pool, parameterName);
            weakBus = ret;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        if (null == weakBus) {
            return false;
        }

        PropertyDescriptor descriptor = null;
        try {
            descriptor = PropertyUtils.getPropertyDescriptor(registry, parameterName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return false;
        }
        Method writeMethod = descriptor.getWriteMethod();
        if (null == writeMethod) {
            return false;
        }
        weakBus.register((obj, value) -> {
            try {
                writeMethod.invoke(obj, value);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }, registry);

        return true;
    }
}
