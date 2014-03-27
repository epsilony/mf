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
package net.epsilony.mf.process.assembler.config;

import java.util.Arrays;
import java.util.stream.Stream;

import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.util.matrix.MFMatrix;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class AssemblersGroup {
    private final Assembler volume, neumann, dirichlet;

    public AssemblersGroup(Assembler volume, Assembler neumann, Assembler dirichlet) {
        this.volume = volume;
        this.neumann = neumann;
        this.dirichlet = dirichlet;
    }

    private Stream<Assembler> stream() {
        return Arrays.asList(volume, neumann, dirichlet).stream();
    }

    public void setMainMatrix(MFMatrix matrix) {
        stream().forEach((asm) -> asm.setMainMatrix(matrix));
    }

    public void setMainVector(MFMatrix vector) {
        stream().forEach((asm) -> asm.setMainVector(vector));
    }

    public void setAllNodesNum(int num) {
        stream().forEach((asm) -> asm.setAllNodesNum(num));
    }

    public void setSpatialDimension(int dim) {
        stream().forEach((asm) -> asm.setSpatialDimension(dim));
    }

    public void setValueDimension(int dim) {
        stream().forEach((asm) -> asm.setValueDimension(dim));
    }

    public Assembler getVolume() {
        return volume;
    }

    public Assembler getNeumann() {
        return neumann;
    }

    public Assembler getDirichlet() {
        return dirichlet;
    }

}
