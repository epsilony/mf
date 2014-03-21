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

import java.util.function.Function;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.process.MFMixer;
import net.epsilony.mf.process.assembler.ShapeFunctionValue;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class GeomPointToShapeFunction implements Function<GeomPoint, ShapeFunctionValue> {

    MFMixer mixer;
    int diffOrder;

    @Override
    public ShapeFunctionValue apply(GeomPoint input) {
        mixer.setDiffOrder(diffOrder);
        mixer.setBoundary(input.getGeomUnit());
        mixer.setCenter(input.getCoord());
        mixer.setUnitOutNormal(null);
        return mixer.mix();
    }

    public GeomPointToShapeFunction() {
    }

    public GeomPointToShapeFunction(MFMixer mixer, int diffOrder) {
        this.mixer = mixer;
        this.diffOrder = diffOrder;
    }

    public MFMixer getMixer() {
        return mixer;
    }

    public void setMixer(MFMixer mixer) {
        this.mixer = mixer;
    }

    public int getDiffOrder() {
        return diffOrder;
    }

    public void setDiffOrder(int diffOrder) {
        this.diffOrder = diffOrder;
    }

}
