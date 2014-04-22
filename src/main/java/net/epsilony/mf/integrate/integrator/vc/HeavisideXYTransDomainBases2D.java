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

import net.epsilony.mf.util.math.ArrayPartialTuple.SingleArray;
import net.epsilony.mf.util.math.convention.Pds2;
import net.epsilony.mf.util.math.PartialTuple;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class HeavisideXYTransDomainBases2D implements TransDomainPartialVectorFunction {

    private int maxPartialOrder;
    private double scale;
    private double[] origin;
    public static final int SPATIAL_DIMENSION = 2;
    public static final int BASE_SIZE = 2;

    @Override
    public int size() {
        return BASE_SIZE;
    }

    @Override
    public int getSpatialDimension() {
        return SPATIAL_DIMENSION;
    }

    @Override
    public int getMaxPartialOrder() {
        return maxPartialOrder;
    }

    @Override
    public void setMaxPartialOrder(int maxPartialOrder) {
        this.maxPartialOrder = maxPartialOrder;
    }

    private SingleArray result;

    @Override
    public PartialTuple value(double[] coord) {
        if (result == null || result.getMaxPartialOrder() != maxPartialOrder) {
            result = new SingleArray(BASE_SIZE, SPATIAL_DIMENSION, maxPartialOrder);
        }
        double x = coord[0] - origin[0];
        double y = coord[1] - origin[1];

        double dist = x * x + y * y;

        if (dist < scale * scale) {
            result.set(0, 0, x);
            result.set(1, 0, y);
            if (maxPartialOrder == 1) {
                result.set(0, Pds2.U_x, 1);
                result.set(0, Pds2.U_y, 0);

                result.set(1, Pds2.U_x, 0);
                result.set(1, Pds2.U_y, 1);
            }
            if (maxPartialOrder > 1) {
                throw new UnsupportedOperationException();
            }
        } else {
            result.fill(0);
        }
        return result;
    }

    @Override
    public double getDomainScale() {
        return scale;
    }

    @Override
    public void setDomainScale(double scale) {
        this.scale = scale;
    }

    @Override
    public double[] getOrigin() {
        return origin;
    }

    @Override
    public void setOrigin(double[] origin) {
        this.origin = origin;
    }

}
