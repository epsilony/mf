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
package net.epsilony.mf.shape_func.bases;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public abstract class MFMonomialBases implements MFBases {
    public static class Linear1D extends MFMonomialBases {

        @Override
        public void bases(double[] v, double[][] result) {
            double x = v[0];
            double[] res = result[0];
            res[0] = 1;
            res[1] = x;
            if (diffOrder >= 1) {
                double[] res_x = result[1];
                res_x[0] = 0;
                res_x[1] = 1;
            }
        }

        @Override
        public int getDegree() {
            return 1;
        }

        @Override
        public int length() {
            return 2;
        }

        @Override
        public int getSpatialDimension() {
            return 1;
        }
    }

    public static class Quadric1D extends MFMonomialBases {

        @Override
        public void bases(double[] v, double[][] result) {
            double x = v[0];
            double[] res = result[0];
            res[0] = 1;
            res[1] = x;
            res[2] = x * x;
            if (diffOrder >= 1) {
                double[] res_x = result[1];
                res_x[0] = 0;
                res_x[1] = 1;
                res_x[2] = 2 * x;
            }
        }

        @Override
        public int getDegree() {
            return 2;
        }

        @Override
        public int length() {
            return 3;
        }

        @Override
        public int getSpatialDimension() {
            return 1;
        }
    }

    public static class Cubic1D extends MFMonomialBases {

        @Override
        public void bases(double[] v, double[][] result) {
            final double x = v[0];
            double[] res = result[0];
            res[0] = 1;
            res[1] = x;
            final double x2 = x * x;
            res[2] = x2;
            res[3] = x2 * x;
            if (diffOrder >= 1) {
                double[] res_x = result[1];
                res_x[0] = 0;
                res_x[1] = 1;
                res_x[2] = 2 * x;
                res_x[3] = 3 * x2;
            }
        }

        @Override
        public int getDegree() {
            return 3;
        }

        @Override
        public int length() {
            return 4;
        }

        @Override
        public int getSpatialDimension() {
            return 1;
        }
    }

    public static class Linear2D extends MFMonomialBases {

        @Override
        public void bases(double[] v, double[][] result) {
            final double x = v[0];
            final double y = v[1];
            final double[] res = result[0];
            res[0] = 1;
            res[1] = x;
            res[2] = y;
            if (diffOrder >= 1) {
                final double[] res_x = result[1];
                res_x[0] = 0;
                res_x[1] = 1;
                res_x[2] = 0;
                final double[] res_y = result[2];
                res_y[0] = 0;
                res_y[1] = 0;
                res_y[2] = 1;
            }
        }

        @Override
        public int length() {
            return 3;
        }

        @Override
        public int getDegree() {
            return 1;
        }

        @Override
        public int getSpatialDimension() {
            return 2;
        }

    }

    public static class Quadric2D extends MFMonomialBases {

        @Override
        public void bases(double[] v, double[][] result) {
            final double x = v[0];
            final double y = v[1];
            final double[] res = result[0];
            res[0] = 1;
            res[1] = x;
            res[2] = y;
            res[3] = x * x;
            res[4] = x * y;
            res[5] = y * y;
            if (diffOrder >= 1) {
                final double[] res_x = result[1];
                res_x[0] = 0;
                res_x[1] = 1;
                res_x[2] = 0;
                res_x[3] = 2 * x;
                res_x[4] = y;
                res_x[5] = 0;
                final double[] res_y = result[2];
                res_y[0] = 0;
                res_y[1] = 0;
                res_y[2] = 1;
                res_y[3] = 0;
                res_y[4] = x;
                res_y[5] = 2 * y;
            }
        }

        @Override
        public int length() {
            return 6;
        }

        @Override
        public int getDegree() {
            return 2;
        }

        @Override
        public int getSpatialDimension() {
            return 2;
        }

    }

    public static class Cubic2D extends MFMonomialBases {

        @Override
        public void bases(double[] v, double[][] result) {
            final double x = v[0];
            final double y = v[1];
            final double[] res = result[0];
            res[0] = 1;
            res[1] = x;
            res[2] = y;
            final double x2 = x * x;
            res[3] = x2;
            final double xy = x * y;
            res[4] = xy;
            final double y2 = y * y;
            res[5] = y2;
            res[6] = x * x2;
            res[7] = x * xy;
            res[8] = xy * y;
            res[9] = y2 * y;
            if (diffOrder >= 1) {
                final double[] res_x = result[1];
                res_x[0] = 0;
                res_x[1] = 1;
                res_x[2] = 0;
                res_x[3] = 2 * x;
                res_x[4] = y;
                res_x[5] = 0;
                res_x[6] = 3 * x2;
                res_x[7] = 2 * xy;
                res_x[8] = y2;
                res_x[9] = 0;
                final double[] res_y = result[2];
                res_y[0] = 0;
                res_y[1] = 0;
                res_y[2] = 1;
                res_y[3] = 0;
                res_y[4] = x;
                res_y[5] = 2 * y;
                res_y[6] = 0;
                res_y[7] = x2;
                res_y[8] = 2 * xy;
                res_y[9] = 3 * y2;
            }
        }

        @Override
        public int length() {
            return 10;
        }

        @Override
        public int getDegree() {
            return 3;
        }

        @Override
        public int getSpatialDimension() {
            return 2;
        }

    }

    protected int diffOrder;

    @Override
    public int getDiffOrder() {
        return diffOrder;
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        if (diffOrder < 0 || diffOrder > 1) {
            throw new IllegalArgumentException();
        }
        this.diffOrder = diffOrder;
    }

    public abstract int getDegree();

}
