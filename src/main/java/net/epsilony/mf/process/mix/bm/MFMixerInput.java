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
package net.epsilony.mf.process.mix.bm;

import net.epsilony.mf.model.geom.MFGeomUnit;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFMixerInput {

    private final MFGeomUnit boundary;
    private final double[] unitOutNormal, center;

    public MFMixerInput(MFGeomUnit boundary, double[] unitOutNormal, double[] center) {
        this.boundary = boundary;
        this.unitOutNormal = unitOutNormal;
        this.center = center;
    }

    public MFMixerInput(MFMixerInput toCopy) {
        this.boundary = toCopy.boundary;
        this.unitOutNormal = toCopy.unitOutNormal.clone();
        this.center = toCopy.center.clone();
    }

    public MFGeomUnit getBoundary() {
        return boundary;
    }

    public double[] getUnitOutNormal() {
        return unitOutNormal;
    }

    public double[] getCenter() {
        return center;
    }

}
