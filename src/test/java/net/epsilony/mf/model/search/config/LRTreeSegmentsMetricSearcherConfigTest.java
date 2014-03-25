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
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.search.MetricSearcher;
import net.epsilony.mf.util.event.GenericOneOffDispatcher;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LRTreeSegmentsMetricSearcherConfigTest extends AbstractMetricSearcherConfigTest<Segment> {
    Random rand = new Random();
    List<Segment> allSegments = genAllSegments();

    private List<Segment> genAllSegments() {
        int sampleSize = 100;
        double[] range = new double[] { -10, 10 };
        ArrayList<Segment> result = new ArrayList<>(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
            result.add(new SingleLine(randsInRange(2, range), randsInRange(2, range)));
        }
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
    public List<MetricSearcher<Segment>> genActSearchers(int size) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ModelBusConfig.class,
                TwoDBoundariesSearcherConfig.class, TwoDLRTreeBoundariesRangeSearcherConfig.class);
        ArrayList<MetricSearcher<Segment>> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            MetricSearcher<Segment> result = (MetricSearcher<Segment>) applicationContext
                    .getBean(SearcherBaseConfig.BOUNDARIES_SEARCHER_PROTO);
            results.add(result);
        }
        applicationContext.getBean(ModelBusConfig.BOUNDARIES_BUS, GenericOneOffDispatcher.class).postToNew(allSegments);
        applicationContext.getBean(ModelBusConfig.SPATIAL_DIMENSION_BUS, GenericOneOffDispatcher.class).postToNew(2);
        applicationContext.getBean(ModelBusConfig.MODEL_INPUTED_BUS, GenericOneOffDispatcher.class).postToNew("GOOD");
        return results;
    }

    @Override
    public MetricSearcher<Segment> expSearcher() {
        return new MockSearcher();
    }

    public static class SingleLine extends Line {

        public SingleLine(double[] start, double[] end) {
            setStart(new MFNode(start));
            setSucc(new Line(new MFNode(end)));
        }
    }

    public class MockSearcher implements MetricSearcher<Segment> {

        private double[] center;
        private double radius;

        @Override
        public void setCenter(double[] center) {
            this.center = center;
        }

        @Override
        public void setRadius(double radius) {
            this.radius = radius;
        }

        @Override
        public void search(Collection<? super Segment> output) {
            output.clear();
            for (Segment seg : allSegments) {
                if (Segment2DUtils.distanceToChord(seg, center) <= radius) {
                    output.add(seg);
                }
            }
        }

    }

}
