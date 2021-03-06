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
import net.epsilony.mf.model.MFRectangle;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class RectangleToQuadranglePolygonIntegrateUnitGrid implements
        Function<MFRectangle, ArrayList<ArrayList<PolygonIntegrateUnit>>> {
    Function<? super MFRectangle, ? extends ArrayList<? extends ArrayList<double[]>>> rectangleToVertesGrid;

    @Override
    public ArrayList<ArrayList<PolygonIntegrateUnit>> apply(MFRectangle input) {

        ArrayList<? extends ArrayList<double[]>> vertesGrid = rectangleToVertesGrid.apply(input);
        ArrayList<ArrayList<PolygonIntegrateUnit>> result = new ArrayList<>(vertesGrid.size() - 1);
        for (int i = 0; i < vertesGrid.size(); i++) {
            ArrayList<PolygonIntegrateUnit> row = new ArrayList<>(vertesGrid.get(i).size() - 1);
            for (int j = 0; j < vertesGrid.get(i).size() - 1; j++) {
                row.add(newUnit(vertesGrid, i, j));
            }
        }
        return result;
    }

    private PolygonIntegrateUnit newUnit(ArrayList<? extends ArrayList<double[]>> vertesGrid, int i, int j) {
        PolygonIntegrateUnit result = new PolygonIntegrateUnit(4);
        result.setVertexCoord(0, vertesGrid.get(i).get(j));
        result.setVertexCoord(1, vertesGrid.get(i).get(j + 1));
        result.setVertexCoord(2, vertesGrid.get(i + 1).get(j + 1));
        result.setVertexCoord(3, vertesGrid.get(i).get(j + 1));
        return result;
    }

    public Function<? super MFRectangle, ? extends ArrayList<? extends ArrayList<double[]>>> getRectangleToVertesGrid() {
        return rectangleToVertesGrid;
    }

    public void setRectangleToVertesGrid(
            Function<? super MFRectangle, ? extends ArrayList<? extends ArrayList<double[]>>> rectangleToVertesGrid) {
        this.rectangleToVertesGrid = rectangleToVertesGrid;
    }

    public RectangleToQuadranglePolygonIntegrateUnitGrid() {
    }

    public RectangleToQuadranglePolygonIntegrateUnitGrid(
            Function<? super MFRectangle, ? extends ArrayList<? extends ArrayList<double[]>>> rectangleToVertesGrid) {
        this.rectangleToVertesGrid = rectangleToVertesGrid;
    }
}
