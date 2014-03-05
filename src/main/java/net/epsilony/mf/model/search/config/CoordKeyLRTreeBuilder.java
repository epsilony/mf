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

import net.epsilony.mf.util.convertor.Convertor;
import net.epsilony.tb.DoubleArrayComparator;
import net.epsilony.tb.pair.PairPack;
import net.epsilony.tb.pair.WithPair;
import net.epsilony.tb.rangesearch.LayeredRangeTree;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class CoordKeyLRTreeBuilder<V> {
    LayeredRangeTree<double[], V> lrTree = new LayeredRangeTree<>();
    Convertor<? super V, double[]> coordPicker;

    public void setCoordPicker(Convertor<? super V, double[]> coordPicker) {
        this.coordPicker = coordPicker;
    }

    public void setDatas(Collection<? extends V> datas) {
        lrTree.setDatas(genDatas(datas));
    }

    public void setSpatialDimension(int spatialDimension) {
        lrTree.setComparators(DoubleArrayComparator.comparatorsForAll(spatialDimension));
    }

    public void prepareTree() {
        lrTree.prepareTree();
    }

    public LayeredRangeTree<double[], V> getLRTree() {
        return lrTree;
    }

    public ArrayList<WithPair<double[], V>> genDatas(Collection<? extends V> values) {
        ArrayList<WithPair<double[], V>> datas = new ArrayList<>(values.size());
        for (V v : values) {
            datas.add(new PairPack<double[], V>(coordPicker.convert(v), v));
        }
        return datas;
    }
}
