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

import java.util.Collection;
import java.util.Iterator;

import net.epsilony.mf.util.StreamIterator;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneManyConvertorIterator<IN, OUT> implements StreamIterator<IN, OUT> {
    Iterator<? extends IN> upstream;
    Convertor<? super IN, ? extends Collection<? extends OUT>> convertor;
    Iterator<? extends OUT> outerIterator;
    boolean prepared = false;

    @Override
    public void setUpstream(Iterator<? extends IN> upstream) {
        prepared = false;
        this.upstream = upstream;
    }

    public Convertor<? super IN, ? extends Collection<? extends OUT>> getConvertor() {
        return convertor;
    }

    public void setConvertor(Convertor<? super IN, ? extends Collection<? extends OUT>> convertor) {
        prepared = false;
        this.convertor = convertor;
    }

    private void prepare() {
        if (prepared) {
            return;
        }
        nextOuterIterator();
        prepared = true;
    }

    protected void nextOuterIterator() {
        while (upstream.hasNext()) {
            outerIterator = convertor.convert(upstream.next()).iterator();
            if (outerIterator.hasNext()) {
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        prepare();
        return (outerIterator != null && outerIterator.hasNext());
    }

    @Override
    public OUT next() {
        prepare();
        OUT res = outerIterator.next();
        if (!outerIterator.hasNext()) {
            nextOuterIterator();
        }
        return res;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
