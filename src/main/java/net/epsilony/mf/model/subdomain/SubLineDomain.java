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

package net.epsilony.mf.model.subdomain;

import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SubLineDomain implements MFSubdomain {

    Line startSegment;
    Line endSegment;
    double startParameter = 0;
    double endParameter = 1;

    public Segment getStartSegment() {
        return startSegment;
    }

    public void setStartSegment(Line startSegment) {
        this.startSegment = startSegment;
    }

    public Segment getEndSegment() {
        return endSegment;
    }

    public void setEndSegment(Line endSegment) {
        this.endSegment = endSegment;
    }

    public double getStartParameter() {
        return startParameter;
    }

    public void setStartParameter(double startParameter) {
        this.startParameter = startParameter;
    }

    public double getEndParameter() {
        return endParameter;
    }

    public void setEndParameter(double endParameter) {
        this.endParameter = endParameter;
    }
}
