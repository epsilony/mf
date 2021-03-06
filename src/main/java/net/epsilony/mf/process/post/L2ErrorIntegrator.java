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
package net.epsilony.mf.process.post;

import java.util.function.Consumer;
import java.util.function.Function;

import net.epsilony.mf.integrate.integrator.PolygonToGeomQuadraturePoints;
import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.util.math.ArrayPartialTuple.SingleArray;
import net.epsilony.mf.util.math.PartialTuple;

import org.apache.commons.math3.util.FastMath;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class L2ErrorIntegrator implements Consumer<GeomQuadraturePoint> {

    private Function<GeomPoint, PartialTuple> expFunction;
    private Function<GeomPoint, PartialTuple> actFunction;

    private SingleArray                       squareQuadrature;
    private final SqrtQuadratureValue         sqrtQuadrature = new SqrtQuadratureValue();

    public void setExpFunction(Function<GeomPoint, PartialTuple> expFunction) {
        this.expFunction = expFunction;
    }

    public void setActFunction(Function<GeomPoint, PartialTuple> actFunction) {
        this.actFunction = actFunction;
    }

    public class PolygonConsumer implements Consumer<PolygonIntegrateUnit> {

        private final PolygonToGeomQuadraturePoints polyToGqps = new PolygonToGeomQuadraturePoints();

        public int getDegree() {
            return polyToGqps.getQuadratureDegree();
        }

        public void setDegree(int degree) {
            polyToGqps.setQuadratureDegree(degree);
        }

        @Override
        public void accept(PolygonIntegrateUnit t) {
            polyToGqps.apply(t).forEach(L2ErrorIntegrator.this);
        }

    }

    @Override
    public void accept(GeomQuadraturePoint gqp) {

        PartialTuple exp = expFunction.apply(gqp.getGeomPoint());
        PartialTuple act = actFunction.apply(gqp.getGeomPoint());
        if (null == squareQuadrature) {
            squareQuadrature = new SingleArray(act.size(), act.getSpatialDimension(), act.getMaxPartialOrder());
        }

        for (int valueDim = 0; valueDim < exp.size(); valueDim++) {
            for (int pd = 0; pd < squareQuadrature.partialSize(); pd++) {
                double e = exp.get(valueDim, pd) - act.get(valueDim, pd);
                squareQuadrature.add(valueDim, pd, e * e * gqp.getWeight());
            }
        }

    }

    public void resetQuadrature() {
        squareQuadrature = null;
    }

    public PartialTuple getQuadrature() {
        if (null == squareQuadrature) {
            return null;
        }
        return sqrtQuadrature;
    }

    private class SqrtQuadratureValue implements PartialTuple {

        @Override
        public int size() {
            return squareQuadrature.size();
        }

        @Override
        public int partialSize() {
            return squareQuadrature.partialSize();
        }

        @Override
        public int getSpatialDimension() {
            return squareQuadrature.getSpatialDimension();
        }

        @Override
        public int getMaxPartialOrder() {
            return squareQuadrature.getMaxPartialOrder();
        }

        @Override
        public double get(int index, int partialIndex) {
            return FastMath.sqrt(squareQuadrature.get(index, partialIndex));
        }

    }

}
