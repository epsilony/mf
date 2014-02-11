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

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ArrayDirichletLoadValue extends ArrayLoadValue implements DirichletLoadValue {
    boolean[] validities;

    public ArrayDirichletLoadValue() {
    }

    public ArrayDirichletLoadValue(double[] values, boolean[] validities) {
        super(values);
        if (values.length != validities.length) {
            throw new IllegalArgumentException();
        }
        this.validities = validities;
    }

    public boolean[] getValidities() {
        return validities;
    }

    public void setValidities(boolean[] validities) {
        this.validities = validities;
    }

    @Override
    public boolean validity(int dimIndex) {
        return validities[dimIndex];
    }

    @Override
    public int size() {
        if (validities.length != values.length) {
            throw new IllegalStateException();
        }
        return super.size();
    }

}
