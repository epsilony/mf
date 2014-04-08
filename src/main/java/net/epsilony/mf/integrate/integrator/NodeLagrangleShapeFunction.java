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
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.shape_func.ShapeFunctionValue;
import net.epsilony.mf.shape_func.SimpShapeFunctionValue;
import net.epsilony.mf.util.math.PartialValueTuple;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NodeLagrangleShapeFunction implements Function<GeomPoint, ShapeFunctionValue> {
    MFNode node;

    SimpShapeFunctionValue result = new SimpShapeFunctionValue(new PartialValueTuple() {

        @Override
        public double get(int index, int partialIndex) {
            if (index != 0 || partialIndex != 0) {
                throw new IllegalArgumentException();
            }
            return 1;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int getSpatialDimension() {
            return 0;
        }

        @Override
        public int getMaxPartialOrder() {
            return 0;
        }

        @Override
        public PartialValueTuple copy() {
            return this;
        }
    }, (index) -> node.getLagrangeAssemblyIndex());

    @Override
    public ShapeFunctionValue apply(GeomPoint geomPoint) {
        node = (MFNode) geomPoint.getGeomUnit();
        return result;
    }
}
