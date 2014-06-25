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
import java.util.Random;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;
import net.epsilony.mf.model.search.MetricSearcher;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LRTreeSegmentsMetricSearcherConfigTest extends AbstractMetricSearcherConfigTest<MFLine> {
    Random       rand        = new Random(47);
    List<MFLine> allSegments = genAllSegments();

    private List<MFLine> genAllSegments() {
        int sampleSize = 100;
        double[] range = new double[] { -10, 10 };
        ArrayList<MFLine> result = new ArrayList<>(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
            result.add(newSingleLine(randsInRange(2, range), randsInRange(2, range)));
        }
        return result;
    }

    private MFLine newSingleLine(double[] start, double[] end) {
        MFLine result = new SimpMFLine();
        result.connectSucc(new SimpMFLine());
        result.setStart(new MFNode(start));
        result.setEnd(new MFNode(end));
        return result;
    }

    private double[] randsInRange(int size, double[] range) {
        double[] result = new double[size];
        for (int i = 0; i < result.length; i++) {
            result[i] = randInRange(range);
        }
        return result;
    }

    private double randInRange(double[] range) {
        double t = rand.nextDouble();
        return range[0] * (1 - t) + range[1];
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MetricSearcher<MFLine>> genActSearchers(int size) {
        @SuppressWarnings("resource")
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TwoDLRTreeSearcherConfig.class);

        SearcherBaseHub searcherBaseHub = applicationContext.getBean(SearcherBaseHub.class);

        ArrayList<MetricSearcher<MFLine>> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            results.add(searcherBaseHub.getBoundariesSearcherSupplier().get());
        }

        searcherBaseHub.setBoundaries(allSegments);
        searcherBaseHub.setSpatialDimension(2);
        searcherBaseHub.setModelInputed(true);
        return results;
    }

    @Override
    public MetricSearcher<MFLine> expSearcher() {
        return new MockSearcher();
    }

    public class MockSearcher implements MetricSearcher<MFLine> {

        private double[] center;
        private double   radius;

        @Override
        public void setCenter(double[] center) {
            this.center = center;
        }

        @Override
        public void setRadius(double radius) {
            this.radius = radius;
        }

        @Override
        public void search(Collection<? super MFLine> output) {
            output.clear();
            for (MFLine seg : allSegments) {
                if (MFLine2DUtils.distanceToChord(seg, center) <= radius) {
                    output.add(seg);
                }
            }
        }

    }

}
