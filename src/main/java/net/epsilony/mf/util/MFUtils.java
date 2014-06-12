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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.SerializationUtils;

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

    public static <K, V> Map<K, LockableHolder<V>> lockablyWrapValues(Map<K, V> toBeWrapped) {
        return lockablyWrapValues(toBeWrapped, new HashMap<K, LockableHolder<V>>());
    }

    public static <K, V> Map<K, LockableHolder<V>> lockablyWrapValues(Map<K, V> toBeWrapped,
            Map<K, LockableHolder<V>> result) {

        Map<V, LockableHolder<V>> valueMapCloned = new HashMap<V, LockableHolder<V>>();

        for (Entry<K, V> entry : toBeWrapped.entrySet()) {
            V value = entry.getValue();
            LockableHolder<V> clonedValue = valueMapCloned.get(value);
            if (null == clonedValue) {
                clonedValue = new LockableHolder<V>(value);
                valueMapCloned.put(value, clonedValue);
            }
            result.put(entry.getKey(), clonedValue);
        }
        return result;
    }

    public static double[] linSpace(double start, double end, int numPt) {
        double d = end - start;
        double[] result = new double[numPt];
        double numD = numPt - 1;
        for (int i = 0; i < numPt; i++) {
            result[i] = start + d * (i / numD);
        }
        result[result.length - 1] = end;
        return result;
    }

    public static String simpToString(Object obj) {
        return String.format("%s@%x", obj.getClass().getSimpleName(), obj.hashCode());
    }
}
