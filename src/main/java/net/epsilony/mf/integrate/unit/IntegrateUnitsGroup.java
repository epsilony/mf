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
package net.epsilony.mf.integrate.unit;

import java.util.List;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class IntegrateUnitsGroup {
    List<Object> dirichlet;
    List<Object> neumann;
    List<Object> volume;

    public List<Object> getDirichlet() {
        return dirichlet;
    }

    public void setDirichlet(List<Object> dirichlet) {
        this.dirichlet = dirichlet;
    }

    public List<Object> getNeumann() {
        return neumann;
    }

    public void setNeumann(List<Object> neumann) {
        this.neumann = neumann;
    }

    public List<Object> getVolume() {
        return volume;
    }

    public void setVolume(List<Object> volume) {
        this.volume = volume;
    }

}
