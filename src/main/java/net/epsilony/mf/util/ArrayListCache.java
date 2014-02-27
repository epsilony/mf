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

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ArrayListCache<T> {
    SoftReference<ArrayList<T>> softReference;

    public ArrayList<T> get(int capacity) {
        ArrayList<T> arrayList;
        if (null == softReference) {
            arrayList = null;
        } else {
            arrayList = softReference.get();
        }

        if (null == arrayList) {
            arrayList = new ArrayList<>(capacity);
            softReference = new SoftReference<ArrayList<T>>(arrayList);
        }
        return arrayList;
    }

    public ArrayList<T> get() {
        return get(0);
    }

}
