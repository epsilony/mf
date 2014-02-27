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

import static net.epsilony.mf.util.event.EventBuses.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import net.epsilony.mf.integrate.integrator.AssemblerIntegrator;
import net.epsilony.mf.model.load.LoadValue;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.AssemblyInput;
import net.epsilony.mf.util.event.IndexicalMethodEventBus;
import net.epsilony.mf.util.event.MethodEventBus;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.RudeFactory;

import org.springframework.context.annotation.Bean;

/**
 * @author epsilon
 * 
 */
public class NeumannAssemblerConfig {
    @Resource
    protected int threadNum;

    @Resource
    protected MethodEventBus spatialDimensionEventBus;
    @Resource
    protected MethodEventBus valueDimensionEventBus;
    @Resource
    protected MethodEventBus allNodesNumEventBus;
    @Resource
    protected IndexicalMethodEventBus mainVectorFactoryEventBus;

    @Resource
    protected Class<?> neumannAssemblerClass;

    @Bean
    public List<AssemblerIntegrator<LoadValue>> neumannAssemblerIntegrators() {
        List<AssemblerIntegrator<LoadValue>> result = new ArrayList<>();
        Iterator<Assembler<AssemblyInput<LoadValue>>> asmIter = neumannAssemblers().iterator();
        for (int i = 0; i < threadNum; i++) {
            result.add(new AssemblerIntegrator<>(asmIter.next()));
        }
        return result;
    }

    @Bean
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Assembler<AssemblyInput<LoadValue>>> neumannAssemblers() {
        RudeFactory<Assembler<AssemblyInput<LoadValue>>> assemblerFactory = new RudeFactory(neumannAssemblerClass);
        List<Assembler<AssemblyInput<LoadValue>>> result = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            Assembler<AssemblyInput<LoadValue>> assembler = assemblerFactory.produce();
            mainVectorFactoryEventBus.registry(i, assembler, "mainVector", types(MFMatrix.class));
            allNodesNumEventBus.registry(assembler, "allNodesNum", types(int.class));
            spatialDimensionEventBus.registry(assembler, "spatialDimension", types(int.class));
            valueDimensionEventBus.registry(assembler, "valueDimension", types(int.class));
            result.add(assembler);
        }
        return result;
    }
}
