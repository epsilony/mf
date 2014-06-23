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
package net.epsilony.mf.util.bus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MethodSubBusBiConsumerInvoker implements BiConsumer<Object, Supplier<? extends Object>> {
    private Method method;

    public MethodSubBusBiConsumerInvoker(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 1 || Supplier.class.isAssignableFrom(parameters[0].getType()) == false) {
            throw new IllegalArgumentException();
        }
        this.method = method;
    }

    @Override
    public void accept(Object object, Supplier<? extends Object> supplier) {
        try {
            method.invoke(object, supplier);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }

    }

}