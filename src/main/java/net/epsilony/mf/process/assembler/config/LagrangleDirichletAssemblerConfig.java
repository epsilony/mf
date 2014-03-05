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

import java.util.List;

import javax.annotation.Resource;

import net.epsilony.mf.process.assembler.LagrangleDirichletAssembler;
import net.epsilony.mf.util.event.MethodEventBus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author epsilon
 * 
 */
@Configuration
@Import(DirichletAssemblerConfig.class)
public class LagrangleDirichletAssemblerConfig {

    @Resource
    MethodEventBus lagrangleNodesNumEventBus;

    @Resource
    List<?> dirichletAssemblers;

    @Bean
    public Class<LagrangleDirichletAssembler> dirichletAssemblerClass() {
        return LagrangleDirichletAssembler.class;
    }

    @Bean
    public boolean phonyRegistryAssemblerToLagrangleDataEventBus() {
        for (Object obj : dirichletAssemblers) {
            LagrangleDirichletAssembler lda = (LagrangleDirichletAssembler) obj;
            lagrangleNodesNumEventBus.registry(lda, "setLagrangleNodesNum", types(int.class));
        }
        return true;
    }

}
