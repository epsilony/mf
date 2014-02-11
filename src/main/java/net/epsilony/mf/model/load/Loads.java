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

import net.epsilony.mf.util.convertor.Convertor;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class Loads {
    private Loads() {

    }

    public static LoadValue constantLoadValue(double... values) {
        return new ArrayLoadValue(values);
    }

    public static LoadValue zeroLoadValue(int size) {
        return constantLoadValue(new double[size]);
    }

    public static DirichletLoadValue constantDirichletLoadValue(double[] values, boolean[] validities) {
        return new ArrayDirichletLoadValue(values, validities);
    }

    public static DirichletLoadValue constantDirichletLoadValue(double[] values, int[] validDims) {
        return constantDirichletLoadValue(values, genValidities(values.length, validDims));
    }

    public static DirichletLoadValue zeroDirichletLoadValue(boolean[] validities) {
        return constantDirichletLoadValue(new double[validities.length], validities);
    }

    public static DirichletLoadValue zeroDirichletLoadValue(int size, int... validDims) {
        return zeroDirichletLoadValue(genValidities(size, validDims));
    }

    public static boolean[] genValidities(int size, int... validDims) {
        boolean[] validities = new boolean[size];
        for (int i : validDims) {
            validities[i] = true;
        }
        return validities;
    }

    public static <T extends LoadValue> Load<T> spatialLoad(Convertor<double[], ? extends T> function,
            Class<T> loadValueClass) {
        return new FunctionalLoad<double[], T>(function, loadValueClass, LoadInputType.SPATIAL);
    }

    public static Load<LoadValue> constantLoad(double... values) {
        return new ConstantLoad<>(constantLoadValue(values));
    }

    public static Load<LoadValue> zeroLoad(int size) {
        return new ConstantLoad<>(zeroLoadValue(size));
    }

    public static Load<DirichletLoadValue> constantDirichletLoad(double[] values, boolean[] validities) {
        return new ConstantLoad<>(constantDirichletLoadValue(values, validities));
    }

    public static Load<DirichletLoadValue> constantDirichletLoad(double[] values, int[] validDims) {
        return new ConstantLoad<>(constantDirichletLoadValue(values, validDims));
    }

    public static Load<DirichletLoadValue> zeroDirichletLoad(boolean[] validities) {
        return new ConstantLoad<>(zeroDirichletLoadValue(validities));
    }

    public static Load<DirichletLoadValue> zeroDirichletLoad(int size, int... validDims) {
        return new ConstantLoad<>(zeroDirichletLoadValue(size, validDims));
    }
}
