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

import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.integrate.unit.RectangleFacet;
import net.epsilony.mf.model.MFRectangle;
import java.util.function.Function;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class RectangleFacetToQuadranglePolygonUnits implements
        Function<RectangleFacet, Iterable<? extends PolygonIntegrateUnit>> {
    Function<? super MFRectangle, ? extends Iterable<? extends PolygonIntegrateUnit>> rectangleToPolygonIntegrateUnits;

    @Override
    public Iterable<? extends PolygonIntegrateUnit> apply(RectangleFacet input) {
        Iterable<? extends PolygonIntegrateUnit> result = rectangleToPolygonIntegrateUnits
                .apply(input.getRectangle());
        for (PolygonIntegrateUnit pu : result) {
            pu.setEmbededIn(input.getFacet());
        }
        return result;

    }

    public RectangleFacetToQuadranglePolygonUnits(
            Function<? super MFRectangle, ? extends Iterable<? extends PolygonIntegrateUnit>> rectangleToPolygonIntegrateUnits) {
        this.rectangleToPolygonIntegrateUnits = rectangleToPolygonIntegrateUnits;
    }

    public RectangleFacetToQuadranglePolygonUnits() {
    }

    public Function<? super MFRectangle, ? extends Iterable<? extends PolygonIntegrateUnit>> getRectangleToPolygonIntegrateUnits() {
        return rectangleToPolygonIntegrateUnits;
    }

    public void setRectangleToPolygonIntegrateUnits(
            Function<? super MFRectangle, ? extends Iterable<? extends PolygonIntegrateUnit>> rectangleToPolygonIntegrateUnits) {
        this.rectangleToPolygonIntegrateUnits = rectangleToPolygonIntegrateUnits;
    }

}
