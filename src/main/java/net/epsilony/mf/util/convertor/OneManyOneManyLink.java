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

import java.util.function.Function;

import com.google.common.collect.Iterables;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneManyOneManyLink<A, B, C> implements Function<A, Iterable<C>> {
    Function<? super A, ? extends Iterable<? extends B>> upper;
    OneOneToIterableOneOne<B, Iterable<? extends C>> iterableOneOneConvertor = new OneOneToIterableOneOne<>();

    @Override
    public Iterable<C> apply(A input) {
        return Iterables.concat(iterableOneOneConvertor.apply(upper.apply(input)));
    }

    public OneManyOneManyLink() {
    }

    public OneManyOneManyLink(Function<? super A, ? extends Iterable<? extends B>> upper,
            Function<? super B, ? extends Iterable<? extends C>> lower) {
        this.upper = upper;
        setLower(lower);
    }

    public Function<? super A, ? extends Iterable<? extends B>> getUpper() {
        return upper;
    }

    public void setUpper(Function<A, ? extends Iterable<? extends B>> upper) {
        this.upper = upper;
    }

    public Function<? super B, ? extends Iterable<? extends C>> getLower() {
        return iterableOneOneConvertor.getOneOneConvertor();
    }

    public void setLower(Function<? super B, ? extends Iterable<? extends C>> lower) {
        iterableOneOneConvertor.setOneOneConvertor(lower);
    }

}
