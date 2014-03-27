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

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class IntegratorsGroup {

    Consumer<?> volume;
    Consumer<?> neumann;
    Consumer<?> dirichlet;
    List<?> volumeStack, neumannStack, dirichletStack;

    public Consumer<?> getVolume() {
        return volume;
    }

    public void setVolume(Consumer<?> volume) {
        this.volume = volume;
    }

    public Consumer<?> getNeumann() {
        return neumann;
    }

    public void setNeumann(Consumer<?> neumann) {
        this.neumann = neumann;
    }

    public Consumer<?> getDirichlet() {
        return dirichlet;
    }

    public void setDirichlet(Consumer<?> dirichlet) {
        this.dirichlet = dirichlet;
    }

    public List<?> getVolumeStack() {
        return volumeStack;
    }

    public void setVolumeStack(List<?> volumeStack) {
        this.volumeStack = volumeStack;
    }

    public List<?> getNeumannStack() {
        return neumannStack;
    }

    public void setNeumannStack(List<?> neumannStack) {
        this.neumannStack = neumannStack;
    }

    public List<?> getDirichletStack() {
        return dirichletStack;
    }

    public void setDirichletStack(List<?> dirichletStack) {
        this.dirichletStack = dirichletStack;
    }

}
