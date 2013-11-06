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

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.model.search.SphereSearcher;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawSupportDomainSearcher implements SupportDomainSearcher {

    SphereSearcher<MFNode> nodesSearcher;
    SphereSearcher<Segment> segmentSearcher;
    double radius;
    double[] bndOutNormal;
    GeomUnit boundary;
    double[] center;

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public double[] getUnitOutNormal() {
        return bndOutNormal;
    }

    @Override
    public GeomUnit getBoundary() {
        return boundary;
    }

    @Override
    public double[] getCenter() {
        return center;
    }

    @Override
    public void setCenter(double[] center) {
        this.center = center;
    }

    @Override
    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public void setUnitOutNormal(double[] bndOutNormal) {
        this.bndOutNormal = bndOutNormal;
    }

    @Override
    public void setBoundary(GeomUnit boundary) {
        this.boundary = boundary;
    }

    public RawSupportDomainSearcher(SphereSearcher<MFNode> nodesSearcher, SphereSearcher<Segment> segmentSearcher) {
        this.nodesSearcher = nodesSearcher;
        this.segmentSearcher = segmentSearcher;
    }

    public RawSupportDomainSearcher(SphereSearcher<MFNode> nodesSearcher) {
        this(nodesSearcher, null);
    }

    @Override
    public SupportDomainData searchSupportDomain() {
        SupportDomainData result = new SupportDomainData();
        result.allNodes = nodesSearcher.searchInSphere(center, radius);
        if (null != segmentSearcher) {
            result.segments = segmentSearcher.searchInSphere(center, radius);
        }
        return result;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + "{" + "nodesSearcher="
                + nodesSearcher
                + ", segmentSearcher=" + segmentSearcher + '}';
    }
}
