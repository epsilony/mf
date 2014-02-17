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

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneOneConvertedIterator<IN, OUT> implements Iterator<OUT> {

    Convertor<? super IN, ? extends OUT> convertor;
    Iterator<? extends IN> upstream;

    public Convertor<? super IN, ? extends OUT> getConvertor() {
        return convertor;
    }

    public void setConvertor(Convertor<? super IN, ? extends OUT> convertor) {
        this.convertor = convertor;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }

    @Override
    public OUT next() {
        return convertor.convert(upstream.next());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void setUpstream(Iterator<? extends IN> upstream) {
        this.upstream = upstream;
    }

    public OneOneConvertedIterator(Convertor<? super IN, ? extends OUT> convertor, Iterator<? extends IN> upstream) {
        this.convertor = convertor;
        this.upstream = upstream;
    }

    public OneOneConvertedIterator() {
    }

}
