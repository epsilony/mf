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

package net.epsilony.mf.model;

import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class FacetModel extends RawPhysicalModel {

    public final static int DIMENSION = 2;

    public FacetModel() {
        spatialDimension = 2;
    }

    public static boolean checkPolygon(Facet polygon) {
        for (Segment seg : polygon) {
            if (!(seg instanceof Line) || !(seg.getStart() instanceof MFNode)) {
                return false;
            }
        }
        return true;
    }

    public Facet getFacet() {
        return getGeomRoot();
    }

    public void setFacet(Facet facet) {
        super.setGeomRoot(facet);
    }

    @Override
    public Facet getGeomRoot() {
        return (Facet) super.getGeomRoot();
    }

    @Override
    public void setGeomRoot(GeomUnit geomRoot) {
        setFacet((Facet) geomRoot);
    }
}
