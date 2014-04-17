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
package net.epsilony.mf.integrate.util;

import java.util.ArrayList;
import java.util.function.Function;

import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class NormalGridToPolygonUnitGrid implements
        Function<ArrayList<ArrayList<double[]>>, ArrayList<ArrayList<PolygonIntegrateUnit>>> {

    private Object loadKey;

    public Object getLoadKey() {
        return loadKey;
    }

    public void setLoadKey(Object loadKey) {
        this.loadKey = loadKey;
    }

    @Override
    public ArrayList<ArrayList<PolygonIntegrateUnit>> apply(ArrayList<ArrayList<double[]>> input) {
        int numRows = input.size() - 1;
        int numCols = input.get(0).size() - 1;
        ArrayList<ArrayList<PolygonIntegrateUnit>> results = new ArrayList<>(numRows);
        for (int row = 0; row < numRows; row++) {
            ArrayList<PolygonIntegrateUnit> quadRow = new ArrayList<>(numCols);
            results.add(quadRow);
            for (int col = 0; col < numCols; col++) {
                PolygonIntegrateUnit quad = new PolygonIntegrateUnit(4);
                quad.setLoadKey(loadKey);
                quad.setVertexCoord(0, input.get(row).get(col));
                quad.setVertexCoord(1, input.get(row).get(col + 1));
                quad.setVertexCoord(2, input.get(row + 1).get(col + 1));
                quad.setVertexCoord(3, input.get(row + 1).get(col));

                quadRow.add(quad);
            }
        }
        return results;
    }
}
