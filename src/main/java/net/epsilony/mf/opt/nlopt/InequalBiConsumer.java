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
package net.epsilony.mf.opt.nlopt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class InequalBiConsumer implements BiConsumer<double[], double[][]> {

    private final ArrayList<BiConsumer<double[], double[][]>> biConsumers = new ArrayList<>();

    @Override
    public void accept(double[] values, double[][] gradients) {
        biConsumers.forEach(bi -> accept(values, gradients));
    }

    public boolean add(BiConsumer<double[], double[][]> e) {
        return biConsumers.add(e);
    }

    public void clear() {
        biConsumers.clear();
    }

    public boolean addAll(Collection<? extends BiConsumer<double[], double[][]>> c) {
        return biConsumers.addAll(c);
    }

}
