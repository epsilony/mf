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

import static net.epsilony.mf.util.event.EventBuses.types;

import java.util.ArrayList;
import java.util.Collection;

import net.epsilony.mf.util.convertor.Convertor;
import net.epsilony.mf.util.event.EventBus;
import net.epsilony.mf.util.event.MethodEventBus;
import net.epsilony.tb.pair.PairPack;
import net.epsilony.tb.pair.WithPair;
import net.epsilony.tb.rangesearch.LayeredRangeTree;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LRTreePickerBasedValuesEventBus<V> implements EventBus {
    MethodEventBus methodEventBus = new MethodEventBus();
    Convertor<? super V, double[]> coordPicker;

    public void post(Collection<? extends V> values) {
        ArrayList<WithPair<double[], V>> datas = genDatas(values);
        methodEventBus.post(datas);
    }

    public void postToNew(Collection<? extends V> values) {
        ArrayList<WithPair<double[], V>> datas = genDatas(values);
        methodEventBus.postToNew(datas);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void post(Object... values) {
        if (values.length != 1 || !(values[0] instanceof Collection)) {
            throw new IllegalArgumentException();
        }
        post((Collection<? extends V>) values[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void postToNew(Object... values) {
        if (values.length != 1 || !(values[0] instanceof Collection)) {
            throw new IllegalArgumentException();
        }
        postToNew((Collection<? extends V>) values[0]);
    }

    public ArrayList<WithPair<double[], V>> genDatas(Collection<? extends V> values) {
        ArrayList<WithPair<double[], V>> datas = new ArrayList<>(values.size());
        for (V v : values) {
            datas.add(new PairPack<double[], V>(coordPicker.convert(v), v));
        }
        return datas;
    }

    public LRTreePickerBasedValuesEventBus(Convertor<? super V, double[]> coordPicker) {
        this.coordPicker = coordPicker;
    }

    public LRTreePickerBasedValuesEventBus() {
    }

    public void registry(LayeredRangeTree<double[], V> lrTree) {
        methodEventBus.registry(lrTree, "setDatas", types(Collection.class));
        methodEventBus.registry(lrTree, "prepareTree", types());
    }

    public void remove(LayeredRangeTree<double[], V> lrTree) {
        methodEventBus.remove(lrTree, "setDatas", types(Collection.class));
        methodEventBus.remove(lrTree, "prepareTree", types());
    }

    public Convertor<? super V, double[]> getCoordPicker() {
        return coordPicker;
    }

    public void setCoordPicker(Convertor<? super V, double[]> coordPicker) {
        this.coordPicker = coordPicker;
    }

}
