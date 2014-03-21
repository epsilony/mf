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
public class OneOneConvertedIterator<IN, OUT> implements Iterator<OUT> {

    Function<? super IN, ? extends OUT> convertor;
    Iterator<? extends IN> upstream;

    public Function<? super IN, ? extends OUT> getConvertor() {
        return convertor;
    }

    public void setConvertor(Function<? super IN, ? extends OUT> convertor) {
        this.convertor = convertor;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }

    @Override
    public OUT next() {
        return convertor.apply(upstream.next());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void setUpstream(Iterator<? extends IN> upstream) {
        this.upstream = upstream;
    }

    public OneOneConvertedIterator(Function<? super IN, ? extends OUT> convertor, Iterator<? extends IN> upstream) {
        this.convertor = convertor;
        this.upstream = upstream;
    }

    public OneOneConvertedIterator() {
    }

}
