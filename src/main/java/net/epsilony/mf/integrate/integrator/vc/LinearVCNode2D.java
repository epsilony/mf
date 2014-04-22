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
package net.epsilony.mf.integrate.integrator.vc;

import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.math.PartialValue;
import net.epsilony.mf.util.math.convention.Pds2;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class LinearVCNode2D extends AbstractVCNode {

    static final int SPATIAL_DIMENSION = 2;
    static final int BASES_SIZE = 2;

    public LinearVCNode2D(int assemblyIndex) {
        super(assemblyIndex);
    }

    public LinearVCNode2D() {
        super();
    }

    @Override
    public void volumeIntegrate(double[] coord, PartialValue shapeFunction, PartialTuple basesValue, double weight) {

        vector.add(0, 0, -shapeFunction.get(Pds2.U_x) * weight);
        vector.add(1, 0, -shapeFunction.get(Pds2.U_y) * weight);

        if (basesValue.size() != matrix.numRows) {
            throw new IllegalStateException();
        }

        for (int index = 0; index < basesValue.size(); index++) {
            matrix.add(0, index, basesValue.get(index, Pds2.U_x) * weight);
            matrix.add(1, index, basesValue.get(index, Pds2.U_y) * weight);
        }

    }

    @Override
    public void boundaryIntegrate(double[] coord, PartialValue shapeFunction, PartialTuple basesValue,
            double weight, double[] unitOutNormal) {
        double sv = weight * shapeFunction.get(0);
        vector.add(0, 0, sv * unitOutNormal[0]);
        vector.add(1, 0, sv * unitOutNormal[1]);

        if (basesValue.size() != matrix.numRows) {
            throw new IllegalStateException();
        }
        for (int index = 0; index < basesValue.size(); index++) {
            double bv = -weight * basesValue.get(index, 0);
            matrix.add(0, index, bv * unitOutNormal[0]);
            matrix.add(1, index, bv * unitOutNormal[1]);

        }

    }

    @Override
    protected int getBasesSize() {
        return BASES_SIZE;
    }

    @Override
    protected int getSpatialDimension() {
        return SPATIAL_DIMENSION;
    }

}
