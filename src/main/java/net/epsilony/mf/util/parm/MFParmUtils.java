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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.epsilony.mf.util.parm.ann.MFParmIgnore;
import net.epsilony.mf.util.parm.ann.MFParmNullPolicy;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmUtils {

    public static boolean isNullPermitted(Class<?> cls) {
        MFParmNullPolicy nullPolicy = cls.getAnnotation(MFParmNullPolicy.class);
        return null != nullPolicy && nullPolicy.permit() == true;
    }

    public static boolean isNullPermitted(boolean defaultPermit, Method method) {
        MFParmNullPolicy nullPolicy = method.getAnnotation(MFParmNullPolicy.class);
        boolean permitNull = null != nullPolicy ? nullPolicy.permit() : defaultPermit;
        return permitNull;
    }

    public static Map<String, Method> searchParameterNameToSetter(Class<?> cls) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(cls);
        Map<String, Method> result = new HashMap<>();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            Method writeMethod = descriptor.getWriteMethod();
            if (null == writeMethod || writeMethod.isAnnotationPresent(MFParmIgnore.class)) {
                continue;
            }

            String parameterName = descriptor.getName();

            result.put(parameterName, writeMethod);
        }
        return result;
    }

    public static Map<String, Method> searchParameterNameToGetter(Class<?> cls) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(cls);
        Map<String, Method> result = new HashMap<>();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            Method readMethod = descriptor.getReadMethod();
            if (null == readMethod || readMethod.isAnnotationPresent(MFParmIgnore.class)) {
                continue;
            }

            String parameterName = descriptor.getName();

            result.put(parameterName, readMethod);
        }
        return result;
    }

    // public static boolean registryToBusPool(Object pool, String
    // parameterName, Object registry) {
    // Method busPoolMethod = searchBusPool(pool.getClass());
    // WeakBus<Object> weakBus = null;
    // try {
    // @SuppressWarnings("unchecked")
    // WeakBus<Object> ret = (WeakBus<Object>) busPoolMethod.invoke(pool,
    // parameterName);
    // weakBus = ret;
    // } catch (IllegalAccessException | IllegalArgumentException |
    // InvocationTargetException e) {
    // throw new IllegalArgumentException(e);
    // }
    // if (null == weakBus) {
    // return false;
    // }
    //
    // PropertyDescriptor descriptor = null;
    // try {
    // descriptor = PropertyUtils.getPropertyDescriptor(registry,
    // parameterName);
    // } catch (IllegalAccessException | InvocationTargetException |
    // NoSuchMethodException e) {
    // return false;
    // }
    // Method writeMethod = descriptor.getWriteMethod();
    // if (null == writeMethod) {
    // return false;
    // }
    // weakBus.register((obj, value) -> {
    // try {
    // writeMethod.invoke(obj, value);
    // } catch (Exception e) {
    // throw new IllegalStateException(e);
    // }
    // }, registry);
    //
    // return true;
    // }
}
