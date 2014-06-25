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
package net.epsilony.mf.shape_func.bases;

import java.util.function.Supplier;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFMonomialBasesFactory implements Supplier<MFBases> {
    private int                          spatialDimension;
    private int                          degree;

    @SuppressWarnings("unchecked")
    private static Supplier<MFBases>[][] getters = new Supplier[][] {
            { MFMonomialBases.Linear1D::new, MFMonomialBases.Quadric1D::new, MFMonomialBases.Cubic1D::new },
            { MFMonomialBases.Linear2D::new, MFMonomialBases.Quadric2D::new, MFMonomialBases.Cubic2D::new } };

    public int getSpatialDimension() {
        return spatialDimension;
    }

    public void setSpatialDimension(int spatialDimension) {
        if (spatialDimension < 1 || spatialDimension > 2) {
            throw new IllegalArgumentException();
        }
        this.spatialDimension = spatialDimension;
    }

    public int getDegree() {
        return degree;
    }

    public void setMonomialDegree(int degree) {
        if (degree < 1 || degree > 3) {
            throw new IllegalArgumentException();
        }
        this.degree = degree;
    }

    @Override
    public MFBases get() {
        return getters[spatialDimension - 1][degree - 1].get();
    }

}
