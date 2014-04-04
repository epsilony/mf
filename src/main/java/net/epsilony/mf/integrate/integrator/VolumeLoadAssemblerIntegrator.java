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
package net.epsilony.mf.integrate.integrator;

import java.util.function.Consumer;

import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblyInput;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class VolumeLoadAssemblerIntegrator implements Consumer<AssemblyInput> {
    Assembler volumeAssembler, volumeLoadAssembler;

    public Assembler getVolumeAssembler() {
        return volumeAssembler;
    }

    public void setVolumeAssembler(Assembler volumeAssembler) {
        this.volumeAssembler = volumeAssembler;
    }

    public Assembler getVolumeLoadAssembler() {
        return volumeLoadAssembler;
    }

    public void setVolumeLoadAssembler(Assembler volumeLoadAssembler) {
        this.volumeLoadAssembler = volumeLoadAssembler;
    }

    @Override
    public void accept(AssemblyInput unit) {
        volumeAssembler.setAssemblyInput(unit);
        volumeAssembler.assemble();
        if (unit.getLoadValue() != null) {
            volumeLoadAssembler.setAssemblyInput(unit);
            volumeLoadAssembler.assemble();
        }
    }

}
