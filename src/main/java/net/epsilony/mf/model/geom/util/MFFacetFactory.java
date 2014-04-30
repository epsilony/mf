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
package net.epsilony.mf.model.geom.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFFacetFactory {
    public MFFacetFactory() {

    }

    public MFFacetFactory(Supplier<? extends MFLine> lineFactory, Function<double[], ? extends Node> nodeFactory) {
        chainFactory.setLineFactory(lineFactory);
        chainFactory.setNodeFactory(nodeFactory);
    }

    private final MFLineChainFactory chainFactory = new MFLineChainFactory();
    {
        chainFactory.setClosed(true);
    }

    public void setLineFactory(Supplier<? extends MFLine> lineFactory) {
        chainFactory.setLineFactory(lineFactory);
    }

    public void setNodeFactory(Function<double[], ? extends Node> nodeFactory) {
        chainFactory.setNodeFactory(nodeFactory);
    }

    public <T> MFFacet produce(Iterable<? extends Iterable<T>> chainIterable, Function<? super T, double[]> coordGetter) {
        MFFacet facet = new MFFacet();
        for (Iterable<T> iter : chainIterable) {
            facet.getRingsHeads().add(chainFactory.produce(iter, coordGetter));
        }
        facet.requireWell();
        return facet;
    }

    public MFFacet produce(Iterable<? extends Iterable<double[]>> chainIterable) {
        return produce(chainIterable, Function.identity());
    }

    public MFFacet produce(double[][][] chainsCoord) {
        List<double[][]> list = Arrays.asList(chainsCoord);
        List<List<double[]>> listList = list.stream().map(Arrays::asList).collect(Collectors.toList());
        return produce(listList);
    }

    public MFFacet produceBySingleChain(Iterable<double[]> iter) {
        return produceBySingleChain(iter.iterator());
    }

    public MFFacet produceBySingleChain(Iterator<double[]> iter) {
        return produceBySingleChain(iter, Function.identity());
    }

    public <T> MFFacet produceBySingleChain(Iterable<T> iter, Function<? super T, double[]> coordGetter) {
        return produceBySingleChain(iter.iterator(), coordGetter);
    }

    public <T> MFFacet produceBySingleChain(Iterator<T> iter, Function<? super T, double[]> coordGetter) {
        MFFacet facet = new MFFacet();
        facet.getRingsHeads().add(chainFactory.produce(iter, coordGetter));
        facet.requireWell();
        return facet;
    }

}
