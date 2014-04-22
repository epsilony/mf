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
package net.epsilony.mf.cons_law;

import java.util.function.Function;

import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.math.convention.Pds2;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class Strains {
    public static Function<PartialTuple, double[]> partialValueTupleToEngineeringStrainFunction2D() {
        return pv -> {
            return new double[] { pv.get(0, Pds2.U_x), pv.get(1, Pds2.U_y), pv.get(0, Pds2.U_y) + pv.get(1, Pds2.U_x) };
        };
    }
}
