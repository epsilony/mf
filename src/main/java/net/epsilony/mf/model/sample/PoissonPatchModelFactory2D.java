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
package net.epsilony.mf.model.sample;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.model.load.ArrayDirichletLoadValue;
import net.epsilony.mf.model.load.ArrayLoadValue;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.math.convention.Pds2;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;

import org.apache.commons.math3.util.MathArrays;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class PoissonPatchModelFactory2D extends PatchModelFactory2D {

    @Override
    protected GeomPointLoad genNeumannLoad() {
        return new GeomPointLoad() {

            @Override
            synchronized public LoadValue calcLoad(GeomPoint geomPoint) {
                MFLine seg = (MFLine) geomPoint.getGeomUnit();
                double[] outNormal = MFLine2DUtils.chordUnitOutNormal(seg, null);

                PartialTuple fieldValue = field.apply(geomPoint.getCoord());
                double[] grad = new double[] { fieldValue.get(0, Pds2.U_x), fieldValue.get(0, Pds2.U_y) };
                double neu = MathArrays.linearCombination(outNormal, grad);
                ArrayLoadValue result = new ArrayLoadValue();
                result.setValues(new double[] { neu });
                return result;
            }
        };
    }

    @Override
    protected GeomPointLoad genVolumeLoad() {
        return new GeomPointLoad() {

            @Override
            synchronized public LoadValue calcLoad(GeomPoint geomPoint) {
                ArrayLoadValue result = new ArrayLoadValue();
                PartialTuple fieldValue = field.apply(geomPoint.getCoord());
                result.setValues(new double[] { -fieldValue.get(0, Pds2.U_xx) - fieldValue.get(0, Pds2.U_yy) });
                return result;
            }
        };
    }

    @Override
    protected GeomPointLoad genDirichletLoad() {
        return new GeomPointLoad() {
            final boolean[] validities = new boolean[] { true };

            @Override
            synchronized public LoadValue calcLoad(GeomPoint geomPoint) {
                ArrayDirichletLoadValue result = new ArrayDirichletLoadValue();
                result.setValidities(validities);
                result.setValues(new double[] { field.apply(geomPoint.getCoord()).get(0, 0) });
                return result;
            }

            @Override
            public boolean isDirichlet() {
                return true;
            };
        };
    }

    @Override
    protected int getValueDimension() {
        return 1;
    }
}
