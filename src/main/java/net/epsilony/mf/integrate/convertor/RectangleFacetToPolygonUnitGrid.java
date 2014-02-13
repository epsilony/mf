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
package net.epsilony.mf.integrate.convertor;

import java.util.ArrayList;

import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.unit.RectangleFacet;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.util.convertor.Convertor;

import com.google.common.collect.Iterables;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class RectangleFacetToPolygonUnitGrid implements
        Convertor<RectangleFacet, ArrayList<ArrayList<PolygonIntegrateUnit>>> {
    Convertor<MFRectangle, ArrayList<ArrayList<double[]>>> rectangleToVertesGridConvertor;
    Convertor<ArrayList<ArrayList<double[]>>, ArrayList<ArrayList<PolygonIntegrateUnit>>> vertesGridToPolygonIntegrateUnitGridConvertor = new NormalGridToPolygonUnitGrid();

    @Override
    public ArrayList<ArrayList<PolygonIntegrateUnit>> convert(RectangleFacet input) {
        ArrayList<ArrayList<double[]>> vertesGrid = rectangleToVertesGridConvertor.convert(input.getRectangle());
        ArrayList<ArrayList<PolygonIntegrateUnit>> result = vertesGridToPolygonIntegrateUnitGridConvertor
                .convert(vertesGrid);
        for (PolygonIntegrateUnit pu : Iterables.concat(result)) {
            pu.setEmbededIn(input.getFacet());
        }
        return result;
    }

    public RectangleFacetToPolygonUnitGrid() {
    }

    public RectangleFacetToPolygonUnitGrid(
            Convertor<MFRectangle, ArrayList<ArrayList<double[]>>> rectangleToVertesGridConvertor) {
        this.rectangleToVertesGridConvertor = rectangleToVertesGridConvertor;
    }

    public Convertor<MFRectangle, ArrayList<ArrayList<double[]>>> getRectangleToVertesGridConvertor() {
        return rectangleToVertesGridConvertor;
    }

    public void setRectangleToVertesGridConvertor(
            Convertor<MFRectangle, ArrayList<ArrayList<double[]>>> rectangleToVertesGridConvertor) {
        this.rectangleToVertesGridConvertor = rectangleToVertesGridConvertor;
    }

}
