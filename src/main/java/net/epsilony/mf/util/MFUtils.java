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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFUtils {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void RudeAddTo(List src, List dst) {
        dst.addAll(src);
    }

    public static String singletonName(Class<?> singletonClass) {
        String simpleName = singletonClass.getSimpleName();
        StringBuilder builder = new StringBuilder();
        boolean atFirst = true;
        for (int i = 0; i < simpleName.length(); i++) {
            char c = simpleName.charAt(i);
            if (Character.isUpperCase(c) && atFirst) {
                builder.append(Character.toLowerCase(c));
            } else {
                atFirst = false;
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static GenericBeanDefinition rudeDefinition(Class<?> beanClass, Object... constructArgs) {
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(beanClass);
        ConstructorArgumentValues values = new ConstructorArgumentValues();
        for (Object arg : constructArgs) {
            values.addGenericArgumentValue(arg);
        }
        definition.setConstructorArgumentValues(values);
        return definition;
    }

    public static GenericBeanDefinition rudeListDefinition(Object... objects) {
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(ArrayList.class);
        ConstructorArgumentValues values = new ConstructorArgumentValues();
        values.addGenericArgumentValue((Arrays.asList(objects)));
        definition.setConstructorArgumentValues(values);
        return definition;
    }

    public static <K, V extends Serializable> Map<K, V> cloneMapWithSameKeys(Map<K, V> toBeCloned) {
        return cloneMapWithSameKeys(toBeCloned, new HashMap<K, V>());
    }

    public static <K, V extends Serializable> Map<K, V> cloneMapWithSameKeys(Map<K, V> toBeCloned, Map<K, V> resultMap) {
        Map<V, V> valueMapcloned = new HashMap<V, V>();

        for (Entry<K, V> entry : toBeCloned.entrySet()) {
            V value = entry.getValue();
            V clonedValue = valueMapcloned.get(value);
            if (null == clonedValue) {
                clonedValue = SerializationUtils.clone(value);
                valueMapcloned.put(value, clonedValue);
            }
            resultMap.put(entry.getKey(), clonedValue);
        }
        return resultMap;
    }
}
