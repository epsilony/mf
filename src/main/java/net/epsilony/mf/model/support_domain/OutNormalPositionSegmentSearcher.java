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
package net.epsilony.mf.model.support_domain;

import net.epsilony.tb.analysis.Math2D;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;

import org.apache.commons.math3.util.FastMath;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OutNormalPositionSegmentSearcher {

    public static final double DEFAULT_MAX_CENTER_BND_DISTANCE = 1e-6;
    public static final double DEFAULT_MIN_BND_OUTNORMAL_COSINE = FastMath.cos(FastMath.PI / 3600);
    public static final double DEFAULT_UNITY_TOL = 1e-12;

    double unityTol = DEFAULT_UNITY_TOL;
    double maxCenterBndDistance = DEFAULT_MAX_CENTER_BND_DISTANCE;
    double minBndOutNormalCosine = DEFAULT_MIN_BND_OUTNORMAL_COSINE;

    public MFLine search(double[] center, double[] unitOutNormal, Iterable<? extends MFLine> segments) {
        if (null == center || null == unitOutNormal) {
            return null;
        }
        checkUnity(unitOutNormal);

        for (MFLine segment : segments) {
            if (MFLine2DUtils.distanceToChord(segment, center) > maxCenterBndDistance) {
                continue;
            }
            double par = Math2D.projectionParameter(segment.getStart().getCoord(), segment.getEnd().getCoord(), center);
            if (par > 1 || par < 0) {
                continue;
            }
            double[] chordUnitOutNormal = MFLine2DUtils.chordUnitOutNormal(segment, null);
            double dot = Math2D.dot(unitOutNormal, chordUnitOutNormal);
            if (dot > minBndOutNormalCosine) {
                return segment;
            }
        }
        return null;
    }

    private void checkUnity(double[] outNorm) {
        double x = outNorm[0];
        double y = outNorm[1];
        if (FastMath.abs(x * x + y * y - 1) > DEFAULT_UNITY_TOL) {
            throw new IllegalStateException();
        }
    }
}
