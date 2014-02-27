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
package net.epsilony.mf.model.search;

import java.util.ArrayList;
import java.util.Collection;

import net.epsilony.mf.util.ArrayListCache;
import net.epsilony.tb.rangesearch.RangeSearcher;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class RangeBasedMetricSearcher<V> extends AbstractMetricSearcher<V> {
    RangeGenerator rangeGenerator = null;
    MetricFilter<? super V> metricFilter = null;
    private RangeSearcher<double[], V> rangeSearcher;
    ArrayListCache<V> arrayListCache = new ArrayListCache<>();

    @Override
    public void search(Collection<? super V> output) {
        rangeGenerator.setCenter(center);
        rangeGenerator.setRadius(radius);

        ArrayList<V> arrayList = arrayListCache.get();
        rangeSearcher.rangeSearch(rangeGenerator.getFrom(), rangeGenerator.getTo(), arrayList);

        metricFilter.setCenter(center);
        metricFilter.setRadius(radius);
        output.clear();
        for (V v : arrayList) {
            if (metricFilter.isInside(v)) {
                output.add(v);
            }
        }
    }

    public RangeGenerator getRangeGenerator() {
        return rangeGenerator;
    }

    public void setRangeGenerator(RangeGenerator rangeGenerator) {
        this.rangeGenerator = rangeGenerator;
    }

    public MetricFilter<? super V> getMetricFilter() {
        return metricFilter;
    }

    public void setMetricFilter(MetricFilter<? super V> metricFilter) {

        this.metricFilter = metricFilter;
    }

    public RangeSearcher<double[], V> getRangeSearcher() {
        return rangeSearcher;
    }

    public void setRangeSearcher(RangeSearcher<double[], V> rangeSearcher) {
        this.rangeSearcher = rangeSearcher;
    }
}
