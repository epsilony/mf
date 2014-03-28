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

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OneDInfluenceRadiusProcesser implements Runnable {
    InfluenceRadiusCalculator influenceRadiusCalculator;
    Collection<? extends MFNode> nodes;

    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public Collection<? extends MFNode> getNodes() {
        return nodes;
    }

    public void setNodes(Collection<? extends MFNode> nodes) {
        this.nodes = nodes;
    }

    public void process() {
        for (MFNode node : nodes) {
            double rad = influenceRadiusCalculator.calcInflucenceRadius(node.getCoord(), null);
            node.setInfluenceRadius(rad);
        }
    }

    @Override
    public void run() {
        process();
    }
}
