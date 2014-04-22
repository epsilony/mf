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

import java.util.function.Function;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.cons_law.Strains;
import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.model.load.ArrayDirichletLoadValue;
import net.epsilony.mf.model.load.ArrayLoadValue;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.math.convention.Pds2;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MechanicalPatchModelFactory2D extends PatchModelFactory2D {
    private ConstitutiveLaw constitutiveLaw;

    @Override
    protected GeomPointLoad genDirichletLoad() {
        return new GeomPointLoad() {
            final boolean[] validities = new boolean[] { true, true };

            @Override
            synchronized public LoadValue calcLoad(GeomPoint geomPoint) {
                ArrayDirichletLoadValue result = new ArrayDirichletLoadValue();
                result.setValidities(validities);
                PartialTuple fieldValue = field.apply(geomPoint.getCoord());
                result.setValues(new double[] { fieldValue.get(0, 0), fieldValue.get(1, 0) });
                return result;
            }

            @Override
            public boolean isDirichlet() {
                return true;
            };
        };
    }

    private final Function<PartialTuple, double[]> engStrainFunc = Strains
            .partialValueTupleToEngineeringStrainFunction2D();

    private double[] engStrain(double u_x, double u_y, double v_x, double v_y) {
        return new double[] { u_x, v_y, u_y + v_x };
    }

    @Override
    protected GeomPointLoad genVolumeLoad() {
        return new GeomPointLoad() {

            @Override
            public LoadValue calcLoad(GeomPoint geomPoint) {
                PartialTuple fieldValue = field.apply(geomPoint.getCoord());
                double[] strain_x = engStrain(fieldValue.get(0, Pds2.U_xx), fieldValue.get(0, Pds2.U_xy),
                        fieldValue.get(1, Pds2.U_xx), fieldValue.get(1, Pds2.U_xy));
                double[] stress_x = constitutiveLaw.calcStressByEngineeringStrain(strain_x, null);
                double[] strain_y = engStrain(fieldValue.get(0, Pds2.U_xy), fieldValue.get(0, Pds2.U_yy),
                        fieldValue.get(1, Pds2.U_xy), fieldValue.get(1, Pds2.U_yy));
                double[] stress_y = constitutiveLaw.calcStressByEngineeringStrain(strain_y, null);
                return new ArrayLoadValue(new double[] { -stress_x[0] - stress_y[2], -stress_x[2] - stress_y[1] });
            }
        };
    }

    @Override
    protected GeomPointLoad genNeumannLoad() {
        return new GeomPointLoad() {

            @Override
            public LoadValue calcLoad(GeomPoint geomPoint) {
                PartialTuple fieldValue = field.apply(geomPoint.getCoord());
                double[] engStrain = engStrainFunc.apply(fieldValue);
                double[] stress = constitutiveLaw.calcStressByEngineeringStrain(engStrain, null);
                double sxx = stress[0];
                double syy = stress[1];
                double sxy = stress[2];
                Segment seg = (Segment) geomPoint.getGeomUnit();
                double[] chordUnitOutNormal = Segment2DUtils.chordUnitOutNormal(seg, null);
                double nx = chordUnitOutNormal[0];
                double ny = chordUnitOutNormal[1];
                return new ArrayLoadValue(new double[] { sxx * nx + sxy * ny, sxy * nx + syy * ny });
            }
        };
    }

    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    @Override
    protected int getValueDimension() {
        return 2;
    }

}
