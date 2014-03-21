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

import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblyInput;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class AssemblerIntegrator<T extends LoadValue> implements Consumer<AssemblyInput<T>> {

    Assembler<AssemblyInput<? extends T>> assembler;

    public AssemblerIntegrator() {
    }

    public AssemblerIntegrator(Assembler<AssemblyInput<? extends T>> assembler) {
        this.assembler = assembler;
    }

    public Assembler<AssemblyInput<? extends T>> getAssembler() {
        return assembler;
    }

    public void setAssembler(Assembler<AssemblyInput<? extends T>> assembler) {
        this.assembler = assembler;
    }

    @Override
    public void accept(AssemblyInput<T> assemblyInput) {
        assembler.setAssemblyInput(assemblyInput);
        assembler.assemble();
    }

}
