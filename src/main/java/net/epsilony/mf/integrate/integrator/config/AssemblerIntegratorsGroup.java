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

import net.epsilony.mf.integrate.integrator.AssemblerIntegrator;
import net.epsilony.mf.integrate.integrator.VolumeLoadAssemblerIntegrator;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class AssemblerIntegratorsGroup {
    private final VolumeLoadAssemblerIntegrator volume;
    private final AssemblerIntegrator neumann;
    private final AssemblerIntegrator dirichlet;

    public AssemblerIntegratorsGroup(VolumeLoadAssemblerIntegrator volume, AssemblerIntegrator neumann,
            AssemblerIntegrator dirichlet) {
        this.volume = volume;
        this.neumann = neumann;
        this.dirichlet = dirichlet;
    }

    public VolumeLoadAssemblerIntegrator getVolume() {
        return volume;
    }

    public AssemblerIntegrator getNeumann() {
        return neumann;
    }

    public AssemblerIntegrator getDirichlet() {
        return dirichlet;
    }

}
