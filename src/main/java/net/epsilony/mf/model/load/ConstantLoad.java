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
package net.epsilony.mf.model.load;

import java.util.List;

import net.epsilony.mf.util.DataType;

import com.google.common.collect.ImmutableList;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ConstantLoad<T extends LoadValue> implements Load<T> {

    T loadValue;

    public static final List<DataType> loadTypes = ImmutableList.of();

    public ConstantLoad() {
    }

    public ConstantLoad(T loadValue) {
        this.loadValue = loadValue;
    }

    @Override
    public T getLoadValue() {
        return loadValue;
    }

    public void setLoadValue(T loadValue) {
        this.loadValue = loadValue;
    }

    @Override
    public List<DataType> getInputTypes() {
        return loadTypes;
    }

    @Override
    public void setInputValue(DataType type, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends LoadValue> getLoadValueType() {
        return loadValue.getClass();
    }
}
