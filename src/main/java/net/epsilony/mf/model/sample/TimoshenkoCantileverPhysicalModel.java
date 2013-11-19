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

import static net.epsilony.mf.model.MFRectangleEdge.DOWN;
import static net.epsilony.mf.model.MFRectangleEdge.LEFT;
import static net.epsilony.mf.model.MFRectangleEdge.RIGHT;
import static net.epsilony.mf.model.MFRectangleEdge.UP;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.cons_law.PlaneStress;
import net.epsilony.mf.model.RectanglePhysicalModel;
import net.epsilony.mf.model.load.AbstractSegmentLoad;
import net.epsilony.tb.analysis.GenericFunction;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TimoshenkoCantileverPhysicalModel extends RectanglePhysicalModel {
    public static final double DEFAULT_LEFT = 0;
    public static final double DEFAULT_RIGHT = 48;
    public static final double DEFAULT_DOWN = -6;
    public static final double DEFAULT_UP = 6;
    public static final int VALUE_DIMENSION = 2;
    double E, nu;
    double P;

    public TimoshenkoCantileverPhysicalModel() {
        super();
        setValueDimension(VALUE_DIMENSION);
        initEdgePositions();
        applyLoadsOnRectangle();
    }

    private void initEdgePositions() {
        setEdgePosition(DOWN, DEFAULT_DOWN);
        setEdgePosition(LEFT, DEFAULT_LEFT);
        setEdgePosition(RIGHT, DEFAULT_RIGHT);
        setEdgePosition(UP, DEFAULT_UP);
    }

    protected void applyLoadsOnRectangle() {
        setEdgeLoad(RIGHT, new AbstractSegmentLoad() {
            final GenericFunction<double[], double[]> func = new NeumannFunction();

            @Override
            public double[] getValue() {
                segment.setDiffOrder(0);
                double[] ds = segment.values(parameter, null);
                return func.value(ds, ds);
            }
        });

        setEdgeLoad(LEFT, new AbstractSegmentLoad() {
            GenericFunction<double[], double[]> func = new DirichletFunction();
            GenericFunction<double[], boolean[]> valdFunc = new DirichletMarker();
            double[] coord = new double[2];

            @Override
            public double[] getValue() {
                segment.setDiffOrder(0);
                segment.values(parameter, coord);
                return func.value(coord, null);
            }

            @Override
            public boolean[] getValidity() {
                return valdFunc.value(coord, null);
            }

            @Override
            public boolean isDirichlet() {
                return true;
            }
        });
    }

    public double[] displacement(double x, double y, int partDiffOrder, double[] results) {
        int resDim = 0;
        switch (partDiffOrder) {
        case 0:
            resDim = 2;
            break;
        case 1:
            resDim = 6;
            break;
        default:
            throw new IllegalArgumentException("partDiffOrder should be 0 or 1, others are not supported");
        }
        if (null == results) {
            results = new double[resDim];
        } else {
            if (results.length < resDim) {
                throw new IllegalArgumentException("When partDiffOrder is " + partDiffOrder
                        + ", the results.lenght should >= " + resDim
                        + ". Try to give a longer results all just give a null reference.");
            }
        }
        double xr = getRelativeX(x);
        double yr = getRelativeY(y);
        double D = getHeight(), L = getWidth();
        double I = getI();
        double u = -P * yr / (6 * E * I) * ((6 * L - 3 * xr) * xr + (2 + nu) * (yr * yr - D * D / 4));
        double v = P / (6 * E * I)
                * (3 * nu * yr * yr * (L - xr) + (4 + 5 * nu) * D * D * xr / 4 + (3 * L - xr) * xr * xr);
        results[0] = u;
        results[1] = v;
        if (partDiffOrder > 0) {
            double u_x = -P * yr * (L - xr) / E / I;
            double u_y = P / E / I * (-(nu + 2) * yr * yr / 2 + (nu + 2) * D * D / 24 - L * xr + xr * xr / 2);
            double v_x = P / E / I * (-nu * yr * yr / 2 + (4 + 5 * nu) * D * D / 24 + L * xr - xr * xr / 2);
            double v_y = P / E / I * nu * yr * (L - xr);
            results[2] = u_x;
            results[3] = u_y;
            results[4] = v_x;
            results[5] = v_y;
        }
        return results;
    }

    public double[] strain(double x, double y, double results[]) {
        if (null == results) {
            results = new double[3];
        }
        double xr = getRelativeX(x);
        double yr = getRelativeY(y);
        double L = getWidth();
        double D = getHeight();
        double I = getI();
        double xx = -P * yr * (L - xr) / E / I;
        double yy = P / E / I * nu * yr * (L - xr);
        double xy = P / E / I * (1 + nu) * (D * D / 4 - yr * yr);
        results[0] = xx;
        results[1] = yy;
        results[2] = xy;
        return results;
    }

    public double[] stress(double x, double y, double results[]) {
        if (null == results) {
            results = new double[3];
        }
        double xr = getRelativeX(x);
        double yr = getRelativeY(y);
        double I = getI();
        double L = getWidth();
        double D = getHeight();
        double sxx = -P * (L - xr) * yr / I;
        double syy = 0;
        double sxy = P / (2 * I) * (D * D / 4.0 - yr * yr);
        results[0] = sxx;
        results[1] = syy;
        results[2] = sxy;
        return results;
    }

    public ConstitutiveLaw constitutiveLaw() {
        return new PlaneStress(E, nu);
    }

    public class NeumannFunction implements GenericFunction<double[], double[]> {

        @Override
        public double[] value(double[] input, double[] output) {
            double[] strVal = stress(input[0], input[1], null);
            if (output == null) {
                output = new double[2];
            }
            output[0] = strVal[0];
            output[1] = strVal[2];
            return output;
        }
    }

    public class DirichletFunction implements GenericFunction<double[], double[]> {

        @Override
        public double[] value(double[] input, double[] output) {
            return displacement(input[0], input[1], 0, output);

        }
    }

    public class DirichletMarker implements GenericFunction<double[], boolean[]> {

        @Override
        public boolean[] value(double[] input, boolean[] output) {
            if (output == null) {
                output = new boolean[2];
            }
            output[0] = true;
            output[1] = true;
            return output;
        }
    }

    private double getI() {
        return Math.pow(getHeight(), 3) / 12;
    }

    private double getRelativeX(double x) {
        return x - getEdgePosition(LEFT);
    }

    private double getRelativeY(double y) {
        return y - (getEdgePosition(UP) + getEdgePosition(DOWN)) / 2;
    }

    public double getE() {
        return E;
    }

    public double getNu() {
        return nu;
    }

    public double getP() {
        return P;
    }

    public void setE(double e) {
        E = e;
    }

    public void setNu(double nu) {
        this.nu = nu;
    }

    public void setP(double p) {
        P = p;
    }
}
