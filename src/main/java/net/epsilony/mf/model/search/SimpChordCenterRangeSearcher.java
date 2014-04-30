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

import java.util.Collection;
import java.util.List;

import net.epsilony.tb.rangesearch.RangeSearcher;
import net.epsilony.mf.model.geom.MFLine;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class SimpChordCenterRangeSearcher<V extends MFLine> implements RangeSearcher<double[], V> {
    List<? extends V> boundaries;

    public List<? extends V> getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(List<? extends V> boundaries) {
        this.boundaries = boundaries;
    }

    @Override
    public void rangeSearch(double[] from, double[] to, Collection<? super V> output) {
        output.clear();
        boundaries.stream().filter((seg) -> {
            double[] s = seg.getStart().getCoord();
            double[] e = seg.getEnd().getCoord();
            for (int i = 0; i < s.length; i++) {
                double m = (s[i] + e[i]) / 2;
                if (m < from[i] || m > to[i]) {
                    return false;
                }
            }
            return true;
        }).forEach(output::add);
    }
}
