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
package net.epsilony.mf.model.search.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.search.MetricSearcher;
import net.epsilony.mf.util.function.RectangleToGridCoords;
import net.epsilony.mf.util.function.RectangleToGridCoords.ByNumRowsCols;
import net.epsilony.mf.util.math.VectorMath;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LRTreeNodesSearcherConfigTest extends AbstractMetricSearcherConfigTest<MFNode> {

    List<MFNode> sampleNodes = sampleNodes();

    List<MFNode> sampleNodes() {
        MFRectangle rect = new MFRectangle();
        rect.setDrul(new double[] { -4, 11, 6, 1 });
        int numRowCols = 11;
        ByNumRowsCols gridsByRowNums = new RectangleToGridCoords.ByNumRowsCols();
        gridsByRowNums.setNumCols(numRowCols);
        gridsByRowNums.setNumRows(numRowCols);
        ArrayList<ArrayList<double[]>> coordsGrid = gridsByRowNums.apply(rect);
        ArrayList<double[]> coords = Lists.newArrayList(Iterables.concat(coordsGrid));
        ArrayList<MFNode> nodes = new ArrayList<>(coords.size());
        for (double[] coord : coords) {
            nodes.add(new MFNode(coord));
        }
        return nodes;
    }

    @Override
    public MetricSearcher<MFNode> expSearcher() {
        SimpSearcher simpSearcher = new SimpSearcher(sampleNodes);
        return simpSearcher;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MetricSearcher<MFNode>> genActSearchers(int size) {
        @SuppressWarnings("resource")
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TwoDLRTreeSearcherConfig.class);
        ArrayList<MetricSearcher<MFNode>> result = new ArrayList<>(size);

        SearcherBaseHub searcherBaseHub = ac.getBean(SearcherBaseHub.class);

        for (int i = 0; i < size; i++) {
            result.add(searcherBaseHub.getNodesSearcherSupplier().get());
        }
        searcherBaseHub.setNodes(sampleNodes);
        searcherBaseHub.setSpatialDimension(2);
        searcherBaseHub.init();

        return result;
    }

    public static class SimpSearcher implements MetricSearcher<MFNode> {
        Collection<? extends MFNode> nodes;
        private double[]             center;
        private double               radius;

        public SimpSearcher(Collection<? extends MFNode> nodes) {
            this.nodes = nodes;
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
        public void search(Collection<? super MFNode> output) {
            output.clear();
            for (MFNode node : nodes) {
                if (VectorMath.distanceSquare(node.getCoord(), center) <= radius * radius) {
                    output.add(node);
                }
            }

        }

    }

}
