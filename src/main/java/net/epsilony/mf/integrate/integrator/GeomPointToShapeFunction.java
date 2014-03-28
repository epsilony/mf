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
package net.epsilony.mf.integrate.integrator;

import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.shape_func.ShapeFunctionValue;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class GeomPointToShapeFunction implements Function<GeomPoint, ShapeFunctionValue> {

    private final MFMixer mixer;

    @Override
    public ShapeFunctionValue apply(GeomPoint input) {
        mixer.setBoundary(input.getGeomUnit());
        mixer.setCenter(input.getCoord());
        mixer.setUnitOutNormal(null);
        return mixer.mix();
    }

    public GeomPointToShapeFunction(MFMixer mixer) {
        this.mixer = mixer;
    }

    public int getDiffOrder() {
        return mixer.getDiffOrder();
    }

    public void setDiffOrder(int diffOrder) {
        mixer.setDiffOrder(diffOrder);
    }

}
