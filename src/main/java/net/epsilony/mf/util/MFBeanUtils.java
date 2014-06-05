/*
0 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import net.epsilony.mf.util.parm.MFParmOptional;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFBeanUtils {

    public static void transmitProperties(Object src, Object dest) {
        transmitProperties(src, dest, null);
    }

    public static void transmitProperties(Object src, Object dest, Logger logger) {
        BeanMap srcMap = new BeanMap(src);
        BeanMap destMap = new BeanMap(dest);
        Map<String, Object> record = null;
        if (null != logger) {
            record = new LinkedHashMap<>();
        }
        for (PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(dest)) {
            if (descriptor.getWriteMethod() != null) {
                String name = descriptor.getName();
                Object value = srcMap.get(name);
                if (null != value) {
                    destMap.put(name, value);
                    if (null != logger) {
                        record.put(name, value);
                    }
                }
            }
        }
        if (null != logger) {
            logger.info("transmit: {}", record);
        }
    }

    public static Stream<PropertyDescriptor> writablePropertyDescriptorStream(Object obj, boolean includeOptional) {
        return writablePropertyDescriptorStream(obj.getClass(), includeOptional);
    }

    public static Stream<PropertyDescriptor> writablePropertyDescriptorStream(Class<?> cls, boolean includeOptional) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(cls);
        return Arrays.stream(propertyDescriptors).filter(descriptor -> {
            Method writeMethod = descriptor.getWriteMethod();
            if (writeMethod == null) {
                return false;
            }
            if (!includeOptional && writeMethod.getAnnotation(MFParmOptional.class) != null) {
                return false;
            }
            return true;
        });
    }

}
