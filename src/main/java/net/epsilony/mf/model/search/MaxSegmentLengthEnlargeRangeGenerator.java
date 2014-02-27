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
public class MaxSegmentLengthEnlargeRangeGenerator implements RangeGenerator {
    EnlargeRangeGenerator enlargeRangeGenerator = new EnlargeRangeGenerator();

    public EnlargeRangeGenerator getEnlargeRangeGenerator() {
        return enlargeRangeGenerator;
    }

    public void setEnlargeRangeGenerator(EnlargeRangeGenerator enlargeRangeGenerator) {
        this.enlargeRangeGenerator = enlargeRangeGenerator;
    }

    public void setEnlargement(Iterable<? extends Segment> segments) {
        double longest = 0;
        for (Segment seg : segments) {
            double length = Segment2DUtils.chordLength(seg);
            if (longest < length) {
                longest = length;
            }
        }
        enlargeRangeGenerator.setEnlargement(longest / 2);
    }

    @Override
    public void setCenter(double[] center) {
        enlargeRangeGenerator.setCenter(center);
    }

    @Override
    public void setRadius(double radius) {
        enlargeRangeGenerator.setRadius(radius);
    }

    @Override
    public double[] getFrom() {
        return enlargeRangeGenerator.getFrom();
    }

    @Override
    public double[] getTo() {
        return enlargeRangeGenerator.getTo();
    }

    public double getEnlargement() {
        return enlargeRangeGenerator.getEnlargement();
    }

}
