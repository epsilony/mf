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

import java.util.function.Function;
import net.epsilony.tb.analysis.Math2D;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class InRadiusPickerFilter<T> implements MetricFilter<T> {

    private double[] center;
    private double radius;
    private Function<? super T, double[]> coordPicker;

    @Override
    public void setCenter(double[] center) {
        this.center = center;
    }

    @Override
    public void setRadius(double radius) {
        this.radius = radius;

    }

    @Override
    public boolean isInside(T elem) {
        double[] coord = coordPicker.apply(elem);
        return Math2D.distanceSquare(center, coord) <= radius * radius;
    }

    public InRadiusPickerFilter(Function<? super T, double[]> coordPicker) {
        this.coordPicker = coordPicker;
    }

    public InRadiusPickerFilter() {
    }

    public Function<? super T, double[]> getCoordPicker() {
        return coordPicker;
    }

    public void setCoordPicker(Function<? super T, double[]> coordPicker) {
        this.coordPicker = coordPicker;
    }

}
