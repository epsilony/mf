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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.epsilony.mf.model.search.MetricSearcher;

import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public abstract class AbstractMetricSearcherConfigTest<T> {

    /**
     * 
     */
    public AbstractMetricSearcherConfigTest() {
        super();
    }

    public abstract List<MetricSearcher<T>> genActSearchers(int size);

    public abstract MetricSearcher<T> expSearcher();

    @Test
    public void testSingleThread() {

        MetricSearcher<T> expSearcher = expSearcher();

        double[] center = new double[] { 6, 0.5 };
        double[] rads = new double[] { 0.5, 1, 1.5, 5, 13 };

        boolean tested = false;
        for (double rad : rads) {
            assertTwoSearcher(expSearcher, genActSearchers(1).get(0), center, rad);
            tested = true;
        }
        assertTrue(tested);
    }

    void assertTwoSearcher(MetricSearcher<T> expSearcher, MetricSearcher<T> actSearcher, double[] center, double radius) {
        assertNodesEquals(runSearcher(expSearcher, center, radius), runSearcher(actSearcher, center, radius));

    }

    ArrayList<T> runSearcher(MetricSearcher<T> searcher, double[] center, double radius) {
        searcher.setCenter(center);
        searcher.setRadius(radius);
        ArrayList<T> exps = new ArrayList<>();
        searcher.search(exps);
        return exps;
    }

    void assertNodesEquals(List<T> acts, List<T> exps) {
        Set<T> actsSet = new HashSet<>(acts);
        assertTrue(actsSet.size() == exps.size());
        for (T nd : exps) {
            assertTrue(actsSet.contains(nd));
        }
    }

    @Test
    public void testMultiThread() {
        // as all the range searcher is based on a single layered range tree and
        // other helper classes.
        int threadNum = 50;

        MetricSearcher<T> simpSearcher = expSearcher();
        double[] center = new double[] { 6, 0.5 };
        double[] rads = new double[] { 0.5, 1, 1.5, 5, 13 };

        boolean tested = false;
        for (double rad : rads) {
            assertMultiThreadSearchers(simpSearcher, genActSearchers(threadNum), center, rad, threadNum);
            tested = true;
        }
        assertTrue(tested);
    }

    private void assertMultiThreadSearchers(MetricSearcher<T> expSearcher, final List<MetricSearcher<T>> actSearchers,
            final double[] center, final double radius, int threadNum) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadNum);
        ArrayList<T> exps = runSearcher(expSearcher, center, radius);
        final ArrayList<List<T>> allActs = new ArrayList<>(threadNum);
        for (final MetricSearcher<T> actSearcher : actSearchers) {
            fixedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    List<T> acts = runSearcher(actSearcher, center, radius);
                    allActs.add(acts);
                }
            });
        }
        fixedThreadPool.shutdown();
        do {
            try {
                fixedThreadPool.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                fail();
            }
        } while (!fixedThreadPool.isTerminated());

        assertTrue(threadNum > 1);
        assertEquals(threadNum, allActs.size());
        for (List<T> acts : allActs) {
            assertNodesEquals(exps, acts);
        }

    }

}