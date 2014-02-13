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

import java.util.Arrays;
import java.util.List;

import net.epsilony.mf.util.DataType;
import net.epsilony.mf.util.convertor.Convertor;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class FunctionalLoad<T, L extends LoadValue> implements Load<L> {

    private List<DataType> inputTypes;
    Convertor<T, ? extends L> loadFunction;
    Object value;
    Class<L> loadValueType;

    public FunctionalLoad(Convertor<T, ? extends L> loadFunction, Class<L> loadValueType, DataType inputType) {
        this.loadFunction = loadFunction;
        this.loadValueType = loadValueType;
        setInputType(inputType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public L getLoadValue() {
        if (inputTypes.get(0).getClass().isInstance(value)) {
            return loadFunction.convert((T) value);
        }
        throw new IllegalStateException();
    }

    @Override
    public List<DataType> getInputTypes() {
        return inputTypes;
    }

    @Override
    public void setInputValue(DataType type, Object value) {
        if (inputTypes.get(0) != type) {
            throw new IllegalArgumentException();
        }
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Convertor<T, ? extends L> getLoadFunction() {
        return loadFunction;
    }

    public void setLoadFunction(Convertor<T, L> loadFunction) {
        this.loadFunction = loadFunction;
    }

    public void setInputType(DataType type) {
        inputTypes = Arrays.asList(type);
    }

    @Override
    public Class<L> getLoadValueType() {
        return loadValueType;
    }

    public void setLoadValueType(Class<L> loadValueType) {
        this.loadValueType = loadValueType;
    }
}
