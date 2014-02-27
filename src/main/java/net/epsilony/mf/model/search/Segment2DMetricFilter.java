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

import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class Segment2DMetricFilter implements MetricFilter<Segment> {

    private double[] center;
    private double radius;

    @Override
    public void setCenter(double[] center) {
        this.center = center;
    }

    @Override
    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean isInside(Segment elem) {
        // TODO: is '<' better?
        return Segment2DUtils.distanceToChord(elem, center) <= radius;
    }
}
