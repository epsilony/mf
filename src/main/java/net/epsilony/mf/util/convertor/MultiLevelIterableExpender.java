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
package net.epsilony.mf.util.convertor;

import com.google.common.collect.Iterables;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class MultiLevelIterableExpender<T> implements Convertor<Iterable<?>, Iterable<T>> {
    public static final int DEFAULT_INPUT_LEVEL = 2;

    int numLevels = DEFAULT_INPUT_LEVEL;

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<T> convert(Iterable<?> multiLevelIterable) {
        if (numLevels == 1) {
            return (Iterable<T>) multiLevelIterable;
        }
        Iterable<?> result = Iterables.concat((Iterable<? extends Iterable<?>>) multiLevelIterable);
        for (int i = 3; i <= numLevels; i++) {
            result = Iterables.concat((Iterable<? extends Iterable<?>>) result);
        }
        return (Iterable<T>) result;
    }

    public MultiLevelIterableExpender(int numLevels) {
        this.numLevels = numLevels;
    }

    public MultiLevelIterableExpender() {
    }

    public int getNumLevels() {
        return numLevels;
    }

    public void setNumLevels(int inputLevel) {
        if (inputLevel < 1) {
            throw new IllegalArgumentException();
        }
        this.numLevels = inputLevel;
    }
}
