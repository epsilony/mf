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
package net.epsilony.mf.model.influence;

import java.util.Collection;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class TwoDInfluenceRadiusProcessor implements Runnable {
    Collection<? extends MFNode> spaceNodes;
    Collection<? extends Segment> boundaries;
    InfluenceRadiusCalculator influenceRadiusCalculator;

    public Collection<? extends MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(Collection<? extends MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    public Collection<? extends Segment> getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(Collection<? extends Segment> boundaries) {
        this.boundaries = boundaries;
    }

    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public int getSpatialDimension() {
        return 2;
    }

    public void setSpatialDimension(int spatialDimension) {
        if (spatialDimension != 2) {
            throw new IllegalArgumentException();
        }
    }

    public void process() {
        for (MFNode nd : spaceNodes) {
            double rad = influenceRadiusCalculator.calcInflucenceRadius(nd.getCoord(), null);
            nd.setInfluenceRadius(rad);
        }

        if (null != boundaries) {
            for (Segment bnd : boundaries) {
                MFNode nd = (MFNode) bnd.getStart();
                double rad = influenceRadiusCalculator.calcInflucenceRadius(nd.getCoord(), bnd);
                nd.setInfluenceRadius(rad);
            }
        }
    }

    @Override
    public void run() {
        process();
    }
}
