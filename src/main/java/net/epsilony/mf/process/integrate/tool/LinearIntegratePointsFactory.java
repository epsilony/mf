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

package net.epsilony.mf.process.integrate.tool;

import java.util.LinkedList;
import java.util.List;

import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.process.integrate.unit.RawMFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.unit.RawMFIntegratePoint;
import net.epsilony.tb.Factory;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.Segment2DQuadraturePoint;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2DUtils;

/**
 * 
 * @author epsilon
 */
public class LinearIntegratePointsFactory implements Factory<List<MFIntegratePoint>> {

    Segment2DQuadrature segment2DQuadrature;
    private Line lineForGaussianQuadrature;

    public LinearIntegratePointsFactory() {
        init_segment2DQuadrature();
    }

    private void init_segment2DQuadrature() {
        lineForGaussianQuadrature = new Line(new Node(new double[2]));
        Line succ = new Line(new Node(new double[2]));
        Segment2DUtils.link(lineForGaussianQuadrature, succ);

        segment2DQuadrature = new Segment2DQuadrature();
        segment2DQuadrature.setSegment(lineForGaussianQuadrature);
    }

    public void setStartCoord(double[] startCoord) {
        lineForGaussianQuadrature.getStart().setCoord(startCoord);
    }

    public void setEndCoord(double[] endCoord) {
        lineForGaussianQuadrature.getEnd().setCoord(endCoord);
    }

    public void setDegree(int degree) {
        segment2DQuadrature.setDegree(degree);
    }

    public int getNumOfPoints() {
        return segment2DQuadrature.getNumOfPoints();
    }

    public int getDegree() {
        return segment2DQuadrature.getDegree();
    }

    @Override
    public List<MFIntegratePoint> produce() {
        List<MFIntegratePoint> result = new LinkedList<>();
        for (Segment2DQuadraturePoint segPt : segment2DQuadrature) {
            result.add(genNewPoint(segPt));
        }
        return result;
    }

    private MFIntegratePoint genNewPoint(Segment2DQuadraturePoint segPt) {
        RawMFIntegratePoint point = new RawMFBoundaryIntegratePoint();
        point.setWeight(segPt.weight);
        point.setCoord(segPt.coord);
        return point;
    }
}
