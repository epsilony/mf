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
import java.util.Iterator;
import java.util.List;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.MiscellaneousUtils;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LRTreeSegmentChordIntersectingSphereSearcher implements SphereSearcher<Segment> {

    public static final int DEMENSION = 2;
    SegmentsMidPointLRTreeRangeSearcher segmentsRangeSearcher;
    double maxSegmentLength;

    @Override
    public List<Segment> searchInSphere(double[] center, double radius) {

        if (radius < 0) {
            throw new IllegalArgumentException("Illegal negative Radius!");
        }
        double[] from = new double[] { center[0] - radius - maxSegmentLength / 2,
                center[1] - radius - maxSegmentLength / 2 };
        double[] to = new double[] { center[0] + radius + maxSegmentLength / 2,
                center[1] + radius + maxSegmentLength / 2 };

        List<Segment> segments = segmentsRangeSearcher.rangeSearch(from, to);
        Iterator<Segment> segIter = segments.iterator();
        while (segIter.hasNext()) {
            Segment seg = segIter.next();
            if (Segment2DUtils.distanceToChord(seg, center) > radius) {
                segIter.remove();
            }
        }
        return segments;
    }

    @Override
    public void setAll(Collection<? extends Segment> datas) {
        segmentsRangeSearcher = new SegmentsMidPointLRTreeRangeSearcher(datas, DEMENSION);
        maxSegmentLength = Segment2DUtils.maxChordLength(datas);
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + '{' + "segment searcher " + segmentsRangeSearcher
                + ", max segment length: " + maxSegmentLength + '}';
    }
}
