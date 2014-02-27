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
package net.epsilony.mf.util.math;

import static org.apache.commons.math3.util.FastMath.sqrt;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class VectorMath {
    private VectorMath() {

    }

    public static double distanceSquare(double[] v1, double[] v2) {
        checkLengthLegal(v1, v2);
        double result = 0;
        for (int i = 0; i < v1.length; i++) {
            double d = v1[i] - v2[i];
            result += d * d;
        }
        return result;
    }

    public static double distance(double[] v1, double[] v2) {
        return sqrt(distanceSquare(v1, v2));
    }

    public static double lengthSquare(double[] v) {
        return distanceSquare(v, v);
    }

    public static double length(double[] v) {
        return sqrt(lengthSquare(v));
    }

    public static void translate(double[] v1, double translation, double[] output) {
        checkLengthLegal(v1, output);
        for (int i = 0; i < v1.length; i++) {
            output[i] = v1[i] + translation;
        }
    }

    public static double[] translate(double[] v1, double translation) {
        double[] result = new double[v1.length];
        translate(v1, translation, result);
        return result;
    }

    public static void pointBetween(double[] v1, double[] v2, double ratio, double[] output) {
        checkLengthLegal(v1, v2, output);
        for (int i = 0; i < v1.length; i++) {
            output[i] = v1[i] * (1 - ratio) + v2[i] * ratio;
        }
    }

    public static double[] pointBetween(double[] v1, double[] v2, double ratio) {
        double[] result = new double[v1.length];
        pointBetween(v1, v2, ratio, result);
        return result;
    }

    public static void midPoint(double[] v1, double[] v2, double[] output) {
        pointBetween(v1, v2, 0.5, output);
    }

    public static double[] midPoint(double[] v1, double[] v2) {
        double[] result = new double[v1.length];
        midPoint(v1, v2, result);
        return result;
    }

    public static void scale(double[] v1, double scale, double[] result) {
        checkLengthLegal(v1, result);
        for (int i = 0; i < v1.length; i++) {
            result[i] = v1[i] * scale;
        }
    }

    public static double[] scale(double[] v1, double scale) {
        double[] result = new double[v1.length];
        scale(v1, scale, result);
        return result;
    }

    public static double innerProduce(double[] v1, double[] v2) {
        checkLengthLegal(v1, v2);
        double result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        return result;
    }

    public static boolean isLengthLegal(double[] v1) {
        return v1.length != 0;
    }

    public static boolean isLengthLegal(double[] v1, double[] v2) {
        return v1.length != 0 && v1.length == v2.length;
    }

    public static boolean isLengthLegal(double[] v1, double[] v2, double[] v3) {
        return v1.length != 0 && v1.length == v2.length && v3.length == v1.length;
    }

    public static boolean isLengthLegal(double[]... vecs) {
        if (vecs.length == 0) {
            throw new IllegalArgumentException();
        }
        int length = vecs[0].length;
        boolean result = true;
        for (int i = 1; i < vecs.length; i++) {
            result = result && vecs[i].length == length;
        }
        return result;
    }

    private static final String lengthMismatch = "vector length is 0 or mismatch";

    public static void checkLengthLegal(double[] v) {
        if (!isLengthLegal(v)) {
            throw new IllegalArgumentException(lengthMismatch);
        }
    }

    public static void checkLengthLegal(double[] v1, double[] v2) {
        if (!isLengthLegal(v1, v2)) {
            throw new IllegalArgumentException(lengthMismatch);
        }
    }

    public static void checkLengthLegal(double[] v1, double[] v2, double[] v3) {
        if (!isLengthLegal(v1, v2, v3)) {
            throw new IllegalArgumentException(lengthMismatch);
        }
    }

    public static void checkLengthLegal(double[]... vecs) {
        if (!isLengthLegal(vecs)) {
            throw new IllegalArgumentException(lengthMismatch);
        }
    }
}
