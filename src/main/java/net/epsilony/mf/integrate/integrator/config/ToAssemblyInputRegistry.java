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
package net.epsilony.mf.integrate.integrator.config;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import net.epsilony.mf.process.assembler.AssemblyInput;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public interface ToAssemblyInputRegistry {
    Map<Class<?>, Function<?, ? extends Stream<AssemblyInput>>> volume();

    Map<Class<?>, Function<?, ? extends Stream<AssemblyInput>>> neumann();

    Map<Class<?>, Function<?, ? extends Stream<AssemblyInput>>> dirichlet();

    default void putAll(ToAssemblyInputRegistry registry) {
        volume().putAll(registry.volume());
        neumann().putAll(registry.neumann());
        dirichlet().putAll(registry.dirichlet());
    }
}