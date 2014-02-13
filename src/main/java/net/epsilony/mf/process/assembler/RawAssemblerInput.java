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
package net.epsilony.mf.process.assembler;

import net.epsilony.mf.model.load.LoadValue;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class RawAssemblerInput<T extends LoadValue> implements AssemblyInput<T> {

    double weight;
    T2Value ttValue;
    T loadValue;

    @Override
    public T2Value getT2Value() {
        return ttValue;
    }

    public void setTTValue(T2Value ttValue) {
        this.ttValue = ttValue;
    }

    public void setLoadValue(T loadValue) {
        this.loadValue = loadValue;
    }

    @Override
    public T getLoadValue() {
        return loadValue;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public RawAssemblerInput(double weight, T2Value ttValue, T loadValue) {
        this.weight = weight;
        this.ttValue = ttValue;
        this.loadValue = loadValue;
    }

    public RawAssemblerInput() {
    }
}
