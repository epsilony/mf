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
package net.epsilony.mf.model.search;

import java.util.Arrays;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class EnlargeRangeGenerator implements RangeGenerator {

    private double[] center;
    private double   enlargement;
    private double   radius;

    @Override
    public void setCenter(double[] center) {
        this.center = center;
    }

    @Override
    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public double[] getFrom() {
        return shiftFromCenter(-enlargement - radius);
    }

    private double[] shiftFromCenter(double shift) {
        double[] result = Arrays.copyOf(center, center.length);
        for (int i = 0; i < result.length; i++) {
            result[i] += shift;
        }
        return result;
    }

    @Override
    public double[] getTo() {
        return shiftFromCenter(enlargement + radius);
    }

    public double getEnlargement() {
        return enlargement;
    }

    public void setEnlargement(double shift) {
        this.enlargement = shift;
    }

}
