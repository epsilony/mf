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
package net.epsilony.mf.model.geom.util;

import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.Iterator;

import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.tb.analysis.Math2D;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFLine2DUtils {
    public static double chordLength(MFLine seg) {
        return Math2D.distance(seg.getStart().getCoord(), seg.getEnd().getCoord());
    }

    public static double[] chordMidPoint(MFLine seg, double[] result) {
        return Math2D.pointOnSegment(seg.getStart().getCoord(), seg.getEnd().getCoord(), 0.5, result);
    }

    public static double[] chordPoint(MFLine seg, double parameter, double[] result) {
        return Math2D.pointOnSegment(seg.getStart().getCoord(), seg.getEnd().getCoord(), parameter, result);
    }

    public static boolean isPointStrictlyAtChordLeft(MFLine seg, double[] xy) {
        double[] startCoord = seg.getStartCoord();
        double[] endCoord = seg.getEndCoord();
        double dhrX = endCoord[0] - startCoord[0];
        double dhrY = endCoord[1] - startCoord[1];
        double dx = xy[0] - startCoord[0];
        double dy = xy[1] - startCoord[1];
        double cross = Math2D.cross(dhrX, dhrY, dx, dy);
        return cross > 0 ? true : false;
    }

    public static boolean isPointStrictlyAtChordRight(MFLine seg, double[] xy) {
        double[] startCoord = seg.getStartCoord();
        double[] endCoord = seg.getEndCoord();
        double dhrX = endCoord[0] - startCoord[0];
        double dhrY = endCoord[1] - startCoord[1];
        double dx = xy[0] - startCoord[0];
        double dy = xy[1] - startCoord[1];
        double cross = Math2D.cross(dhrX, dhrY, dx, dy);
        return cross < 0 ? true : false;
    }

    public static double distanceToChord(MFLine seg, double x, double y) {
        double[] v1 = seg.getStartCoord();
        double[] v2 = seg.getEndCoord();
        double d12_x = v2[0] - v1[0];
        double d12_y = v2[1] - v1[1];
        double len12 = Math.sqrt(d12_x * d12_x + d12_y * d12_y);
        double d1p_x = x - v1[0];
        double d1p_y = y - v1[1];
        double project_len = Math2D.dot(d1p_x, d1p_y, d12_x, d12_y) / len12;
        if (project_len > len12) {
            double dx = x - v2[0];
            double dy = y - v2[1];
            return Math.sqrt(dx * dx + dy * dy);
        } else if (project_len < 0) {
            return Math.sqrt(d1p_x * d1p_x + d1p_y * d1p_y);
        } else {
            return Math.abs(Math2D.cross(d12_x, d12_y, d1p_x, d1p_y)) / len12;
        }
    }

    public static double distanceToChord(MFLine seg, double[] pt) {
        return distanceToChord(seg, pt[0], pt[1]);
    }

    public static double maxChordLength(Iterable<? extends MFLine> segments) {
        double maxLength = 0;
        for (MFLine seg : segments) {
            double chordLength = chordLength(seg);
            if (chordLength > maxLength) {
                maxLength = chordLength;
            }
        }
        return maxLength;
    }

    public static void link(MFLine asPred, MFLine asSucc) {
        asPred.setSucc(asSucc);
        asSucc.setPred(asPred);
    }

    public static Path2D genChordPath(Collection<? extends MFLine> heads) {
        Path2D path = new Path2D.Double();
        for (MFLine line : heads) {
            double[] startCoord = line.getStart().getCoord();
            path.moveTo(startCoord[0], startCoord[1]);
            Iterator<MFLine> lineIter = line.iterator();
            MFLine next;
            next = lineIter.next();
            while (lineIter.hasNext()) {
                next = lineIter.next();
                startCoord = next.getStart().getCoord();
                path.lineTo(startCoord[0], startCoord[1]);
            }
            if (next.getSucc() != null) {
                path.closePath();
            }
        }

        return path;
    }

    public static double[] chordVector(MFLine seg, double[] result) {
        return Math2D.subs(seg.getEnd().getCoord(), seg.getStart().getCoord(), result);
    }

    public static double chordVectorDot(MFLine seg1, MFLine seg2) {
        double[] s1 = seg1.getStart().getCoord();
        double[] e1 = seg1.getEnd().getCoord();
        double[] s2 = seg2.getStart().getCoord();
        double[] e2 = seg2.getEnd().getCoord();
        double dx1 = e1[0] - s1[0];
        double dy1 = e1[1] - s1[1];
        double dx2 = e2[0] - s2[0];
        double dy2 = e2[1] - s2[1];
        return Math2D.dot(dx1, dy1, dx2, dy2);
    }

    public static double[] chordUnitOutNormal(MFLine seg, double[] result) {
        double[] vec = chordVector(seg, result);
        double dx = vec[0];
        double dy = vec[1];
        double len = chordLength(seg);
        vec[0] = dy / len;
        vec[1] = -dx / len;
        return vec;
    }
}
