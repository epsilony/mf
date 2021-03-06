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
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class Segment2DChordCenterPicker implements Function<MFLine, double[]> {

    @Override
    public double[] apply(MFLine input) {
        return MFLine2DUtils.chordMidPoint(input, null);
    }

}
