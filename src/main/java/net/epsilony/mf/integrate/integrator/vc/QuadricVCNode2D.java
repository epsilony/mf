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
public class QuadricVCNode2D extends AbstractVCNode {
    static final int SPATIAL_DIMENSION = 2;
    static final int BASES_SIZE        = 6;

    public QuadricVCNode2D(int assemblyIndex) {
        super(assemblyIndex);
    }

    public QuadricVCNode2D() {
        super();
    }

    @Override
    public void volumeIntegrate(double[] coord, PartialValue shapeFunction, PartialTuple basesValue, double weight) {

        vector.add(0, 0, -shapeFunction.get(Pds2.U_x) * weight);
        vector.add(1, 0, -shapeFunction.get(Pds2.U_y) * weight);
        double x = coord[0], y = coord[1];
        vector.add(2, 0, -(shapeFunction.get(0) + x * shapeFunction.get(Pds2.U_x)) * weight);
        vector.add(3, 0, -shapeFunction.get(Pds2.U_x) * y * weight);
        vector.add(4, 0, -shapeFunction.get(Pds2.U_y) * x * weight);
        vector.add(5, 0, -(shapeFunction.get(0) + y * shapeFunction.get(Pds2.U_y)) * weight);
        if (basesValue.size() != matrix.numRows) {
            throw new IllegalStateException();
        }

        for (int index = 0; index < basesValue.size(); index++) {
            matrix.add(0, index, basesValue.get(index, Pds2.U_x) * weight);
            matrix.add(1, index, basesValue.get(index, Pds2.U_y) * weight);
            matrix.add(2, index, (basesValue.get(index, 0) + x * basesValue.get(index, Pds2.U_x)) * weight);
            matrix.add(3, index, basesValue.get(index, Pds2.U_x) * y * weight);
            matrix.add(4, index, basesValue.get(index, Pds2.U_y) * x * weight);
            matrix.add(5, index, (basesValue.get(index, 0) + y * basesValue.get(index, Pds2.U_y)) * weight);
        }
    }

    @Override
    public void boundaryIntegrate(double[] coord, PartialValue shapeFunction, PartialTuple basesValue, double weight,
            double[] unitOutNormal) {
        double sv = weight * shapeFunction.get(0);
        double nx = unitOutNormal[0];
        double ny = unitOutNormal[1];
        vector.add(0, 0, sv * nx);
        vector.add(1, 0, sv * ny);
        double x = coord[0], y = coord[1];
        vector.add(2, 0, x * nx * sv);
        vector.add(3, 0, y * nx * sv);
        vector.add(4, 0, x * ny * sv);
        vector.add(5, 0, y * ny * sv);
        if (basesValue.size() != matrix.numRows) {
            throw new IllegalStateException();
        }
        for (int index = 0; index < basesValue.size(); index++) {
            double bv = -weight * basesValue.get(index, 0);
            matrix.add(0, index, bv * nx);
            matrix.add(1, index, bv * ny);
            matrix.add(2, index, x * nx * bv);
            matrix.add(3, index, y * nx * bv);
            matrix.add(4, index, x * ny * bv);
            matrix.add(5, index, y * ny * bv);
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
