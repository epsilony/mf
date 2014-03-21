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

import java.util.Iterator;
import java.util.function.Function;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneOneConvertedIterable<B, C> implements Iterable<C> {

    Function<? super B, ? extends C> convertor;
    Iterable<? extends B> iterable;

    @Override
    public Iterator<C> iterator() {
        return new OneOneConvertedIterator<B, C>(convertor, iterable.iterator());
    }

    public OneOneConvertedIterable(Function<? super B, ? extends C> convertor, Iterable<? extends B> iterable) {
        this.convertor = convertor;
        this.iterable = iterable;
    }

    public OneOneConvertedIterable() {
    }

    public Function<? super B, ? extends C> getConvertor() {
        return convertor;
    }

    public void setConvertor(Function<? super B, ? extends C> convertor) {
        this.convertor = convertor;
    }

    public Iterable<? extends B> getIterable() {
        return iterable;
    }

    public void setIterable(Iterable<? extends B> iterable) {
        this.iterable = iterable;
    }

}
