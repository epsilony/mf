package net.epsilony.mf.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

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

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class MFRectangle {

    private double[] drul = new double[4];

    public MFRectangle() {
    }

    public MFRectangle(double... drul) {
        setDrul(drul);
    }

    public double getArea() {
        return getWidth() * getHeight();
    }

    public double[] getDrul() {
        return drul;
    }

    public void setDrul(double... drul) {
        if (drul.length != 4) {
            throw new IllegalArgumentException();
        }
        this.drul = Arrays.copyOf(drul, 4);
    }

    public void setDown(double value) {
        drul[0] = value;
    }

    public double getDown() {
        return drul[0];
    }

    public void setRight(double value) {
        drul[1] = value;
    }

    public double getRight() {
        return drul[1];
    }

    public void setUp(double value) {
        drul[2] = value;
    }

    public double getUp() {
        return drul[2];
    }

    public void setLeft(double value) {
        drul[3] = value;
    }

    public double getLeft() {
        return drul[3];
    }

    public boolean isAvialable() {
        if (getHeight() <= 0 || getWidth() <= 0) {
            return false;
        }
        return true;
    }

    public void checkRectangleParameters() {
        if (!isAvialable()) {
            throw new IllegalStateException();
        }
    }

    public double getWidth() {
        return getRight() - getLeft();
    }

    public double getHeight() {
        return getUp() - getDown();
    }

    public ArrayList<double[]> vertesCoords() {
        double[][] coords = { { drul[3], drul[0] }, { drul[1], drul[0] }, { drul[1], drul[2] }, { drul[3], drul[2] } };
        return Lists.newArrayList(coords);
    }

    public boolean isInside(boolean restrictly, double x, double y) {
        if (restrictly) {
            return x < getRight() && x > getLeft() && y < getUp() && y > getDown();
        } else {
            return x <= getRight() && x >= getLeft() && y <= getUp() && y >= getDown();
        }
    }

    public boolean isInside(boolean restrictly, double[] coord) {
        return isInside(restrictly, coord[0], coord[1]);
    }

    public static MFRectangle coordsRange(Stream<double[]> coords) {
        MFRectangle range = new MFRectangle(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        coords.forEach(coord -> {
            double x = coord[0], y = coord[1];
            if (x < range.getLeft()) {
                range.setLeft(x);
            }
            if (x > range.getRight()) {
                range.setRight(x);
            }
            if (y < range.getDown()) {
                range.setDown(y);
            }
            if (y > range.getUp()) {
                range.setUp(y);
            }
        });
        return range;
    }

}
