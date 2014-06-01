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

import static org.apache.commons.math3.util.FastMath.floor;
import static org.apache.commons.math3.util.FastMath.min;
import static org.apache.commons.math3.util.MathArrays.distance;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFGeomUnit;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class EnsureNodesAutoSupportDomainSearcherTest {

    List<MFNode> nodes;

    @Test
    public void test() {
        double y = 1;
        double x0 = -5;
        double step = 1.5;
        int numNodes = 100;
        nodes = new ArrayList<>(numNodes);
        for (int i = 0; i < numNodes; i++) {
            nodes.add(new MFNode(new double[] { x0 + i * step, y }));
        }

        EnsureNodesAutoSupportDomainSearcher searcher = new EnsureNodesAutoSupportDomainSearcher();
        searcher.setSupportDomainSearcher(new SampleSupportDomainSearcher(nodes));
        searcher.setRadiusEstimator(new SampleEstimator());
        double[] center = new double[] { x0 - 1, y };
        searcher.setCenter(center);
        ArraySupportDomainData outputData = new ArraySupportDomainData();
        for (int i = 1; i < numNodes; i += 3) {
            searcher.setNodesNumLowerBound(i);
            searcher.search(outputData);

            Set<MFNode> actSet = new HashSet<>(outputData.getVisibleNodesContainer());

            int toIndex = (int) floor(((nodes.get(i - 1).getCoord()[0] - center[0]) * searcher.getResultEnlargeRatio() - (x0 - center[0]))
                    / step) + 1;
            toIndex = min(toIndex, numNodes);
            Set<MFNode> expSet = new HashSet<>(nodes.subList(0, toIndex));
            assertTrue(expSet.equals(actSet));
        }
    }

    public class SampleSupportDomainSearcher implements SupportDomainSearcher {

        private double[] center;
        private double radius;
        private Collection<MFNode> nodes;

        public SampleSupportDomainSearcher(List<MFNode> nodes) {
            this.nodes = nodes;
        }

        @Override
        public void setCenter(double[] center) {
            this.center = center;
        }

        @Override
        public void setBoundary(MFGeomUnit bndOfCenter) {
        }

        @Override
        public void setUnitOutNormal(double[] bndOutNormal) {
        }

        @Override
        public void setRadius(double radius) {
            this.radius = radius;

        }

        @Override
        public void search(SupportDomainData outputData) {
            outputData.getAllNodesContainer().clear();
            outputData.getVisibleNodesContainer().clear();
            List<MFNode> collect = nodes.stream().filter(nd -> {
                return distance(nd.getCoord(), center) <= radius;
            }).collect(Collectors.toList());
            outputData.getAllNodesContainer().addAll(collect);
            outputData.getVisibleNodesContainer().addAll(collect);
        }

        public Collection<MFNode> getNodes() {
            return nodes;
        }

        public void setNodes(Collection<MFNode> nodes) {
            this.nodes = nodes;
        }

    }

    public class SampleEstimator implements NodesNumRadiusEstimator {

        @Override
        public void setCenter(double[] center) {
        }

        @Override
        public void setNodesNumAim(int nodesNum) {
        }

        @Override
        public double estimate() {
            return 1;
        }

        @Override
        public void feedBack(double radius, int nodesNum) {
        }
    }

}
