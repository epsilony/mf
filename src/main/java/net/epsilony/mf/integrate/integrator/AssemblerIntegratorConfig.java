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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.process.assembler.ListAssembler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author epsilon
 * 
 */
@Configuration
public class AssemblerIntegratorConfig {

    @Resource
    int threadNum;
    @Resource
    List<?> volumeAssemblers;
    @Resource
    List<?> neumannAssemblers;
    @Resource
    List<?> dirichletAssemblers;

    @Bean
    public List<AssemblerIntegrator<LoadValue>> volumeAndLoadAssemblerIntegrators() {
        List<AssemblerIntegrator<LoadValue>> result = new ArrayList<>();
        Iterator<?> volIter = volumeAssemblers.iterator();
        Iterator<?> loadIter = neumannAssemblers.iterator();
        for (int i = 0; i < threadNum; i++) {
            @SuppressWarnings("unchecked")
            Assembler<AssemblyInput<LoadValue>> vol = (Assembler<AssemblyInput<LoadValue>>) volIter.next();
            @SuppressWarnings("unchecked")
            Assembler<AssemblyInput<LoadValue>> ld = (Assembler<AssemblyInput<LoadValue>>) loadIter.next();
            AssemblerIntegrator<LoadValue> assemblerIntegrator = new AssemblerIntegrator<LoadValue>(
                    new ListAssembler<>(Arrays.asList(vol, ld)));
            result.add(assemblerIntegrator);
        }
        return result;
    }

    @Bean
    public List<AssemblerIntegrator<LoadValue>> dirichletAssemblerIntegrators() {
        List<AssemblerIntegrator<LoadValue>> result = new ArrayList<>();
        Iterator<?> asmIter = dirichletAssemblers.iterator();
        for (int i = 0; i < threadNum; i++) {
            @SuppressWarnings("unchecked")
            Assembler<AssemblyInput<LoadValue>> asm = (Assembler<AssemblyInput<LoadValue>>) asmIter.next();

            result.add(new AssemblerIntegrator<LoadValue>(asm));
        }
        return result;
    }

    @Bean
    public List<AssemblerIntegrator<LoadValue>> neumannAssemblerIntegrators() {
        List<AssemblerIntegrator<LoadValue>> result = new ArrayList<>();
        Iterator<?> asmIter = neumannAssemblers.iterator();
        for (int i = 0; i < threadNum; i++) {
            @SuppressWarnings("unchecked")
            Assembler<AssemblyInput<LoadValue>> asm = (Assembler<AssemblyInput<LoadValue>>) asmIter.next();

            result.add(new AssemblerIntegrator<LoadValue>(asm));
        }
        return result;
    }
}
