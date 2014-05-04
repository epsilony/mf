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

import java.util.function.Function;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class FunctionIntegratorGroup<T, R> {
    private final Function<T, R> volume;
    private final Function<T, R> dirichlet;
    private final Function<T, R> neumann;

    public FunctionIntegratorGroup(Function<T, R> volume, Function<T, R> neumann, Function<T, R> dirichlet) {
        this.volume = volume;
        this.neumann = neumann;
        this.dirichlet = dirichlet;
    }

    public Function<T, R> getVolume() {
        return volume;
    }

    public Function<T, R> getDirichlet() {
        return dirichlet;
    }

    public Function<T, R> getNeumann() {
        return neumann;
    }

}
