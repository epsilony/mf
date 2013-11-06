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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.TestTool;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LRTreeSegment2DIntersectingSphereSearcherTest {

    public LRTreeSegment2DIntersectingSphereSearcherTest() {
    }

    /**
     * Test of searchInSphere method, of class
     * LRTreeSegmentChordIntersectingSphereSearcher.
     */
    @Test
    public void testSearchInSphere() {
        ArrayList<double[][][]> coords = new ArrayList<>(1);
        Facet pg = TestTool.samplePolygon(coords);
        LRTreeSegmentChordIntersectingSphereSearcher polygonSearcher =
                new LRTreeSegmentChordIntersectingSphereSearcher();
        polygonSearcher.setAll(pg.getSegments());
        int testTime = 1000;
        double radiusMin = 0.3;
        double radiusRange = 3;
        double centerMargin = 1;

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Segment seg : pg) {
            Node nd = seg.getStart();
            double[] coord = nd.getCoord();
            double x = coord[0];
            double y = coord[1];
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        Random rand = new Random();
        double[] centerFrom = new double[]{minX - centerMargin, minY - centerMargin};
        double[] centerRange = new double[]{
            maxX - minX + 2 * centerMargin,
            maxY - minY + 2 * centerMargin};
        for (int i = 0; i < testTime; i++) {

            double[] center = new double[]{
                rand.nextDouble() * centerRange[0] + centerFrom[0],
                rand.nextDouble() * centerRange[1] + centerFrom[1]};
            double radius = rand.nextDouble() * radiusRange + radiusMin;

            List<Segment> acts = polygonSearcher.searchInSphere(center, radius);
            LinkedList<Segment> exps = new LinkedList<>();
            for (Segment seg : pg) {
                if (Segment2DUtils.distanceToChord(seg, center) <= radius) {
                    exps.add(seg);
                }
            }
            try {
                assertEquals(exps.size(), acts.size());
                for (Segment seg : acts) {
                    assertTrue(exps.contains(seg));
                }
            } catch (Throwable e) {
                polygonSearcher.searchInSphere(center, radius);
                throw e;
            }
        }
    }
}
