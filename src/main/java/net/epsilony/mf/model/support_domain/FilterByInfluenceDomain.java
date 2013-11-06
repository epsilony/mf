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

import java.util.Iterator;
import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class FilterByInfluenceDomain implements SupportDomainSearcher {

    SupportDomainSearcher upperSearcher;

    public FilterByInfluenceDomain(
            SupportDomainSearcher supportDomainSearcher) {
        this.upperSearcher = supportDomainSearcher;
    }

    @Override
    public SupportDomainData searchSupportDomain() {
        SupportDomainData result = upperSearcher.searchSupportDomain();
        filter(result);
        return result;
    }

    private void filter(SupportDomainData filterAim) {
        Iterator<MFNode> nodesIter = filterAim.allNodes.iterator();
        while (nodesIter.hasNext()) {
            MFNode node = nodesIter.next();
            double rad = node.getInfluenceRadius();
            if (rad <= Math2D.distance(node.getCoord(), getCenter())) {
                nodesIter.remove();
            }
        }
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + "{upper searcher: " + upperSearcher + "}";
    }

    @Override
    public void setCenter(double[] center) {
        upperSearcher.setCenter(center);
    }

    @Override
    public void setBoundary(GeomUnit bndOfCenter) {
        upperSearcher.setBoundary(bndOfCenter);
    }

    @Override
    public void setUnitOutNormal(double[] bndOutNormal) {
        upperSearcher.setUnitOutNormal(bndOutNormal);
    }

    @Override
    public void setRadius(double radius) {
        upperSearcher.setRadius(radius);
    }

    @Override
    public double[] getUnitOutNormal() {
        return upperSearcher.getUnitOutNormal();
    }

    @Override
    public GeomUnit getBoundary() {
        return upperSearcher.getBoundary();
    }

    @Override
    public double[] getCenter() {
        return upperSearcher.getCenter();
    }

    @Override
    public double getRadius() {
        return upperSearcher.getRadius();
    }
}
