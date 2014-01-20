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

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class Convertors {
    public static <A, B> Iterable<B> convertOneManyAll(Convertor<? super A, ? extends Iterable<? extends B>> convertor,
            Iterable<? extends A> inputs) {
        ManyManyLinkedConvertor<Iterable<? extends A>, A, B> manyManyConvertor = new ManyManyLinkedConvertor<>(
                new IdentityConvertor<Iterable<? extends A>>(), convertor);
        return manyManyConvertor.convert(inputs);
    }

    public static <A, B> Iterable<B> convertOneOneAll(Convertor<? super A, ? extends B> convertor,
            Iterable<? extends A> inputs) {
        UpperManyLinkedConvertor<Iterable<? extends A>, A, B> upperManyLinkedConvertor = new UpperManyLinkedConvertor<>(
                new IdentityConvertor<Iterable<? extends A>>(), convertor);
        return upperManyLinkedConvertor.convert(inputs);
    }
}
