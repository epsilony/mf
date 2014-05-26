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
package net.epsilony.mf.util.persist;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RecordUtils {

    public static Map<String, Object> readRecordFields(Object object) {
        Map<String, Object> result = new HashMap<>();
        Field[] allFields = FieldUtils.getAllFields(object.getClass());
        for (Field field : allFields) {
            Record annotation = field.getAnnotation(Record.class);
            if (annotation == null) {
                continue;
            }
            field.setAccessible(true);
            String name = annotation.name();
            if (name.isEmpty()) {
                name = field.getName();
            }
            try {
                result.put(name, field.get(object));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        return result;
    }

    public static void setRecordFields(Object object, Map<String, Object> valueMap) {

        Field[] allFields = FieldUtils.getAllFields(object.getClass());
        for (Field field : allFields) {
            Record annotation = field.getAnnotation(Record.class);
            if (annotation == null) {
                continue;
            }
            field.setAccessible(true);
            String name = annotation.name();
            if (name == null) {
                name = field.getName();
            }
            try {
                Object value = valueMap.get(name);
                if (value != null) {
                    field.set(object, value);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
