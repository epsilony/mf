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
package net.epsilony.mf.model.geom;

import java.util.ArrayList;
import java.util.Iterator;

import net.epsilony.mf.model.geom.util.MFLine2DUtils;

import com.google.common.collect.Iterables;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFFacet implements MFGeomUnit, Iterable<MFLine> {
    private final ArrayList<MFLine> chainHeads = new ArrayList<>();

    public ArrayList<MFLine> getRingsHeads() {
        return chainHeads;
    }

    /**
     * REMARK, this method will not be internally called except
     * {@link #requireWell()}
     */
    public boolean isWell() {
        if (chainHeads.isEmpty()) {
            return true;
        }
        // must be closed, with restrict positive area and well connected
        MFLine firstHead = chainHeads.get(0);
        if (firstHead.getPred() == null || firstHead.getPred() == firstHead || !firstHead.isWellConnected()
                || !firstHead.isAnticlockWise()) {
            return false;
        }

        // must be closed, with restrict negative area and well connected
        for (int i = 1; i < chainHeads.size(); i++) {
            MFLine head = chainHeads.get(i);
            if (head.getPred() == null || head.getPred() == head || !head.isWellConnected() || head.isAnticlockWise()) {
                return false;
            }
        }
        return true;
    }

    public void requireWell() {
        if (!isWell()) {
            throw new IllegalStateException();
        }
    }

    @Override
    public Iterator<MFLine> iterator() {
        return Iterables.concat(chainHeads).iterator();
    }

    /**
     * Originate from:<\br> Joseph O'Rourke, Computational Geometry in C,2ed.
     * Page 244, Code 7.13
     */

    public enum PositionStatus {
    INSIDE, OUTSIDE, ON_EDGE, ON_VERTEX;
    }

    public PositionStatus rayCrossing(double x, double y) {
        int rCross = 0, lCross = 0;
        for (MFLine seg : this) {
            double[] start = seg.getStartCoord();
            double x1 = start[0];
            double y1 = start[1];
            if (x1 == x && y1 == y) {
                return PositionStatus.ON_VERTEX;
            }
            double[] end = seg.getEndCoord();
            double x2 = end[0];
            double y2 = end[1];

            boolean rStrad = (y1 > y) != (y2 > y);
            boolean lStrad = (y1 < y) != (y2 < y);

            if (rStrad || lStrad) {
                if (rStrad && x1 > x && x2 > x) {
                    rCross += 1;
                } else if (lStrad && x1 < x && x2 < x) {
                    lCross += 1;
                } else {
                    double xCross = (x1 * y - x1 * y2 - x2 * y + x2 * y1) / (y1 - y2);
                    if (rStrad && xCross > x) {
                        rCross++;
                    }
                    if (lStrad && xCross < x) {
                        lCross++;
                    }
                }
            }
        }
        rCross %= 2;
        lCross %= 2;
        if (rCross != lCross) {
            return PositionStatus.ON_EDGE;
        }
        if (rCross == 1) {
            return PositionStatus.INSIDE;
        } else {
            return PositionStatus.OUTSIDE;
        }
    }

    public double distanceFunction(double[] coord) {
        return distanceFunction(coord[0], coord[1]);
    }

    public double distanceFunction(double x, double y) {
        PositionStatus rayCrs = rayCrossing(x, y);

        if (rayCrs == PositionStatus.ON_EDGE || rayCrs == PositionStatus.ON_VERTEX) {
            return 0;
        }

        double inf = Double.POSITIVE_INFINITY;
        for (MFLine seg : this) {
            double dst = MFLine2DUtils.distanceToChord(seg, x, y);
            if (dst < inf) {
                inf = dst;
            }
        }
        return rayCrs == PositionStatus.INSIDE ? inf : -inf;
    }
}
