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

import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.unit.RectangleFacet;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.util.convertor.Convertor;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class RectangleFacetToQuadranglePolygonUnits implements
        Convertor<RectangleFacet, Iterable<? extends PolygonIntegrateUnit>> {
    Convertor<? super MFRectangle, ? extends Iterable<? extends PolygonIntegrateUnit>> rectangleToPolygonIntegrateUnits;

    @Override
    public Iterable<? extends PolygonIntegrateUnit> convert(RectangleFacet input) {
        Iterable<? extends PolygonIntegrateUnit> result = rectangleToPolygonIntegrateUnits
                .convert(input.getRectangle());
        for (PolygonIntegrateUnit pu : result) {
            pu.setEmbededIn(input.getFacet());
        }
        return result;

    }

    public RectangleFacetToQuadranglePolygonUnits(
            Convertor<? super MFRectangle, ? extends Iterable<? extends PolygonIntegrateUnit>> rectangleToPolygonIntegrateUnits) {
        this.rectangleToPolygonIntegrateUnits = rectangleToPolygonIntegrateUnits;
    }

    public RectangleFacetToQuadranglePolygonUnits() {
    }

    public Convertor<? super MFRectangle, ? extends Iterable<? extends PolygonIntegrateUnit>> getRectangleToPolygonIntegrateUnits() {
        return rectangleToPolygonIntegrateUnits;
    }

    public void setRectangleToPolygonIntegrateUnits(
            Convertor<? super MFRectangle, ? extends Iterable<? extends PolygonIntegrateUnit>> rectangleToPolygonIntegrateUnits) {
        this.rectangleToPolygonIntegrateUnits = rectangleToPolygonIntegrateUnits;
    }

}
