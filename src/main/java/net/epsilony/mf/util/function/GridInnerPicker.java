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
package net.epsilony.mf.util.function;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class GridInnerPicker<T> implements Function<ArrayList<? extends ArrayList<T>>, ArrayList<ArrayList<T>>> {

    @Override
    public ArrayList<ArrayList<T>> apply(ArrayList<? extends ArrayList<T>> input) {
        ArrayList<ArrayList<T>> result = new ArrayList<>(input.size() >= 2 ? input.size() - 2 : 0);
        for (int i = 1; i < input.size() - 1; i++) {
            ArrayList<T> row = input.get(i);
            ArrayList<T> newRow = new ArrayList<>(row.size() - 2);
            result.add(newRow);
            for (int j = 1; j < row.size() - 1; j++) {
                newRow.add(row.get(j));
            }
        }
        return result;
    }

}
