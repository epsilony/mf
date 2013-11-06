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

package net.epsilony.mf.process.integrate.unit;

import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawMFBoundaryIntegratePoint extends RawMFIntegratePoint implements MFBoundaryIntegratePoint {

    GeomUnit boundary;
    double boundaryParameter;
    double[] unitOutNormal;

//    public SimpMFBoundaryIntegratePoint(Segment2DQuadraturePoint qp, double[] load, boolean[] loadValidity) {
//        this.coord = qp.coord;
//        this.weight = qp.weight;
//        this.load = load;
//        this.loadValidity = loadValidity;
//        this.boundary = new MFLineBnd((Line) qp.segment);
//        this.boundaryParameter = qp.segmentParameter;
//    }
    public RawMFBoundaryIntegratePoint() {
    }

    public void setBoundary(GeomUnit boundary) {
        this.boundary = boundary;
    }

    public void setBoundaryParameter(double boundaryParameter) {
        this.boundaryParameter = boundaryParameter;
    }

    @Override
    public GeomUnit getBoundary() {
        return boundary;
    }

    @Override
    public double getBoundaryParameter() {
        return boundaryParameter;
    }

    @Override
    public double[] getUnitOutNormal() {
        return unitOutNormal;
    }

    public void setUnitOutNormal(double[] outNormal) {
        this.unitOutNormal = outNormal;
    }
}
