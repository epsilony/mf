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
package net.epsilony.mf.integrate.unit;

import net.epsilony.mf.model.MFRectangle;
import net.epsilony.tb.solid.Facet;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class RectangleFacet {
    MFRectangle rectangle;
    Facet facet;

    public MFRectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(MFRectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Facet getFacet() {
        return facet;
    }

    public void setFacet(Facet facet) {
        this.facet = facet;
    }

}
