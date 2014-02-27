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
package net.epsilony.mf.util.convertor;

import com.google.common.collect.Iterables;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class IterableOutputConcator<A, C> implements Convertor<A, Iterable<C>> {
    Convertor<? super A, ? extends Iterable<? extends Iterable<? extends C>>> convertor;

    @Override
    public Iterable<C> convert(A input) {
        return Iterables.concat(convertor.convert(input));
    }

    public IterableOutputConcator(Convertor<? super A, ? extends Iterable<? extends Iterable<? extends C>>> convertor) {
        this.convertor = convertor;
    }

    public IterableOutputConcator() {
    }

    public Convertor<? super A, ? extends Iterable<? extends Iterable<? extends C>>> getConvertor() {
        return convertor;
    }

    public void setConvertor(Convertor<? super A, ? extends Iterable<? extends Iterable<? extends C>>> convertor) {
        this.convertor = convertor;
    }
}
